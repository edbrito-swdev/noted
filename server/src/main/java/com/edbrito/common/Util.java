package com.edbrito.common;

import java.util.UUID;

public class Util {
	
	public static String generateRandomName() {
		return UUID.randomUUID().toString();
	}
	
}
