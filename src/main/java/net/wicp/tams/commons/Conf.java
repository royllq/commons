package net.wicp.tams.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wicp.tams.commons.apiext.CollectionUtil;
import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.StringUtil;

/****
 * 配置文件会在30秒刷一下是否更新
 * 
 * @author Administrator
 *
 */
public abstract class Conf {
	private static Logger logger = LoggerFactory.getLogger(Conf.class);
	public static final Properties utilProperties = IOUtil.fileToProperties("/commonsUtil.properties",Conf.class);// 属性配置
	public static long lastModified = 0L;
	private static final Map<String, Callback> reshBacks = new HashMap<>();// 重新加载配置文件时需要的回调函数,key：模块名
	private static final Map<String, String[]> props = new HashMap<>();

	public static interface Callback {
		public void doReshConf(Properties newProperties);
	}

	static {
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
						Properties oldProperties = (Properties) utilProperties.clone();// 旧配置属性
						Properties newProperties = new Properties();
						fileInputStream = new FileInputStream(file);
						newProperties.load(fileInputStream);
						// 重新装配新的属性
						utilProperties.clear();
						for (Object key : newProperties.keySet()) {
							utilProperties.put(key, newProperties.get(key));
						}
						for (String moudle : reshBacks.keySet()) {
							String[] propNames = props.get(moudle);
							if (ArrayUtils.isEmpty(propNames)) {// 没有观察的属性名称不做调用
								continue;
							}
							// 查找是否观察的属性有变化
							boolean ischange = false;
							for (String propName : propNames) {
								if (propName.endsWith("%s")) {//取多个属性值，如redisserver%s
									String keyPre = propName.substring(0, propName.length() - 2);
									Map<String, String> oldmap = CollectionUtil.getPropsByKeypre(oldProperties, keyPre);
									Map<String, String> newmap = CollectionUtil.getPropsByKeypre(newProperties, keyPre);
									for (String key : oldmap.keySet()) {
										String oldValue = oldmap.get(key);
										String newValue = newmap.get(key);
										if (!StringUtil.hasNull(oldValue).equals(StringUtil.hasNull(newValue))) {
											ischange = true;
											break;
										}

									}
									if (ischange) {
										break;
									}
								} else {
									String oldValue = oldProperties.getProperty(propName);
									String newValue = newProperties.getProperty(propName);
									if (!StringUtil.hasNull(oldValue).equals(StringUtil.hasNull(newValue))) {
										ischange = true;
										break;
									}
								}

							}
							if (ischange) {
								try {
									reshBacks.get(moudle).doReshConf(newProperties);
								} catch (Exception e) {
									logger.error("加载配置文件失败，回调模块[" + moudle + "]错误", e);
								}
							}
						}
						logger.info("成功刷新配置文件");
					}
				} catch (Exception e) {
					logger.error("classpath的根目录下没有commonsUtil.properties文件，将使用commons.jar包的缺少配置。", e);
				} finally {
					if (fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}, 0, 60 * 1000);

	}

	/***
	 * 添加回调方法
	 * 
	 * @param moudle
	 *            模块名
	 * @param callback
	 *            回调类
	 * @param proNames
	 *            关心的属性名
	 */
	public static void addCallBack(String moudle, Callback callback, String... proNames) {
		Validate.isTrue(ArrayUtils.isNotEmpty(proNames));
		props.put(moudle, proNames);
		reshBacks.put(moudle, callback);
	}

}
