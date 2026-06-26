package com.techmart.config;

import com.techmart.entity.User;
import jakarta.annotation.Priority;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@AdminOnly
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AdminAuthorizationFilter implements ContainerRequestFilter {

    @PersistenceContext
    private EntityManager em;

    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Long userId = (Long) httpServletRequest.getAttribute("authenticatedUserId");

        if (userId == null) {
            abortWithForbidden(requestContext, "Not authenticated");
            return;
        }

        // Fetch user and check role
        User user = em.find(User.class, userId);
        if (user == null || user.getRole() != User.UserRole.ADMIN) {
            abortWithForbidden(requestContext, "Admin access required");
            return;
        }
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
