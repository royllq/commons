package net.wicp.tams.commons.callback.impl.convertvalue;

import java.util.Map;

import net.wicp.tams.commons.apiext.RedisClient;
import net.wicp.tams.commons.callback.IConvertValue;
import redis.clients.jedis.Jedis;

/***
 * Redis的解释转换器,Redis存的是 hashmap的形式值。 keypattern : key的模式，如：opt:sexf 和
 * opt:sexm的格式为 opt:%s valueCol : 传进来的值解释字段的字段名 jedis ：
 * 如果传此对象进来，此转换器转换所有选项时都只用此连接，且不会释放它，外部产生的连接由外部释放。
 * 如果没有传进来，则由RedisClient跟据配置信息来产生，用完自己释放
 * 
 * @author andy.zhou
 *
 */
public class ConvertValueRedis implements IConvertValue<String> {

	private final String keypattern;
	private final String valueCol;
	private Jedis jedis;

	public ConvertValueRedis(String keypattern, String valueCol) {
		this.keypattern = keypattern;
		this.valueCol = valueCol;
	}

	public ConvertValueRedis(String valueCol) {
		this.keypattern = null;
		this.valueCol = valueCol;
	}

	public ConvertValueRedis(String keypattern, String valueCol, Jedis jedis) {
		this.keypattern = keypattern;
		this.valueCol = valueCol;
		this.jedis = jedis;
	}

	public ConvertValueRedis(String valueCol, Jedis jedis) {
		this.keypattern = null;
		this.valueCol = valueCol;
		this.jedis = jedis;
	}

	@Override
	public String getStr(String keyObj) {
		String retstr = keyObj;
		Jedis jedisTrue = null;
		try {
			jedisTrue = this.jedis == null ? RedisClient.getConnection() : this.jedis;
			String key = keypattern == null ? keyObj : String.format(keypattern, keyObj);
			Map<String, String> retMap = RedisClient.getMapByField(jedisTrue, key, valueCol);
			retstr = retMap.get(valueCol) == null ? keyObj : retMap.get(valueCol);
		} catch (Exception e) {
		} finally {
			if (this.jedis == null && jedisTrue != null) {// 外面没有传jedis且自己成功生成jedis
				RedisClient.returnResource(jedisTrue);
			}
		}
		return retstr;
	}

	protected Jedis getJedis() {
		return this.jedis;
	}

}
