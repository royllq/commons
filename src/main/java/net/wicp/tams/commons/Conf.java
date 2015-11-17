package net.wicp.tams.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.RedisClient;

/****
 * 配置文件会在30秒刷一下是否更新
 * 
 * @author Administrator
 *
 */
public abstract class Conf {
	private static Logger logger = LoggerFactory.getLogger(Conf.class);
	public static final Properties utilProperties = IOUtil.fileToProperties("/commonsUtil.properties");// 属性配置
	public static long lastModified = 0L;
	private static final Map<String, Callback> reshBacks = new HashMap<>();// 重新加载配置文件时需要的回调函数,key：模块名

	public static interface Callback {
		public void doReshConf(Properties newProperties);
	}

	static {
		reshBacks.put("commons", new Callback() {
			@Override
			public void doReshConf(Properties newProperties) {
				utilProperties.clear();
				for (Object key : newProperties.keySet()) {
					utilProperties.put(key, newProperties.get(key));
				}
				RedisClient.setInitPool(true);// Redis动态刷新
			}
		});

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				FileInputStream fileInputStream = null;
				try {
					String url = Thread.currentThread().getContextClassLoader().getResource("/commonsUtil.properties")
							.getPath();
					File file = new File(url);
					long curmodified = file.lastModified();
					if (curmodified > lastModified) {
						lastModified = curmodified;
						Properties newProperties = new Properties();
						fileInputStream = new FileInputStream(file);
						newProperties.load(fileInputStream);

						for (String moudle : reshBacks.keySet()) {
							try {
								reshBacks.get(moudle).doReshConf(newProperties);
							} catch (Exception e) {
								logger.error("加载配置文件失败，回调模块[" + moudle + "]错误", e);
							}
						}
						logger.info("成功刷新配置文件");
					}
				} catch (Exception e) {
					logger.error("加载配置文件失败", e);
				} finally {
					if (fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}, 0, 30 * 1000);

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
	public static Map<String, String> getRedisServerPropByKey(final Properties prop, final String key) {
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

	public static void addCallBack(String moudle, Callback callback) {
		reshBacks.put(moudle, callback);
	}
}
