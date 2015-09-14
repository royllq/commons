package net.wicp.tams.commons.connector.beans.property;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.constant.ColProperty;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

public class StringHandler extends BasicHandler {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogHelp
			.getLogger(StringHandler.class);

	public StringHandler(String name) {
		super(name);
	}

	public StringHandler(String name, Class type) {
		super(name, type);
	}

	public StringHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

	@Override
	protected Result checkSingleValue(Object value) {
		String errorMsg = null;
		if (value != null && !(value instanceof String)) {
			errorMsg = "[" + name + "]的类型不匹配，应该是String,但传进来的参数是["
					+ value.getClass().getName() + "]类型";
		}
		String valueStr = StringUtil.hasNull(String.valueOf(value));
		if (!isNull() && StringUtils.isBlank(valueStr)) {
			errorMsg = "[" + name + "]不允许传空值";
		}
		if (StringUtils.isNotBlank(getAttriValue(ColProperty.length))
				&& valueStr.length() > Integer
						.parseInt(getAttriValue(ColProperty.length))) {
			errorMsg = String.format("[%s]的值[%s]长度超过允许的最大值", name, value);
		}
		if (StringUtils.isNotBlank(getAttriValue(ColProperty.format))
				&& value != null
				&& !valueStr.matches(getAttriValue(ColProperty.format))) {
			errorMsg = "[" + name + "]格式不满足["
					+ getAttriValue(ColProperty.format) + "]";

		}
		if (StringUtils.isBlank(errorMsg)) {
			return Result.getSuc();
		} else {
			logger.error(errorMsg);
			return Result.getError(errorMsg);
		}
	}

	@Override
	public Object getSingleDefaultColValue() {
		return getAttriValue(ColProperty.defaultValue);
	}

	@Override
	protected Object singleObjToJson(Object json) {
		String retstr = StringUtil.hasNull(String.valueOf(json));
		if (StringUtils.isBlank(retstr)) {
			return null;
		} else {
			return retstr;
		}
	}

	@Override
	protected Object jsonTosingleObj(Object obj) {
		String value = String.valueOf(obj);
		return value;
	}

}
