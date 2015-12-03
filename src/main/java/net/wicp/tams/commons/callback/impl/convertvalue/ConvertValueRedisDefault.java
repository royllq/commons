package net.wicp.tams.commons.callback.impl.convertvalue;

import java.util.Map;

import redis.clients.jedis.Jedis;

public class ConvertValueRedisDefault extends ConvertValueRedis {

	public ConvertValueRedisDefault(Jedis jedis, String valueCol) {
		super(jedis, valueCol);
	}

	public ConvertValueRedisDefault(Jedis jedis, String keypattern, String valueCol) {
		super(keypattern, jedis, valueCol);
	}

	public ConvertValueRedisDefault(String valueCol) {
		this(null, valueCol);
	}

	/****
	 * 因为只传入一个valueCol，所以这个方法不起作用，但为了兼容又需要实现此方法
	 */
	@Override
	public String buildText(String key, Map<String, String> retMap) {
		return null;
	}

}
