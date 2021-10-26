package com.edbrito.models;

public class Image {

	private String storage, original, owner;

	public Image(String storage, String original, String owner) {
		this.setOriginal(original);
		this.setStorage(storage);
		this.setOwner(owner);
	}
	
	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
