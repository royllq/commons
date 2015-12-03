package net.wicp.tams.commons.callback.impl.convertvalue;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import net.wicp.tams.commons.apiext.RedisClient;
import net.wicp.tams.commons.apiext.StringUtil;
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
public abstract class ConvertValueRedis implements IConvertValue<String> {

	private final String keypattern;
	private final String[] valueCols;

	private Jedis jedis;

	public ConvertValueRedis(String keypattern, String... valueCols) {
		this.keypattern = keypattern;
		this.valueCols = valueCols;
		Validate.noNullElements(valueCols);
	}

	/***
	 * redis上的key由业务传进来的key值决定。 由转换器自己创建redis链接，性能差，不推荐
	 * 
	 * @param valueCols
	 *            redis上的key名，可以有多个值
	 */
	public ConvertValueRedis(String... valueCols) {
		this.keypattern = null;
		this.valueCols = valueCols;
		Validate.noNullElements(valueCols);
	}

	/***
	 * key和连接由业务决定，性能好，推荐
	 * 
	 * @param keypattern
	 *            key模式，redis的真实key为 String.format(keypattern, key);
	 * @param jedis
	 *            由业务传进来的redis连接
	 * @param valueCols
	 *            redis上的key名，可以有多个值
	 */
	public ConvertValueRedis(String keypattern, Jedis jedis, String... valueCols) {
		this.keypattern = keypattern;
		this.valueCols = valueCols;
		this.jedis = jedis;
		Validate.noNullElements(valueCols);
	}

	/***
	 * 连接由业务决定,key无模式，传进来的key即为redis的key
	 * 
	 * @param jedis
	 *            由业务传进来的redis连接
	 * @param valueCols
	 *            redis上的key名，可以有多个值
	 */
	public ConvertValueRedis(Jedis jedis, String... valueCols) {
		this.keypattern = null;
		this.valueCols = valueCols;
		this.jedis = jedis;
		Validate.noNullElements(valueCols);
	}

	@Override
	public String getStr(String keyObj) {
		String retstr = keyObj;
		Jedis jedisTrue = null;
		try {
			jedisTrue = this.jedis == null ? RedisClient.getConnection() : this.jedis;
			String key = StringUtil.isNull(keypattern) ? keyObj : String.format(keypattern, keyObj);
			Map<String, String> retMap = RedisClient.getMapByField(jedisTrue, key, valueCols);
			if (valueCols.length == 1) {
				retstr = retMap.get(valueCols[0]) == null ? keyObj : retMap.get(valueCols[0]);
			} else {
				retstr = buildText(keyObj, retMap);
			}

		} catch (Exception e) {
		} finally {
			if (this.jedis == null && jedisTrue != null) {// 外面没有传jedis且自己成功生成jedis
				RedisClient.returnResource(jedisTrue);
			}
		}
		return retstr;
	}

	/***
	 * 由业务决定解释器返回值
	 * 
	 * @param key
	 *            业务key
	 * @param retMap
	 *            从redis上返回的map值，key为valueCols
	 * @return 最后转换器的转换值
	 */
	public abstract String buildText(String key, Map<String, String> retMap);

	protected Jedis getJedis() {
		return this.jedis;
	}

	protected String getKeypattern() {
		return keypattern;
	}

	protected String[] getValueCols() {
		return valueCols;
	}

}
