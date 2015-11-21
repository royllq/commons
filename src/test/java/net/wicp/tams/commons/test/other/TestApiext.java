package net.wicp.tams.commons.test.other;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.wicp.tams.commons.apiext.RedisClient;
import redis.clients.jedis.Jedis;

public class TestApiext {
	@Test
	public void testRedis() {
		Jedis jedis = RedisClient.getConnection();
		Map<String, String> input = new HashMap<>();
		input.put("key1", "value1");
		input.put("key2", "value2");
		input.put("key3", "value3");
		jedis.hmset("test:aaa", input);
		List<String> values = jedis.hmget("test:aaa", "key2");
		Assert.assertEquals("value2", values.get(0));
		RedisClient.returnResource(jedis);
	}
}
