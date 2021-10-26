package com.edbrito.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.edbrito.Main;
import com.edbrito.models.Note;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;

public class NoteDAO {

	public int createNote(Note n) throws SQLException {
		String createNote = "INSERT INTO notes (username, content) VALUES (?, ?)";
		String lastID = "SELECT LAST_INSERT_ID();";
		int insertedID = -1;
		Connection conn = DatabaseUtils.getConnection();
		try {
			PreparedStatement createNoteStm = conn.prepareStatement(createNote);
			createNoteStm.setString(1, n.getUsername());
			createNoteStm.setString(2, n.getText());
			createNoteStm.executeUpdate();
			PreparedStatement insertedIDStm = conn.prepareStatement(lastID);
			ResultSet idRS = insertedIDStm.executeQuery();
			
			if (idRS.next()) {
				insertedID = idRS.getInt(1);
			}			
		} finally {
			if(conn!=null)
				conn.close();
		}
		return insertedID;
	}
	
	public List<Note> getMine(String username) {
		String getNotesSQL = "SELECT * FROM notes where username=?";
		ArrayList<Note> notes = new ArrayList<Note>();
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement getNotesStm = conn.prepareStatement(getNotesSQL);
			getNotesStm.setString(1, username);
			ResultSet notesRS = getNotesStm.executeQuery();
			
			while(notesRS.next()) {
				int noteId = notesRS.getInt("id");
				String noteContent = notesRS.getString("content");
				Note n = new Note(username, noteId, noteContent);
				notes.add(n);
			}
		} catch(SQLException e) {
			//Log to errors
		} finally {
			if(conn!=null) {
				try {
					conn.close();
				} catch(Exception e) {}
			}
		}
		
		return notes;
	}

	public List<Note> getShared(String username) {
		String getSharedNotesSQL = 
				"SELECT notes.id as id, notes.username as username, notes.content as content "+
				"FROM notes, sharednotes "+ 
				"where notes.id = sharednotes.noteid " +
				"AND sharednotes.username = ?";
		ArrayList<Note> notes = new ArrayList<Note>();
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement getSharedNotesStm = conn.prepareStatement(getSharedNotesSQL);
			getSharedNotesStm.setString(1, username);
			System.out.println(getSharedNotesStm);
			ResultSet notesRS = getSharedNotesStm.executeQuery();
			
			while(notesRS.next()) {
				System.out.println(notesRS.getRow());
				int noteId = notesRS.getInt("id");
				String noteContent = notesRS.getString("content");
				String owner = notesRS.getString("username");
				Note n = new Note(owner, noteId, noteContent);
				notes.add(n);
			}
		} catch(SQLException e) {
			//Log to errors
		} finally {
			if(conn!=null) {
				try {
					conn.close();
				} catch(Exception e) {}
			}
		}
		
		return notes;
	}
	
	public List<Note> getAll(String username) {
		String getNotesSQL = "SELECT * FROM notes where username=?";
		ArrayList<Note> notes = new ArrayList<Note>();
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement getNotesStm = conn.prepareStatement(getNotesSQL);
			getNotesStm.setString(1, username);
			ResultSet notesRS = getNotesStm.executeQuery();
			
			while(notesRS.next()) {
				int noteId = notesRS.getInt("id");
				String noteContent = notesRS.getString("content");
				Note n = new Note(username, noteId, noteContent);
				notes.add(n);
			}
		} catch(SQLException e) {
			//Log to errors
		} finally {
			if(conn!=null) {
				try {
					conn.close();
				} catch(Exception e) {}
			}
		}
		
		return notes;
	}
	
	public Response deleteNote(String id) {
		Response r = null;
		int result = 0;
		String deleteNoteSQL = "Delete from notes WHERE id=?";
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement deleteNoteStm = conn.prepareStatement(deleteNoteSQL);
			deleteNoteStm.setString(1, id);
			result = deleteNoteStm.executeUpdate();
			if(result > 0) {
				r = Response.ok().build();
			} else {
				r = Response.notModified().build();
			}
		} catch(SQLException e) {
			r = Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			try {
				conn.close();
			} catch(Exception e) {
				r = Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		return r;
	}

	public Response getNote(String id) {
		Note n = null;
		String getNoteSQL = "SELECT * from notes WHERE id=?";
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement getNoteStm = conn.prepareStatement(getNoteSQL);
			getNoteStm.setString(1, id);
			ResultSet rs = getNoteStm.executeQuery();
			if (rs.next()) {
				n = new Note(rs.getInt("id"), rs.getString("content"));				
			}
		} catch(SQLException e) {
			//Log error 
		} finally {
			try {
				conn.close();
			} catch(Exception e) {}
		}
		if(n!=null)
			return Response.ok(n).build();
		else
			return Response.status(Response.Status.NOT_FOUND).build();
	}

	public boolean editNote(Note n) {
		String updateSQL = "UPDATE notes SET content = ? WHERE notes.id=?";
		Connection conn = null;
		boolean result = false;
		
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement updateNoteStm = conn.prepareStatement(updateSQL);
			updateNoteStm.setString(1, n.getText());
			updateNoteStm.setInt(2, n.getId());
			updateNoteStm.executeUpdate();
			result = true;
		} catch(SQLException e) {
			Main.log.error("Couldn't edit note: "+e);
		} finally {
			try {conn.close();}catch(Exception e) {}
		}
		return result;
	}
	
	public boolean isMine(int noteid, String username) {
		String getNoteSQL = "SELECT * from notes WHERE id=? and username=?";
		boolean result = false;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement getNoteStm = conn.prepareStatement(getNoteSQL);
			getNoteStm.setInt(1, noteid);
			getNoteStm.setString(2, username);
			ResultSet rs = getNoteStm.executeQuery();
			result = rs.next();
		} catch(SQLException e) {
			Main.log.error("Couldn't validate note");
		} finally {
			try { conn.close(); } catch(Exception e) {}
		}
		
		return result;
	}

	public boolean addImage(int noteid, String imgid) {
		String imgnotesSQL = "INSERT INTO imagenotes (imageid, noteid) VALUES (?, ?)";
		boolean result = false;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement imgnotesStm = conn.prepareStatement(imgnotesSQL);
			imgnotesStm.setString(1, imgid);
			imgnotesStm.setInt(2, noteid);
			imgnotesStm.executeUpdate();
			result = true;
		} catch(SQLException e){
			Main.log.error("Error adding image to note: "+e);
		} finally {
			try{conn.close();}catch(Exception e) {}
		}
		return result;				
	}

	public boolean share(int noteId, String user) {
		String shareSQL = "INSERT INTO sharednotes (username, noteid) VALUES (?, ?)";
		boolean result = false;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement shareStm = conn.prepareStatement(shareSQL);
			shareStm.setString(1, user);
			shareStm.setInt(2, noteId);
			shareStm.executeUpdate();
			result = true;
		} catch(SQLException e){
			Main.log.error("Error sharing note: "+e);
		} finally {
			try{conn.close();}catch(Exception e) {}
		}
		return result;		
	}
}
