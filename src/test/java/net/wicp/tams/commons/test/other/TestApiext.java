package net.wicp.tams.commons.test.other;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import net.wicp.tams.commons.apiext.PwdUtil;
import net.wicp.tams.commons.apiext.RedisClient;
import net.wicp.tams.commons.apiext.StringUtil;
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

	@Test
	public void testPwd() throws UnsupportedEncodingException {
		String pwd = PwdUtil.StringToBase64("c9f0f895fb98ab9159f51fd0297e236d");
		Assert.assertEquals("yfD4lfuYq5FZ9R/QKX4jbQ==", pwd);
		String pwd2 = PwdUtil.Base64ToString("yfD4lfuYq5FZ9R/QKX4jbQ==");
		Assert.assertEquals("c9f0f895fb98ab9159f51fd0297e236d", pwd2);
	}
}
