package com.techmart.rest;

import com.techmart.dto.RegisterRequest;
import com.techmart.entity.User;
import com.techmart.util.PasswordHandler;
import com.techmart.util.Validators;
import jakarta.ejb.Stateless;
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

@Stateless
@Path("/register")
public class RegisterResource {
    private static final Logger logger = Logger.getLogger(RegisterResource.class.getName());

    @PersistenceContext
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest registerRequest) {
        try {

            if(!Validators.isValidEmail(registerRequest.getEmail())){
                logger.warning("Email is invalid.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid email type").build();
            }

//            check email is in correct type
            if (!Validators.isValidEmail(registerRequest.getEmail())) {
                logger.warning("Email is invalid.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Invalid email type").build();
            }

//            check is user already exists
            User existingUser = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", registerRequest.getEmail())
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (existingUser != null) {
                logger.warning("User with the email already exist.");
                return Response.status(Response.Status.BAD_REQUEST).entity("User already exist").build();
            }

            String password = registerRequest.getPassword();
            if (password == null) {
                logger.warning("Password is required.");
                return Response.status(Response.Status.BAD_REQUEST).entity("Password is required.").build();
            }

//            hash the password
            String hashedPassword = PasswordHandler.hashPassword(password);
            logger.info("Hashed used password successful.");

//            saved user in db
            User newUser = new User();
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPasswordHash(hashedPassword);

            em.persist(newUser);
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
