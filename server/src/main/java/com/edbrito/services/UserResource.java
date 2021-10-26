package com.edbrito.services;

import java.io.InputStream;
import java.sql.SQLException;

import com.edbrito.Main;
import com.edbrito.auth.Secured;
import com.edbrito.auth.TokenManager;
import com.edbrito.database.UserDAO;
import com.edbrito.models.User;
import com.mysql.cj.xdevapi.JsonString;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserResource {

	@Context
	SecurityContext securityContext;

	@Context
	ContainerRequestContext ctx;
	
	@Path("/register")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerUser(InputStream entity) {
		JsonObject jObj = Json.createReader(entity).readObject();
	    String password = jObj.getString("password");
		
	    Zxcvbn zxcvbn = new Zxcvbn();
		Strength strength = zxcvbn.measure(password);
		if (strength.getScore()<2) {
			Main.log.info("Pass not strong enough");
			return Response.status(Status.PRECONDITION_FAILED).build();			
		}

		String username = jObj.getString("username");
		String name = jObj.getString("name");
		if(username == null || username.equals("")) {
			return Response.status(Status.UNAUTHORIZED).build();						
		}
		User u = new User(name, username);
		try {
			UserDAO ud = new UserDAO();
			boolean registered = ud.createUser(u, password);
			if (!registered) {
				return Response.status(Status.FORBIDDEN).build();
			}
		} catch(SQLException e) {
			Main.log.error("Can't register user: "+e);
		}
		return Response.ok(u).build();
	}

	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(InputStream entity) {
		JsonObject jObj = Json.createReader(entity).readObject();
	    String password = jObj.getString("password");
	    String username = jObj.getString("username");

		boolean validated = new UserDAO().login(username, password);
		
		if(!validated) 
			return Response.status(Status.UNAUTHORIZED).build();			
		
		String token = TokenManager.issueToken(username);
		JsonObject result = Json.createObjectBuilder().add("token", token).build();
		return Response.ok(result).build();	
	}

	@Path("/logout")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout() {
        String authorizationHeader =
                ctx.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Extract the token from the Authorization header
        String token = authorizationHeader
                            .substring("Bearer".length()).trim();

        if( TokenManager.revokeToken(token) )
        	return Response.ok().build();
        else
        	return Response.status(Status.UNAUTHORIZED).build();
	}
	
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserByUsername() {
		UserDAO ud = new UserDAO();
		String username = securityContext.getUserPrincipal().getName(); 
		return ud.getUserByUsername(username);
	}	
	
}
