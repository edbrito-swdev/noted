package com.edbrito.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.edbrito.Main;
import com.edbrito.models.Image;

public class ImageDAO {

	public boolean storeImage(String original, String stored, String owner) {
		boolean result = false;
		
	    Connection conn = null;
	    try {
	    	conn = DatabaseUtils.getConnection();
	    	String insertImgSQL = 
	    			"INSERT INTO images "+
	    			"(stored, original, username) "+
	    			"VALUES (?,?,?)";
	    	PreparedStatement insertImgStm = conn.prepareStatement(insertImgSQL);
	    	insertImgStm.setString(1, stored);
	    	insertImgStm.setString(2, original);
	    	insertImgStm.setString(3, owner);
	    	result = insertImgStm.executeUpdate() > 0;
	    } catch(SQLException e) {
	    	Main.log.error("Error storing image info in DB: "+e);
	    } finally {
	    	try {conn.close();}catch(Exception e){}
	    }
	    
	    return result;
	}

	public boolean isMine(String username, String storage) {
		String imgSQL = "SELECT * from images WHERE stored=? and username=?";
		boolean result = false;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement imgStm = conn.prepareStatement(imgSQL);
			imgStm.setString(1, storage);
			imgStm.setString(2, username);
			ResultSet rs = imgStm.executeQuery();
			result = rs.next();
		} catch(SQLException e) {
			Main.log.error("Couldn't validate image: "+e);
		} finally {
			try { conn.close(); } catch(Exception e) {}
		}
		
		return result;
	}

	public Image getImageInfo(String storage) {
		String imgSQL = "SELECT * from images WHERE stored=?";
		Image result = null;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement imgStm = conn.prepareStatement(imgSQL);
			imgStm.setString(1, storage);
			ResultSet rs = imgStm.executeQuery();
			if (rs.next()) {		
				result = new Image(rs.getString("stored"), rs.getString("original"), rs.getString("owner"));
			}
		} catch(SQLException e) {
			Main.log.error("Couldn't validate image: "+e);
		} finally {
			try { conn.close(); } catch(Exception e) {}
		}
		
		return result;
	}

	public List<String> getImageList(String username) {
		String imgSQL = "SELECT * from images WHERE username=?";
		List<String> result = null;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement imgStm = conn.prepareStatement(imgSQL);
			result = new LinkedList<String>();
			imgStm.setString(1, username);
			ResultSet rs = imgStm.executeQuery();
			while(rs.next()) {		
				result.add(rs.getString("stored"));
			}
		} catch(SQLException e) {
			Main.log.error("Couldn't validate image: "+e);
		} finally {
			try { conn.close(); } catch(Exception e) {}
		}
		
		return result;
	}
	
}
