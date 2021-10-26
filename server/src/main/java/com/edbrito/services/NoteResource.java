package com.edbrito.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.edbrito.Main;
import com.edbrito.auth.Secured;
import com.edbrito.database.ImageDAO;
import com.edbrito.database.NoteDAO;
import com.edbrito.models.Note;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

@Path("/note")
public class NoteResource {
	
	@Context
	SecurityContext securityContext;

	@Secured
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON) 
	public Response createNote(Note n) {
		//Create Note using the NoteDAO
		//If it's ok, return object with the ID
		String username = securityContext.getUserPrincipal().getName();
		NoteDAO nd = new NoteDAO();
		try {
			n.setUsername(username);
			int id = nd.createNote(n);
			n.setId(id);
			return Response.ok(n).build();
		} catch(Exception e) {
			Main.log.error("Note failed to create", e);
			return Response.status(Status.NOT_MODIFIED).build();
		}
	}

	@Secured
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editNote(@PathParam("id") int id, Note n) {
		NoteDAO nd = new NoteDAO();
		String username = securityContext.getUserPrincipal().getName();
		n.setId(id);
		
		if (!nd.isMine(id, username))
			return Response.status(Status.NOT_MODIFIED).build();
		
		if( !nd.editNote(n))
			return Response.status(Status.NOT_MODIFIED).build();
		
		return Response.ok(n).build();		
	}

	@Secured
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNote(@PathParam("id") String id) {
		NoteDAO nd = new NoteDAO();
		return nd.deleteNote(id);
	}

	@Secured
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNote(
			@PathParam("id") String id) {
		NoteDAO nd = new NoteDAO();
		return nd.getNote(id);
	}

	@Secured
	@GET
	@Path("/mine")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Note> getMine() {
		List<Note> result = null;
		NoteDAO nd = new NoteDAO();
		String username = securityContext.getUserPrincipal().getName();
		result = nd.getMine(username);
		return result;
	}

	@Secured
	@GET
	@Path("/shared")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Note> getShared() {
		List<Note> result = null;
		NoteDAO nd = new NoteDAO();
		String username = securityContext.getUserPrincipal().getName();
		System.out.println(username);
		result = nd.getShared(username);
		return result;
	}

	@Secured
	@POST
	@Path("/share")
	@Produces(MediaType.APPLICATION_JSON)
	public Response shareNote(
			@QueryParam("note") int noteId,
			@QueryParam("user") String user) {
		NoteDAO nd = new NoteDAO();
		String userName = securityContext.getUserPrincipal().getName();
		
		if ( !nd.isMine(noteId, userName) )
			return Response.status(Status.NOT_MODIFIED).build();
		
		if (!nd.share(noteId, user))
			return Response.status(Status.NOT_MODIFIED).build();

		return Response.ok().build();
	}
	
	@Secured
	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Note> getAll() {
		List<Note> all = getMine();
		List<Note> shared = getShared();
		all.addAll(shared);
		return all;
	}
	
	@Secured
	@POST
	@Path("/{noteid}/image/{imgid}")
	public Response addImageToNote(
			@PathParam("noteid") int noteid,
			@PathParam("imgid") String imgid) {
		ImageDAO imgd = new ImageDAO();
		String username = securityContext.getUserPrincipal().getName();

		if (!imgd.isMine(username, imgid))
			return Response.status(Status.NOT_MODIFIED).build();

		NoteDAO nd = new NoteDAO();
		if (!nd.isMine(noteid, username))
			return Response.status(Status.NOT_MODIFIED).build();
		
		if( !nd.addImage(noteid, imgid) )
			return Response.status(Status.NOT_MODIFIED).build();
			
		return Response.ok().build();
	}
}
