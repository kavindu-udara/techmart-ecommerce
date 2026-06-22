package com.techmart.rest;

import com.techmart.config.Secured;
import com.techmart.controller.UserController;
import com.techmart.dto.UserResponse;
import com.techmart.entity.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@Stateless
@Path("/me")
@Secured
public class MeResource {
    private static final Logger logger = Logger.getLogger(MeResource.class.getName());

    @EJB
    private UserController userController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest request) {
        try {
//            get the user id
            Long userId = (Long) request.getAttribute("authenticatedUserId");

            if (userId == null) {
                logger.warning("Access to /me without valid authentication context.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Authentication context missing\"}")
                        .build();
            }

//            get user from db
            User user = userController.getUserById(userId);
            if (user == null) {
                logger.warning("Authenticated user ID " + userId + " not found in database.");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"User not found\"}")
                        .build();
            }

//            map to safe dto
            UserResponse response = new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt());

            return Response.ok(response).build();

        } catch (Exception e) {
            logger.severe("Error fetching current user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}")
                    .build();
        }
    }


}
