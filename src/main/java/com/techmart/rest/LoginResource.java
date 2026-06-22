package com.techmart.rest;

import com.techmart.controller.UserController;
import com.techmart.dto.LoginRequest;
import com.techmart.entity.User;
import com.techmart.util.JwtUtil;
import com.techmart.util.PasswordHandler;
import com.techmart.util.Validators;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/login")
public class LoginResource {
    private static Logger logger = Logger.getLogger(LoginResource.class.getName());

    @PersistenceContext
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            if (!Validators.isValidEmail(loginRequest.getEmail())) {
                logger.warning("Email is invalid");
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid email type").build();
            }

//            check user by email
            UserController userController = new UserController();
            User user = userController.getUserByEmail(loginRequest.getEmail());
            if (user == null) {
                logger.warning("User not found for email : " + loginRequest.getEmail());
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }

//            check the password
            if (!PasswordHandler.verifyPassword(loginRequest.getPassword(), user.getPasswordHash())) {
                logger.warning("Password is invalid.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Password is invalid").build();
            }

//            generate JWT token
            String token = JwtUtil.generateToken(user.getId(), user.getEmail());

//            return token to client
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("userEmail", user.getEmail());

            logger.info("Login successful, user ID : " + user.getId());
            return Response.ok(response).build();

        } catch (Exception e) {
            logger.severe("Login Error : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error : " + e.getMessage()).build();
        }
    }

}
