package com.techmart.rest;

import com.techmart.config.AdminOnly;
import com.techmart.config.Secured;
import com.techmart.controller.AdminController;
import com.techmart.dto.AdminDashboardResponse;
import com.techmart.dto.ProductRequest;
import com.techmart.entity.Order;
import com.techmart.entity.Product;
import com.techmart.entity.User;
import com.techmart.monitoring.Monitored;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
@Path("/admin")
@Secured
@Monitored
public class AdminResource {
    private static final Logger logger = Logger.getLogger(AdminResource.class.getName());

    @EJB
    private AdminController adminController;

    @GET
    @Path("/dashboard")
    @Produces(MediaType.APPLICATION_JSON)
    @Monitored
    public Response getDashboard() {
        try {
            AdminDashboardResponse stats = adminController.getDashboardStats();
            return Response.ok(stats).build();
        } catch (Exception e) {
            logger.severe("Error fetching dashboard stats: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        try {
            List<User> users = adminController.getAllUsers();

            // Map to safe DTOs (exclude password hash)
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("email", user.getEmail());
                map.put("role", user.getRole().name());
                map.put("createdAt", user.getCreatedAt());
                return map;
            }).collect(Collectors.toList());

            return Response.ok(userList).build();
        } catch (Exception e) {
            logger.severe("Error fetching users: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @GET
    @Path("/orders")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders() {
        try {
            List<Order> orders = adminController.getAllOrders();

            List<Map<String, Object>> orderList = orders.stream().map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("userId", order.getUser().getId());
                map.put("userEmail", order.getUser().getEmail());
                map.put("totalAmount", order.getTotalAmount());
                map.put("status", order.getStatus().name());
                map.put("createdAt", order.getCreatedAt());
                map.put("itemCount", order.getItems().size());
                return map;
            }).collect(Collectors.toList());

            return Response.ok(orderList).build();
        } catch (Exception e) {
            logger.severe("Error fetching orders: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @POST
    @Path("/products")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProduct(ProductRequest request) {
        try {
            if (request.getName() == null || request.getPrice() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Name and price are required\"}").build();
            }

            Product product = adminController.addProduct(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product added successfully");
            response.put("productId", product.getId());

            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            logger.severe("Error adding product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @PUT
    @Path("/products/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(@PathParam("id") Long id, ProductRequest request) {
        try {
            Product product = adminController.updateProduct(id, request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product updated successfully");
            response.put("productId", product.getId());

            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Error updating product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }

    @DELETE
    @Path("/products/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@PathParam("id") Long id) {
        try {
            adminController.deleteProduct(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Product deleted successfully");

            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Error deleting product: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }
}
