package net.wicp.tams.commons.connector.beans.property;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.exception.ExceptAll;

@SuppressWarnings("rawtypes")
public class EnumHandler extends BasicHandler {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogHelp.getLogger(EnumHandler.class);

	private Class beanClass;//

	public EnumHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
		this.beanClass = contentType;
	}

	public EnumHandler(String name, Class type) {
		super(name, type);
		this.beanClass = type;
	}

	@Override
	protected Result checkSingleValue(Object value) {
		if (value.getClass().isAssignableFrom(this.beanClass)) {
			return Result.getSuc();
		} else {
			logger.error("[{}]的类型不匹配，应该是[{}],但传进来的参数是[{}]基本类型", name, beanClass.getName(),value
					.getClass().getName());
			return new Result(ExceptAll.Param_typenofit);
		}
	}

	@Override
	protected Object singleObjToJson(Object singleValue) {
		Enum tempobj =(Enum)singleValue;
		return tempobj.name();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Object jsonTosingleObj(Object obj) {
		return Enum.valueOf(beanClass, (String) obj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getSingleDefaultColValue() {
		String defaultValue = getAttriValue(ColProperty.defaultValue);
		if (StringUtils.isBlank(defaultValue)) {
			return null;
		} else {
			return Enum.valueOf(beanClass, (String) defaultValue);
		}
	}

}
