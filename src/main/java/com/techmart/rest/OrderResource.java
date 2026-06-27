package com.techmart.rest;

import com.techmart.config.AuthenticatedUser;
import com.techmart.config.AuthenticationFilter;
import com.techmart.config.Secured;
import com.techmart.controller.CartController;
import com.techmart.controller.OrderController;
import com.techmart.dto.OrderRequest;
import com.techmart.dto.OrderResponse;
import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.*;
import com.techmart.jms.OrderMessageProducer;

import com.techmart.monitoring.Monitored;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Stateless
@Path("/orders")
@Secured
@Monitored
public class OrderResource {
    private static final Logger logger = Logger.getLogger(OrderResource.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private OrderMessageProducer orderMessageProducer;

    @Inject
    private ProductCacheBean productCacheBean;

    @Inject
    private AuthenticatedUser authenticatedUser;

    @EJB
    private CartController cartController;

    @EJB
    private OrderController orderController;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(OrderRequest orderRequest) {
        try {
//    fetch user
            User user = em.find(User.class, orderRequest.getUserId());
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
            }

//    create order entity
            Order order = new Order();
            order.setUser(user);
            order.setStatus(Order.OrderStatus.PENDING);

//            add items and calculate total
            for (OrderRequest.OrderItemRequest itemRequest : orderRequest.getItems()) {

                // Fetch from Singleton Cache first for sub-second performance!
                Product product = productCacheBean.getProductById(itemRequest.getProductId());
                if (product == null) {
                    product = em.find(Product.class, itemRequest.getProductId());
                }

                if (product != null) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(itemRequest.getQuantity());
                    orderItem.setUnitPrice(product.getPrice());
                    order.addItem(orderItem);
                }

            }

            if (order.getItems().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No valid items in order").build();
            }

//                persist to the DB
            em.persist(order);
            logger.info("Order #" + order.getId() + " created. Total: $" + order.getTotalAmount());

            // Dispatch to JMS Queue for Asynchronous MDB Processing
            orderMessageProducer.sendOrderForProcessing(order.getId());

            // Return HTTP 202 Accepted immediately (Client doesn't wait for MDB to finish)
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("status", "PENDING");
            response.put("message", "Order received and queued for async processing.");

            return Response.status(Response.Status.ACCEPTED).entity(response).build();

        } catch (Exception e) {
            logger.severe("Error placing order: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/checkout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderFromCart(@Context HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute(AuthenticationFilter.AUTHENTICATED_USER_ID);

            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("{\"error\": \"Not authenticated\"}").build();
            }

            User user = em.find(User.class, userId);
            if (user == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"User not found\"}").build();
            }

            Cart cart = cartController.getCartEntity(userId);
            if (cart == null || cart.getItems().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\": \"Cart is empty\"}").build();
            }

            Order order = new Order();
            order.setUser(user);
            order.setStatus(Order.OrderStatus.PENDING);

            for (CartItem cartItem : cart.getItems()) {
                Product product = cartItem.getProduct();

                if (product.getStockQuantity() < cartItem.getQuantity()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"Insufficient stock for product: " + product.getName() + "\"}").build();
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(cartItem.getQuantity());

                orderItem.setUnitPrice(product.getPrice());

                order.addItem(orderItem);
            }
            em.persist(order);
            logger.info("Order #" + order.getId() + " created from cart. Total: $" + order.getTotalAmount());

            cartController.clearCart(userId);
            logger.info("Cart cleared for user ID: " + userId);

            orderMessageProducer.sendOrderForProcessing(order.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("totalAmount", order.getTotalAmount());
            response.put("status", "PENDING");
            response.put("message", "Order placed successfully and queued for processing.");

            return Response.status(Response.Status.ACCEPTED).entity(response).build();
        } catch (Exception e) {
            logger.severe("Error placing order from cart: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyOrders(@Context HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            List<Order> orders = orderController.getOrdersByUserId(userId);

            List<OrderResponse> responseList = orders.stream().map(order -> {
                // Map OrderItems
                List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
                    Product p = item.getProduct();
                    return new OrderResponse.OrderItemResponse(
                            p.getId(),
                            p.getName(),
                            p.getImageUrl(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal() // Uses the @Transient method in OrderItem entity
                    );
                }).collect(Collectors.toList());

                // Map Order
                return new OrderResponse(
                        order.getId(),
                        order.getTotalAmount(),
                        order.getStatus().name(), // Converts Enum to String (e.g., "PENDING")
                        order.getCreatedAt(),
                        itemResponses
                );
            }).collect(Collectors.toList());

            return Response.ok(responseList).build();

        } catch (Exception e) {
            logger.severe("Error fetching user orders: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

}
