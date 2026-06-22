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

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    @Inject
    private JwtUtil jwtUtil;

    @Context
    private HttpServletRequest httpRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
//        get the Authorization header
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

//        check if header exist and start with "Bearer "
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abortWithUnauthorized(requestContext, "Missing or invalid Authorization header");
            return;
        }

//        extract the token
        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        try {
            String userIdString = jwtUtil.validateTokenAndGetUserId(token);

//            store user id in the request context
            httpRequest.setAttribute("authenticatedUserId", Long.parseLong(userIdString));
        } catch (Exception e) {
            abortWithUnauthorized(requestContext, "Invalid or expired token.");
        }

    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + message + "\"}")
                .build());
    }
}
