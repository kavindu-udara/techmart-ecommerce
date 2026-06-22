package com.techmart.rest;

import com.techmart.controller.UserController;
import com.techmart.dto.RegisterRequest;
import com.techmart.entity.User;
import com.techmart.util.Validators;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Stateless
@Path("/register")
public class RegisterResource {
    private static final Logger logger = Logger.getLogger(RegisterResource.class.getName());

    @EJB
    private UserController userController;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest registerRequest) {
        try {

//            check email is in correct type
            if (!Validators.isValidEmail(registerRequest.getEmail())) {
                logger.warning("Email is invalid.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid email type").build();
            }

//            check is user already exists
            User existingUser = userController.getUserByEmail(registerRequest.getEmail());

            if (existingUser != null) {
                logger.warning("User with the email already exist.");
                return Response.status(Response.Status.BAD_REQUEST).entity("User already exist").build();
            }

            String password = registerRequest.getPassword();
            if (password == null) {
                logger.warning("Password is required.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Password is required.").build();
            }

            userController.createNewUser(registerRequest);
            logger.info("New user created success.");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");

            return Response.status(Response.Status.CREATED).entity(response).build();

        } catch (Exception e) {
            logger.severe("Error creating user : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
        }
    }

}
