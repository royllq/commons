package net.wicp.tams.commons;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/****
 * 日志帮助
 * 
 * @author zhoujunhui
 *
 */
public abstract class LogHelp {
	static {
		PropertyConfigurator.configure(LogHelp.class.getResourceAsStream("/log_util.properties"));
	}

	/****
	 * 跟据logger的名字得到日志
	 * 
	 * @param name
	 *            logger的名字
	 * @return 日志对象
	 */
	public static final Logger getByName(String name) {
		return LoggerFactory.getLogger(name);
	}

	/***
	 * 增加日志的配置属性
	 * 
	 * @param prop
	 *            要增加的属性
	 */
	public static final void addConfig(Properties prop) {
		PropertyConfigurator.configure(prop);
	}

	/**
	 * 通过文件增加日志的配置
	 * 
	 * @param path
	 */
	public static final void addConfig(String path) {
		PropertyConfigurator.configure(LogHelp.class.getClassLoader().getResourceAsStream(path));
	}

	/***
	 * 通过类得到日志
	 * 
	 * @param classz
	 *            日志对应的类
	 * @return 日志对象
	 */
	@SuppressWarnings("rawtypes")
	public static final Logger getLogger(Class classz) {
		return LoggerFactory.getLogger(classz);
	}

	/**
	 * 通过对象得到日志
	 * 
	 * @param obj
	 *            日志对应对象
	 * @return 日志对象
	 */
	public static final Logger getByObj(Object obj) {
		return getLogger(obj.getClass());
	}
}
