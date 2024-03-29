package net.wicp.tams.commons.callback.impl.convertvalue;

import java.util.Locale;
import java.util.ResourceBundle;

import net.wicp.tams.commons.I18N.MessageUtils;
import net.wicp.tams.commons.callback.IConvertValue;
import net.wicp.tams.commons.exception.IExcept;

import org.apache.commons.lang.StringUtils;

/***
 * 异常的国际化,如果没有设置国际化信息就取ExceptAll的desc字段来描述 本对象只适用于commons包中的
 * I18N/MessageBundleUtil 文件中的国际化信息，如有其它国际化信息请把它信就入
 * net.wicp.tams.commons.I18N.MessageUtils.I18Resource即可适用
 * 
 * @author zhoujunhui
 *
 */
public class ConvertValueExcept implements IConvertValue<IExcept> {

	private ResourceBundle messages;

	public ConvertValueExcept(String language) {
		messages = MessageUtils.getInstance(language);
	}

	public ConvertValueExcept(Locale locale) {
		messages = MessageUtils.getInstance(locale);
	}

	public ConvertValueExcept() {
		messages = MessageUtils.getInstance();
	}

	@Override
	public String getStr(IExcept keyObj) {
		try {
			String retstr = messages == null ? "" : messages.getString(keyObj.getErrorCode());
			return StringUtils.isBlank(retstr) ? keyObj.getDesc() : retstr;
		} catch (Exception e) {
			return keyObj.getDesc();
		}

	}

}
