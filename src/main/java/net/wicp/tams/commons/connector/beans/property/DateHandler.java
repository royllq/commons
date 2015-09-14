package net.wicp.tams.commons.connector.beans.property;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.constant.DateFormatCase;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class DateHandler extends BasicHandler {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogHelp
			.getLogger(DateHandler.class);

	public DateHandler(String name) {
		super(name);
	}

	public DateHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

	public DateHandler(String name, Class type) {
		super(name, type);
	}

	@Override
	protected Result checkSingleValue(Object value) {

		if (value != null && !(value instanceof Date)) {
			logger.error("[{}]的类型不匹配，应该是Date,但传进来的参数是[{}]类型", name, value
					.getClass().getName());
			return new Result(ExceptAll.Param_typenofit);
		}
		if ((!isNull()) && value == null) {
			logger.error("[{}]不允许传空值", name);
			return new Result(ExceptAll.project_nonull);
		}

		Date minDate = null;
		Date maxDate = null;

		try {
			SimpleDateFormat format = getFormat();
			minDate = StringUtils.isBlank(getAttriValue(ColProperty.min)) ? null
					: format.parse(getAttriValue(ColProperty.min));
			maxDate = StringUtils.isBlank(getAttriValue(ColProperty.max)) ? null
					: format.parse(getAttriValue(ColProperty.max));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (minDate != null && ((Date) value).before(minDate)) {
			logger.error("[{}]比最小值[{}]还小", name, getAttriValue(ColProperty.min));
			return new Result(ExceptAll.project_overflow);
		}
		if (maxDate != null && ((Date) value).after(maxDate)) {
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
			try {
				return getFormat().parse(defaultValue);
			} catch (ParseException e) {
				logger.error("设置默认值{}错误，格式为{}", defaultValue, getFormat()
						.toString());
				return null;
			}
		}
	}

	@Override
	protected Object singleObjToJson(Object json) {
		Date value = (Date) (json);
		return getFormat().format(value);
	}

	@Override
	protected Object jsonTosingleObj(Object obj) {
		String objStr = String.valueOf(obj);
		try {
			return getFormat().parse(objStr);
		} catch (ParseException e) {
			logger.error(String.format("在json转为时间类型时错误，值%s，格式为%s", objStr,
					getFormat().toString()), e);
			return null;
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	/***
	 * 得到解析类默认为yyyy-MM-dd
	 * 
	 * @return
	 */
	private SimpleDateFormat getFormat() {
		SimpleDateFormat format = StringUtils
				.isBlank(getAttriValue(ColProperty.format)) ? DateFormatCase.YYYY_MM_DD
				.getInstanc() : new SimpleDateFormat(
				getAttriValue(ColProperty.format));
		return format;
	}

}
