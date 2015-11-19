package net.wicp.tams.commons.connector.beans.property;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.exception.ExceptAll;

/****
 * 二进制
 * 
 * @author zhoujunhui
 *
 */
@SuppressWarnings("rawtypes")
public class BytesHandler extends BasicNoHandler {
	private static final long serialVersionUID = 8868123823119037087L;
	private final static Logger logger = LogHelp.getLogger(BytesHandler.class);

	public BytesHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

	public BytesHandler(String name, Class type) {
		super(name, type);
	}

	@Override
	protected Result checkSingleValue(Object value) {
		if (value.getClass().isAssignableFrom(byte[].class)) {
			return Result.getSuc();
		} else {
			logger.error("[{}]的类型不匹配，应该是byte[],但传进来的参数是[{}]基本类型", name, value
					.getClass().getName());
			return new Result(ExceptAll.Param_typenofit);
		}
	}

	@Override
	public Object getSingleDefaultColValue() {
		String defaultValue = getAttriValue(ColProperty.defaultValue);
		if (StringUtils.isBlank(defaultValue)) {
			return null;
		} else {
			return defaultValue.getBytes();
		}
	}

	@Override
	protected Object singleObjToJson(Object json) {
		byte[] tempobj = (byte[]) json;
		try {
			return new String(tempobj, Conf.get("common.encode"));
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("字段[{}]二进制转为string对象出错", name), e);
			return null;
		}
	}

	protected Object jsonTosingleObj(Object obj) {
		String tempstr = (String) obj;
		try {
			return tempstr.getBytes(Conf.get("common.encode"));
		} catch (UnsupportedEncodingException e) {
			logger.error(String.format("字段[{}] String转为二进制时出错", name), e);
			return null;
		}
	}

}
