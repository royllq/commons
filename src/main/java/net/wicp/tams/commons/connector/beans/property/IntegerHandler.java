package net.wicp.tams.commons.connector.beans.property;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.exception.ExceptAll;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

@SuppressWarnings("rawtypes")
public class IntegerHandler extends BasicHandler {

	private static final long serialVersionUID = 946199084482013912L;
	private final static Logger logger = LogHelp.getLogger(IntegerHandler.class);

	public IntegerHandler(String name) {
		super(name);
	}

	public IntegerHandler(String name, Class type) {
		super(name, type);
	}

	public IntegerHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

	@Override
	protected Result checkSingleValue(Object value) {
		if (value != null && !(value instanceof Integer)) {
			logger.error("[{}]的类型不匹配，应该是Integer,但传进来的参数是[{}]类型", name, value.getClass().getName());
			return new Result(ExceptAll.Param_typenofit);
		}
		if (!isNull() && value == null || StringUtils.isBlank(StringUtil.hasNull(String.valueOf(value)))) {
			logger.error("[{}]不允许传空值", name);
			return new Result(ExceptAll.project_nonull);
		}
		if (StringUtils.isNotBlank(getAttriValue(ColProperty.min))
				&& Integer.parseInt(getAttriValue(ColProperty.min)) > (Integer) value) {
			logger.error("[{}]比最小值[{}]还小", name, getAttriValue(ColProperty.min));
			return new Result(ExceptAll.project_overflow);
		}
		if (StringUtils.isNotBlank(getAttriValue(ColProperty.max))
				&& Integer.parseInt(getAttriValue(ColProperty.max)) < (Integer) value) {
			logger.error("[{}]比最大值[{}]还大", name, getAttriValue(ColProperty.max));
			return new Result(ExceptAll.project_overflow);
		}
		return Result.getSuc();
	}

	@Override
	public Object getSingleDefaultColValue() {
		String defaultValue = getAttriValue(ColProperty.defaultValue);
		if (StringUtils.isBlank(defaultValue)) {
			return null;
		} else {
			return Integer.parseInt(defaultValue);
		}

	}

	@Override
	protected Object singleObjToJson(Object json) {
		return String.valueOf(json);
	}

	@Override
	protected Object jsonTosingleObj(Object obj) {
		return new Integer(String.valueOf(obj));
	}

}
