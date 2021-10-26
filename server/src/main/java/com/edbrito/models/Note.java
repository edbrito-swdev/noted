package com.edbrito.models;

public class Note {

	private String username;
	private String text;
	private int id;
	
	public Note() { }
	
	public Note(String text) {
		this.text = text;
	}
	
	public Note(int id, String text) {
		this.setId(id);
		this.setText(text);
	}
	
	public Note(String username, int id, String text) {
		this.setId(id);
		this.setText(text);
		this.setUsername(username);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String toString() {
		return "ID: "+id+", "+text;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}
