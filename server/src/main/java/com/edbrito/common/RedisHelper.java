package com.edbrito.common;

import com.edbrito.Main;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisHelper {

	private static String host = System.getenv("REDISHOST");
	private static int port = Integer.parseInt(System.getenv("REDISPORT"));
	
	public static boolean save(String key, String value) {
		JedisPool jedisPool = new JedisPool(host, port);
		boolean result = false;
		
		try (Jedis jedis = jedisPool.getResource()) {
			jedis.set(key, value);
			result = true;
		} catch(Exception e) {
			Main.log.error("Problem with Redis: "+e);			
		} finally {
			jedisPool.close();
		}
		return result;
	}

	public static String get(String key) {
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
		String result = null;
		
		try (Jedis jedis = jedisPool.getResource()) {
			result = jedis.get(key);
		} catch(Exception e) {
			Main.log.error("Problem with Redis: "+e);
		} finally {
			jedisPool.close();
		}
		return result;
	}

	public static boolean remove(String key) {
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
		boolean result = false;
		
		try (Jedis jedis = jedisPool.getResource()) {
			result = jedis.del(key) > 0;
		} catch(Exception e) {
			Main.log.error("Problem with Redis: "+e);
		} finally {
			jedisPool.close();
		}
		return result;		
	}
	
}
