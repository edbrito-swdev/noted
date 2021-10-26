package com.edbrito.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtils {

	private static String DBUSER = "ANUSR";
	private static String DBPASS = "ANPWD";
	private static String DBHOST = "ANHOST";
	private static String DBPORT = "ANPORT";
	
	public static Connection getConnection() throws SQLException {
	    Connection conn = null;
	    Properties connectionProps = new Properties();
	    String userName, password, host, port;
	    
	    //Getting the DB properties from the environment.
	    userName = System.getenv(DBUSER);
	    password = System.getenv(DBPASS);
	    host = System.getenv(DBHOST);
	    port = System.getenv(DBPORT);
	    
	    //Storing the connection properties
	    connectionProps.put("user", userName);
	    //connectionProps.put("password", password);

        conn = DriverManager.getConnection(
	                   "jdbc:mysql://" +
	                   host + ":" + port + "/" + "awesomenotes",
	                   connectionProps);
	    return conn;
	}	
}
