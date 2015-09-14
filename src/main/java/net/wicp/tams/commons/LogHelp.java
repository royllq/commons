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
		PropertyConfigurator.configure(LogHelp.class
				.getResourceAsStream("/log_util.properties"));
	}

	/****
	 * 跟据logger的名字得到日志
	 * 
	 * @param name
	 * @return
	 */
	public static final Logger getByName(String name) {
		return LoggerFactory.getLogger(name);
	}

	public static final void addConfig(Properties prop) {
		PropertyConfigurator.configure(prop);
	}

	public static final void addConfig(String path) {
		PropertyConfigurator.configure(LogHelp.class.getClassLoader()
				.getResourceAsStream(path));
	}

	/***
	 * 通过类得到日志
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static final Logger getLogger(Class c) {
		return LoggerFactory.getLogger(c);
	}

	/**
	 * 通过对象得到日志
	 * 
	 * @param obj
	 * @return
	 */
	public static final Logger getByObj(Object obj) {
		return getLogger(obj.getClass());
	}
}
