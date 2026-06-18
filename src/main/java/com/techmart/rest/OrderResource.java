package com.techmart.rest;

import com.techmart.ejb.ProductCacheBean;
import com.techmart.entity.Order;
import com.techmart.entity.OrderItem;
import com.techmart.entity.Product;
import com.techmart.entity.User;
import com.techmart.jms.OrderMessageProducer;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/orders")
public class OrderResource {
    private static final Logger logger = Logger.getLogger(OrderResource.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private OrderMessageProducer orderMessageProducer;

    @Inject
    private ProductCacheBean productCacheBean;

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
}
