package net.wicp.tams.commons.apiext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.Conf.Callback;
import net.wicp.tams.commons.exception.ProjectException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient {
	private static Logger logger = LoggerFactory.getLogger(RedisClient.class);
	private static JedisPool jedisPool;// 非切片连接池
	private static int defautlDb = 0;// 默认数据库
	private static boolean initPool = false;
	private final static Object lockObj = new Object();
	public final static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();// gson的格式化

	static {
		Conf.addCallBack("redis", new Callback() {
			@Override
			public void doReshConf(Properties newProperties) {
				RedisClient.setInitPool(true);// Redis动态刷新
			}
		}, "redisserver%s");
	}

	/****
	 * 通过配置得到 Jedis
	 * 
	 * @return Jedis实例
	 */
	public static Jedis getConnection() {
		if (jedisPool == null || initPool) {
			synchronized (lockObj) {
				initPool = false;
				if (jedisPool != null) {
					jedisPool.destroy();
				}
				String name = "redisserver";
				Map<String, String> confMap = Conf.getPre(name);
				JedisPoolConfig config = new JedisPoolConfig();
				config.setMaxTotal(Integer.parseInt(confMap.get(name + ".maxTotal")));
				config.setMaxIdle(Integer.parseInt(confMap.get(name + ".maxIdle")));
				config.setMaxWaitMillis(Integer.parseInt(confMap.get(name + ".maxWaitMillis")));
				config.setTestOnBorrow(Boolean.parseBoolean(confMap.get(name + ".testOnBorrow")));
				defautlDb = confMap.get(name + ".defaultDb") == null ? 0
						: Integer.parseInt(confMap.get(name + ".defaultDb"));
				String password = confMap.get(name + ".password");
				if (StringUtils.isBlank(password)) {// 有设置密码
					jedisPool = new JedisPool(config, confMap.get(name + ".host"),
							Integer.parseInt(confMap.get(name + ".port")));
				} else {
					jedisPool = new JedisPool(config, confMap.get(name + ".host"),
							Integer.parseInt(confMap.get(name + ".port")),
							Integer.parseInt(confMap.get(name + ".maxIdle")), password);
				}
				logger.info("初始化池成功");
			}
		}
		Jedis retJedis = jedisPool.getResource();
		retJedis.select(defautlDb);
		return retJedis;
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

	/***
	 * 把对象做为Map存放到Redis
	 * 
	 * @param obj
	 */
	public static <T extends Serializable> void putObjByMap(Jedis jedis, String key, T obj, Integer expire) {
		Map<String, String> inpumap = ReflectAssist.convertMapFromBean(obj);
		jedis.hmset(key, inpumap);
		if (expire != null) {
			jedis.expire(key, expire);
		}
	}

	/***
	 * 把对象做为Map存放到Redis
	 * 
	 * @param obj
	 */
	public static <T extends Serializable> void putObjByMap(Jedis jedis, String key, T obj) {
		putObjByMap(jedis, key, obj, null);
	}

	/***
	 * 把对象做为Json存放到Redis
	 * 
	 * @param obj
	 * @param key
	 * @param expire
	 */
	public final static <T extends Serializable> void putObjByJson(Jedis jedis, T obj, String key, Integer expire) {
		String json = gson.toJson(obj);
		jedis.set(key, json);
		if (expire != null) {
			jedis.expire(key, expire);
		}
	}

	/***
	 * 把对象做为Json存放到Redis
	 * 
	 * @param obj
	 * @param key
	 */
	public final static <T extends Serializable> void putObjByJson(Jedis jedis, T obj, String key) {
		putObjByJson(jedis, obj, key, null);
	}

	/****
	 * 枚举类型的map放到缓存
	 * 
	 * @param jedis
	 * @param key
	 * @param inputMap
	 */
	public final static <T extends Enum> void putEnumMap(Jedis jedis, String key, Map<T, String> inputMap) {
		if (jedis == null || MapUtils.isEmpty(inputMap) || StringUtil.isNull(key)) {
			return;
		}
		Map<String, String> input = new HashMap<>();
		for (Enum ele : inputMap.keySet()) {
			input.put(ele.name(), inputMap.get(ele));
		}
		jedis.hmset(key, input);
	}

	/***
	 * 得到枚举类的缓存对象
	 * 
	 * @param jedis
	 * @param key
	 * @param clazz
	 * @return
	 */
	public final static <T extends Enum> Map<T, String> getEnumMap(Jedis jedis, String key, Class clazz) {
		Object[] objs = clazz.getEnumConstants();
		String[] fields = new String[objs.length];
		for (int i = 0; i < fields.length; i++) {
			Enum tempobj = (Enum) objs[i];
			fields[i] = tempobj.name();
		}
		List<String> rets = jedis.hmget(key, fields);
		Map<T, String> retobj = new HashMap<>();
		for (int i = 0; i < fields.length; i++) {
			Enum curobj = null;
			for (Object object : objs) {
				Enum tempobj = (Enum) object;
				if (fields[i].equals(tempobj.name())) {
					curobj = tempobj;
					break;
				}
			}
			retobj.put((T) curobj, rets.get(i));
		}
		return retobj;
	}

	/***
	 * 取指定列的值
	 * 
	 * @param jedis
	 * @param key
	 * @param fields
	 * @return
	 */
	public static Map<String, String> getMapByField(Jedis jedis, String key, String... fields) {
		Map<String, String> retobj = new HashMap<String, String>();
		if (jedis == null || StringUtil.isNull(key) || ArrayUtils.isEmpty(fields)) {
			return retobj;
		}
		List<String> values = jedis.hmget(key, fields);
		if (ArrayUtils.isNotEmpty(fields) && CollectionUtils.isNotEmpty(values)) {
			for (int i = 0; i < fields.length; i++) {
				retobj.put(fields[i], values.get(i));
			}
		}
		return retobj;
	}

	/***
	 * Redis上的值为Map,取对象的值，没有指定字段就取全部
	 * 
	 * @param clazz
	 * @param jedis
	 * @param key
	 * @param fields
	 * @return
	 */
	public static <T extends Serializable> T getObjByMapValue(Class clazz, Jedis jedis, String key, String... fields) {
		if (ArrayUtils.isEmpty(fields)) {
			List<String> classfields = ReflectAssist.findGetField(clazz);
			fields = classfields.toArray(new String[classfields.size()]);
		}
		Map<String, String> retmap = getMapByField(jedis, key, fields);
		T rett = (T) ReflectAssist.convertMapToBean(clazz, retmap);
		return rett;
	}

	/***
	 * Redis上的值为Json,取对象的值
	 * 
	 * @param clazz
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static <T extends Serializable> T getObjByJsonValue(Class clazz, Jedis jedis, String key) {
		String jsonstr = jedis.get(String.valueOf(key));
		T retobj = (T) gson.fromJson(jsonstr, clazz);
		return retobj;
	}

	public static void setInitPool(boolean initPool) {
		RedisClient.initPool = initPool;
	}

}
