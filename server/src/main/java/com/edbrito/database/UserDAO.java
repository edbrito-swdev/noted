package com.edbrito.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.edbrito.Main;
import com.edbrito.models.User;

public class UserDAO {

	public boolean createUser(User u, String password) throws SQLException {
		String finduserSQL = "SELECT * from users WHERE username=?";
		String createUserSQL = "INSERT INTO users (username, name, salt, password) VALUES (?,?,?,PASSWORD(?))";
		boolean result = false;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement getUserStm = conn.prepareStatement(finduserSQL);
			getUserStm.setString(1, u.getUserName());
			ResultSet userFound = getUserStm.executeQuery();
	
			if(!userFound.next()) {
				PreparedStatement createUserStm = conn.prepareStatement(createUserSQL);
				createUserStm.setString(1, u.getUserName());
				createUserStm.setString(2, u.getName());
				//Not implementing salt for now
				createUserStm.setString(3, "");
				createUserStm.setString(4, password);
				int changes = createUserStm.executeUpdate();
				result = changes > 0;
			}
		} finally {
			conn.close();
		}
		
		return result; 
	}
	
	public boolean login(String username, String password) {
		String loginSQL = "SELECT * from users WHERE username=? and PASSWORD(?)=password";
		boolean result = false;
		
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement loginStm = conn.prepareStatement(loginSQL);
			loginStm.setString(1, username);
			loginStm.setString(2, password);
			System.out.println(loginStm.toString());
			ResultSet loginFound = loginStm.executeQuery();
	
			result = loginFound.next();
		} catch(SQLException e) {
			Main.log.error("Error during login: "+e);
		} finally {
			try { conn.close(); }
			catch(Exception e) {}
		}
		
		return result;
	}

	public User getUser(String id) {
		String finduserSQL = "SELECT * from users WHERE id=?";
		User u = null;
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement userStm = conn.prepareStatement(finduserSQL);
			userStm.setString(1, id);
			ResultSet rs = userStm.executeQuery();
			if (rs.next()) {
				u = new User(rs.getString("name"), rs.getString("username"));
			}
		} catch(SQLException e) {
			Main.log.error("Error fetching user: "+e);
		} finally {
			try {conn.close();} catch(Exception e) {}
		}
		return u;
	}

	public User getUserByUsername(String username) {
		String finduserSQL = "SELECT * from users WHERE username=?";
		User u = null;
		Connection conn = null;
		try {
			conn = DatabaseUtils.getConnection();
			PreparedStatement userStm = conn.prepareStatement(finduserSQL);
			userStm.setString(1, username);
			ResultSet rs = userStm.executeQuery();
			if (rs.next()) {
				u = new User(rs.getString("name"), rs.getString("username"));
			}
		} catch(SQLException e) {
			Main.log.error("Error fetching user: "+e);
		} finally {
			try {conn.close();} catch(Exception e) {}
		}
		return u;
	}
}
