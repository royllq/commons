package net.wicp.tams.commons.apiext;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.wicp.tams.commons.exception.ProjectException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {
	public static final String REDISENVNAME = "redisname";
	private static JedisPool jedisPool;// 非切片连接池

	private final static Object lockObj = new Object();

	/****
	 * 通过配置得到 Jedis
	 *
	 * @param connProp
	 * @return
	 * @throws ProjectException
	 */
	public static Jedis getConnection(Properties connProp) {
		if (connProp == null || connProp.size() == 0) {
			return null;
		}
		if (jedisPool == null) {
			synchronized (lockObj) {
				String redisName = System.getenv(REDISENVNAME);
				if (StringUtils.isBlank(redisName)) {
					redisName = "default";
				}
				String appName = (String) connProp.get(String.format("redisname_%s", redisName));
				Map<String, String> confMap = getRedisServerPropByKey(connProp, appName);
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(Integer.parseInt(confMap.get("maxTotal")));
				config.setMaxIdle(Integer.parseInt(confMap.get("maxIdle")));
				config.setMaxWaitMillis(Integer.parseInt(confMap.get("maxWaitMillis")));
				config.setTestOnBorrow(Boolean.parseBoolean(confMap.get("testOnBorrow")));

				String password = confMap.get("password");
				if (StringUtils.isBlank(password)) {// 有设置密码
					jedisPool = new JedisPool(config, confMap.get("host"), Integer.parseInt(confMap.get("port")));
				} else {
					jedisPool = new JedisPool(config, confMap.get("host"), Integer.parseInt(confMap.get("port")),
							Integer.parseInt(confMap.get("maxIdle")), password);
				}

			}
		}
		return jedisPool.getResource();
	}

	/***
	 * 放资源
	 *
	 * @param jedis
	 */
	public static void returnResource(Jedis jedis) {
		if (jedisPool != null) {
			jedisPool.returnResource(jedis);
		}
	}

	/**
	 * 取到Redis配置服务器配置示例： <br/>
	 * defaultRedisName=redis1<br/>
	 * rjzjh.redisserver.redis1.host=localhost<br/>
	 * rjzjh.redisserver.redis1.port=6379 rjzjh.redisserver.redis1.maxTotal=20
	 * <br/>
	 * rjzjh.redisserver.redis1.maxidle=5<br/>
	 * rjzjh.redisserver.redis1.maxWaitMillis=10001<br/>
	 * rjzjh.redisserver.redis1.testonborrow=false<br/>
	 */
	private static Map<String, String> getRedisServerPropByKey(final Properties prop, final String key) {
		Set<Object> propKeys = prop.keySet();
		Map<String, String> retMap = new HashMap<String, String>();
		for (Object object : propKeys) {
			String tempKey = String.valueOf(object);
			String tempStr = String.format("rjzjh.redisserver.%s.", key);
			if (tempKey.startsWith(tempStr)) {
				retMap.put(tempKey.replace(tempStr, ""), prop.getProperty(tempKey));
			}
		}
		return retMap;
	}

}
