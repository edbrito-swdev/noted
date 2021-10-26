package com.edbrito.services;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.edbrito.Main;
import com.edbrito.auth.Secured;
import com.edbrito.database.DatabaseUtils;
import com.edbrito.database.ImageDAO;
import jakarta.json.JsonArray;

import java.util.List;

import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.SecurityContext;

@Path("/image")
public class ImageResource {

	@Context
	SecurityContext securityCtx;

	String UPLOAD_PATH = "c:/temp/";

	@Secured
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(
			@FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition fileMetaData  ) {
		String original = fileMetaData.getFileName();
		String storage = UUID.randomUUID().toString();
	    try
	    {
	        int read = 0;
	        byte[] bytes = new byte[1024];
	 
	        OutputStream out = new FileOutputStream(new File(UPLOAD_PATH + storage));
	        while ((read = fileInputStream.read(bytes)) != -1) 
	        {
	            out.write(bytes, 0, read);
	        }
	        out.flush();
	        out.close();
	    } catch (Exception e) {
	    	Main.log.error("Error uploading image: "+e);
	    	return Response.status(Status.NOT_MODIFIED).build();
	    }
	
	    ImageDAO id = new ImageDAO();
	    String username = securityCtx.getUserPrincipal().getName();
	    if (id.storeImage(original, storage, username))
	    	return Response.ok(storage).build();
	    else
	    	return Response.status(Status.NOT_MODIFIED).build();
	}
	
	@Secured
	@GET
	@Produces({"image/png", "image/jpg"})
	public Response getImage(@QueryParam("imgid") String imgid) {
		String username = securityCtx.getUserPrincipal().getName();
		ImageDAO imgd = new ImageDAO();
		BufferedImage image = null;
		
		if (!imgd.isMine(username, imgid))
			return Response.status(Status.UNAUTHORIZED).build();
		
		try {
			File imagefile = new File(UPLOAD_PATH + imgid);
			image = ImageIO.read(imagefile);	
		} catch(Exception e) {
			Main.log.error("Error retrieving image: "+e);
			return Response.noContent().build();
		}
		
		return Response.ok(image).build();
	}

	@Secured
	@Path("/base64")
	@GET
	@Produces({"image/png", "image/jpg"})
	public Response getImageAsBase64(@QueryParam("imgid") String imgid) {
		String username = securityCtx.getUserPrincipal().getName();
		ImageDAO imgd = new ImageDAO();
		String imageBase64 = null;
		
		if (!imgd.isMine(username, imgid))
			return Response.status(Status.UNAUTHORIZED).build();
		
		try {
			byte[] filecontents = FileUtils.readFileToByteArray(new File(UPLOAD_PATH + imgid));
			imageBase64 = Base64.getEncoder().encodeToString(filecontents);	
		} catch(Exception e) {
			Main.log.error("Error retrieving image: "+e);
			return Response.noContent().build();
		}
		
		return Response.ok(imageBase64).build();
	}

	@Secured
	@Path("/list")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImageList() {
		String username = securityCtx.getUserPrincipal().getName();
		ImageDAO imgd = new ImageDAO();
		List<String> image_ids = null;
		
		try {
			image_ids = imgd.getImageList(username);
		} catch(Exception e) {
			Main.log.error("Error retrieving image list: "+e);
			return Response.noContent().build();
		}

		return Response.ok(image_ids).build();		
	}
}
