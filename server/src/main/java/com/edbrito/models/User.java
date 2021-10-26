package com.edbrito.models;

public class User {

	private String name;
	private String userName;
	
	public User() {	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public User(String name, String userName) {
		this.name = name;
		this.userName = userName;
	}
}
