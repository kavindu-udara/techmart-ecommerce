package com.techmart.config;

import com.techmart.entity.User;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
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
import java.util.logging.Logger;

@AdminOnly
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AdminAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(AdminAuthorizationFilter.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // ✅ CRITICAL FIX: Check if user is authenticated first
        // If not authenticated, abort immediately - don't proceed
        Long userId = (Long) requestContext.getProperty(AuthenticationFilter.AUTHENTICATED_USER_ID);

        if (userId == null) {
            logger.warning("❌ AdminAuthorizationFilter: User not authenticated. Aborting.");
            abortWithUnauthorized(requestContext, "Authentication required");
            return;
        }

        logger.info("🔍 AdminAuthorizationFilter - userId: " + userId);

        User user = em.find(User.class, userId);

        if (user == null) {
            logger.warning("❌ User not found in database for ID: " + userId);
            abortWithForbidden(requestContext, "User not found");
            return;
        }

        logger.info("🔍 User found: " + user.getEmail() + " with role: " + user.getRole());

        if (user.getRole() != User.UserRole.ADMIN) {
            logger.warning("❌ User " + user.getEmail() + " does not have ADMIN role.");
            abortWithForbidden(requestContext, "Admin access required");
            return;
        }

        logger.info("✅ User " + user.getEmail() + " has ADMIN role. Access granted.");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
