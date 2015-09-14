package net.wicp.tams.commons.constant;

import java.util.Locale;
import java.util.Properties;
import java.util.Random;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.apiext.IOUtil;

import org.slf4j.Logger;

/**
 * @ClassName: Config
 * @Description: 整个系统的常量定义，代替配置文件
 * @author 周俊辉
 * @date 2010-11-13 下午03:33:52
 * 
 */
public abstract class Config {
	private final static Logger logger = LogHelp.getLogger(Config.class);

	public static final Random random = new Random();

	public static Locale getLocale() {
		return Config.locale;
	}

	public final static Properties utilProperties = IOUtil
			.fileToProperties("/conf/commonsUtil.properties");

	public static Locale locale = new Locale(Config.getValue("common.i18n"));// 配置默认语言，如果在多语言版本项目切换语言时需要调用setCurLocale方法进行切换

	public static String getValue(String key) {
		return Config.utilProperties.getProperty(key);
	}

	public static void setCurLocale(Locale curLocale) {
		if (curLocale != null) {
			Config.locale = curLocale;
		}
	}

}
