package com.techmart.config;

import com.techmart.util.JwtUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.util.logging.Logger;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHENTICATED_USER_ID = "authenticatedUserId";

    @Inject
    private JwtUtil jwtUtil;

    @Context
    private HttpServletRequest httpRequest;
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            String userIdStr = jwtUtil.validateTokenAndGetUserId(token);
            Long userId = Long.parseLong(userIdStr);

            // ✅ Set BOTH: ContainerRequestContext property (for filters)
            // AND HttpServletRequest attribute (for resources)
            requestContext.setProperty(AUTHENTICATED_USER_ID, userId);
            httpRequest.setAttribute(AUTHENTICATED_USER_ID, userId);

            logger.info("✅ User " + userId + " authenticated successfully");

        } catch (Exception e) {
            logger.warning("❌ Token validation failed: " + e.getMessage());
            abortWithUnauthorized(requestContext, "Invalid or expired token");
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
