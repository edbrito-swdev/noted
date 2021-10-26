package com.edbrito.auth;

import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.edbrito.Main;
import com.edbrito.common.RedisHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TokenManager {

	//Needed for JWT
	//Should use PKI for it (instead of HMAC). Not implementing it for now.
/*	private static String secret = System.getenv("JWTSECRET");
	private static String issuer = System.getenv("JWTISSUER");
	private static Algorithm algorithm = Algorithm.HMAC256(secret);
	private static Builder jwtc = JWT.create().withIssuer("com.edbrito"); 
	
	private static JWTVerifier jwtv = 
			JWT.require(algorithm)
	        .withIssuer("com.edbrito")
	        .build();*/

	public static String issueToken(String userId) {
		String value = null;
		  
		value = UUID.randomUUID().toString();
		
		if( RedisHelper.save(value, userId) )
			return value;

		return value;
	}

	public static boolean validateToken(String token) {
		String id = RedisHelper.get(token);
		return id != null;
	}

	public static String getUsername(String token) {
		return RedisHelper.get(token);
	}
	
	public static boolean revokeToken(String token) {
		return RedisHelper.remove(token);
	}
/*	public static String issueJWT(String userId) throws JWTCreationException {
		String value = "";		
		
		value = jwtc.sign(algorithm);
		//Store in Redis
		//For now no storing (and no revoking)
		
		return value;
	}

	public static boolean verifyJWT(String token) throws JWTVerificationException {
	    DecodedJWT jwt = jwtv.verify(token);
	    return true;
	}
	
	public static boolean revokeToken(String token) {
		//Revoke JWT token
		//For now, ignore
		return true;
	}

	public static boolean validateToken(String JWToken) {
		return true;
	}*/
}
