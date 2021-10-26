package com.edbrito.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities; 


@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class UserAuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Extract the token from the Authorization header
        String token = authorizationHeader
                            .substring(AUTHENTICATION_SCHEME.length()).trim();
        
        if(!TokenManager.validateToken(token))
        	abortWithUnauthorized(requestContext);

        final String username = TokenManager.getUsername(token); 
        final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
                return () -> username;
            }

            @Override
            public boolean isUserInRole(String role) {
                return true;
            }

            @Override
            public boolean isSecure() {
                return currentSecurityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return AUTHENTICATION_SCHEME;
            }
        });
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .build());
    }
    
}