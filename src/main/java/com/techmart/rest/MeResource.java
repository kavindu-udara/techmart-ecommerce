package com.techmart.rest;

import com.techmart.config.AuthenticatedUser;
import com.techmart.config.AuthenticationFilter;
import com.techmart.config.Secured;
import com.techmart.controller.UserController;
import com.techmart.dto.UserResponse;
import com.techmart.entity.User;
import com.techmart.monitoring.Monitored;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@Stateless
@Path("/me")
@Secured
@Monitored
public class MeResource {

    private static final Logger logger = Logger.getLogger(MeResource.class.getName());

    @EJB
    private UserController userController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrentUser(@Context HttpServletRequest httpRequest) {
        try {
            // ✅ Read from HttpServletRequest attribute (set by filter)
            Long userId = (Long) httpRequest.getAttribute(AuthenticationFilter.AUTHENTICATED_USER_ID);

            if (userId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Authentication context missing\"}").build();
            }

            User user = userController.getUserById(userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"User not found\"}").build();
            }

            UserResponse response = new UserResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getCreatedAt()
            );

            return Response.ok(response).build();

        } catch (Exception e) {
            logger.severe("Error fetching current user: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Server error\"}").build();
        }
    }
}
