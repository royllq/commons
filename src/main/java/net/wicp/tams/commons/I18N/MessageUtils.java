package net.wicp.tams.commons.I18N;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import net.wicp.tams.commons.Conf;

/***
 * 得到国际化信息
 * 
 * @author zhoujunhui
 *
 */
public abstract class MessageUtils {
	public static final Map<Locale, ResourceBundle> I18Resource = new HashMap<Locale, ResourceBundle>();

	// public static final Locale defaultLocale = Config.curLocale;//
	// 系统设置的默认Locale

	public static ResourceBundle getInstance(Locale localhost) {
		Locale queryLocal = null;
		if (localhost == null) {
			queryLocal = Conf.getCurLocale();
		}
		ResourceBundle retobj = I18Resource.get(queryLocal);
		if (retobj == null) {
			retobj = ResourceBundle.getBundle("I18N/MessageBundleUtil",
					queryLocal);
			if (retobj != null) {
				I18Resource.put(queryLocal, retobj);
			}

		}
		return retobj;
	}

	public static ResourceBundle getInstance() {
		Locale input = null;
		return getInstance(input);
	}

	public static ResourceBundle getInstance(String language) {
		if (StringUtils.isBlank(language)) {
			return getInstance();
		}
		Locale input = new Locale(language);
		return getInstance(input);
	}

}
