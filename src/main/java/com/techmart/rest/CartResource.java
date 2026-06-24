package com.techmart.rest;

import com.techmart.config.Secured;
import com.techmart.controller.CartController;
import com.techmart.dto.AddToCartRequest;
import com.techmart.dto.CartResponse;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Stateless
@Path("/cart")
@Secured
public class CartResource {
    private static final Logger logger = Logger.getLogger(CartResource.class.getName());

    @EJB
    private CartController cartController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@Context HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            CartResponse cart = cartController.getCartResponse(userId);
            return Response.ok(cart).build();

        } catch (Exception e) {
            logger.severe("Error fetching cart: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToCart(AddToCartRequest addToCartRequest, @Context HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            if (addToCartRequest.getProductId() == null || addToCartRequest.getQuantity() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Product ID and quantity are required\"}").build();
            }

            if (addToCartRequest.getQuantity() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Quantity must be greater than 0\"}").build();
            }

            cartController.addItem(userId, addToCartRequest);
            CartResponse cart = cartController.getCartResponse(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Item added to cart");
            response.put("cart", cart);

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Error adding to cart: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @PUT
    @Path("/items/{itemId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartItem(@PathParam("itemId") Long itemId,
                                   Map<String, Integer> body,
                                   @Context HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            Integer quantity = body.get("quantity");
            if (quantity == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Quantity is required\"}").build();
            }

            cartController.updateItemQuantity(userId, itemId, quantity);
            CartResponse cart = cartController.getCartResponse(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart updated");
            response.put("cart", cart);

            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Error updating cart: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @DELETE
    @Path("/items/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFromCart(@PathParam("itemId") Long itemId,
                                   @Context HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            cartController.removeItem(userId, itemId);
            CartResponse cart = cartController.getCartResponse(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Item removed from cart");
            response.put("cart", cart);

            return Response.ok(response).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Error removing from cart: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCart(@Context HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("authenticatedUserId");
            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Not authenticated\"}").build();
            }

            cartController.clearCart(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cart cleared");

            return Response.ok(response).build();

        } catch (Exception e) {
            logger.severe("Error clearing cart: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }
}
