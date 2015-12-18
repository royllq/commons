package net.wicp.tams.commons.test.other;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.wicp.tams.commons.apiext.LdapObj;
import net.wicp.tams.commons.apiext.PwdUtil;
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

	@Test
	public void testPwd() throws UnsupportedEncodingException {
		String pwd = PwdUtil.StringToBase64("c9f0f895fb98ab9159f51fd0297e236d");
		Assert.assertEquals("yfD4lfuYq5FZ9R/QKX4jbQ==", pwd);
		String pwd2 = PwdUtil.Base64ToString("yfD4lfuYq5FZ9R/QKX4jbQ==");
		Assert.assertEquals("c9f0f895fb98ab9159f51fd0297e236d", pwd2);
	}

	@Test
	public void Encrypt3DES() {
		String key1 = "fox@mail.com";
		String origValue1 = "andy@@@@@中文";
		String encryptStr = PwdUtil.Encrypt3DES(origValue1, key1);
		String decrypt3DES = PwdUtil.Decrypt3DES(encryptStr, key1);
		Assert.assertEquals(origValue1, decrypt3DES);
		String origValue2 = "#andy@中文2";
		String encryptStr2 = PwdUtil.Encrypt3DES(origValue2);
		String decrypt3DES2 = PwdUtil.Decrypt3DES(encryptStr2);
		Assert.assertEquals(origValue2, decrypt3DES2);
	}
	
	

	/*
	 * @Test public void testgetContextType() { List<AddrInfo> list1=null; Class
	 * class1= ReflectAssist.getContextType(list1); list1=new ArrayList<>();
	 * list1.add(new AddrInfo()); Class class2=
	 * ReflectAssist.getContextType(list1); System.out.println("aa"); }
	 */

}
