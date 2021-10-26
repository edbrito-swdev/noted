package com.edbrito.auth;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@PreMatching
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
//		String xAuth = requestContext.getHeaderString("x-auth");
//
//	    if (xAuth == null || xAuth.isEmpty()) {
//	      requestContext
//	      	.abortWith(Response.status(Response.Status.UNAUTHORIZED)
//	      	.build());
//	      return;
//	    }
	}

}
