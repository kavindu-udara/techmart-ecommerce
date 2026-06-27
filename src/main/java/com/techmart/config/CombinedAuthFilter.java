package com.techmart.config;

import com.techmart.entity.User;
import com.techmart.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

@Secured
@AdminOnly
@Provider
@Priority(Priorities.AUTHENTICATION)
public class CombinedAuthFilter implements ContainerRequestFilter {
    private static final Logger logger = Logger.getLogger(CombinedAuthFilter.class.getName());
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private JwtUtil jwtUtil;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Step 1: Authentication
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();
        Long userId;

        try {
            String userIdStr = jwtUtil.validateTokenAndGetUserId(token);
            userId = Long.parseLong(userIdStr);
            logger.info("✅ User " + userId + " authenticated");
        } catch (Exception e) {
            logger.warning("❌ Token validation failed: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Invalid or expired token");
            return;
        }

        // Step 2: Check if this is an admin-only endpoint
        // We check if the resource method has @AdminOnly annotation
        if (isAdminEndpoint(requestContext)) {
            User user = em.find(User.class, userId);

            if (user == null) {
                abortWithForbidden(requestContext, "User not found");
                return;
            }

            if (user.getRole() != User.UserRole.ADMIN) {
                logger.warning("❌ User " + user.getEmail() + " is not an admin");
                abortWithForbidden(requestContext, "Admin access required");
                return;
            }

            logger.info("✅ User " + user.getEmail() + " has ADMIN role");
        }

        // Store userId for resource methods
        requestContext.setProperty("authenticatedUserId", userId);
    }

    private boolean isAdminEndpoint(ContainerRequestContext requestContext) {
        // Check if the request path contains /admin
        String path = requestContext.getUriInfo().getPath();
        return path.startsWith("admin");
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
