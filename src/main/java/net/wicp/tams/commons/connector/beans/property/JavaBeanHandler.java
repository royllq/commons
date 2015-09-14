package net.wicp.tams.commons.connector.beans.property;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.exception.ExceptAll;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

import com.google.gson.Gson;

@SuppressWarnings("rawtypes")
public class JavaBeanHandler extends BasicNoHandler {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogHelp
			.getLogger(JavaBeanHandler.class);
	private Class beanClass;//

	public JavaBeanHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
		this.beanClass = contentType;
	}

	public JavaBeanHandler(String name, Class type) {
		super(name, type);
		this.beanClass = type;
	}

	@Override
	protected Result checkSingleValue(Object value) {
		if (value.getClass().isAssignableFrom(this.beanClass)) {
			return Result.getSuc();
		} else {
			logger.error("[{}]的类型不匹配，应该是[{}],但传进来的参数是[{}]基本类型", name,
					beanClass.getName(), value.getClass().getName());
			return new Result(ExceptAll.Param_typenofit);
		}
	}

	@Override
	public Object getSingleDefaultColValue() {
		String defaultValue = getAttriValue(ColProperty.defaultValue);
		if (StringUtils.isNotBlank(defaultValue)) {
			JSONObject json = new JSONObject(defaultValue);
			Object retdefaultobj = jsonTosingleObj(json);
			return retdefaultobj;
		} else {
			return null;
		}
	}

	@Override
	protected Object singleObjToJson(Object json) {
		Gson gson = new Gson();
		String jsonstr = gson.toJson(json);// java对象到json字
		return new JSONObject(jsonstr);
	}

	@Override
	protected Object jsonTosingleObj(Object obj) {
		// JSONObject inputobj = new JSONObject(String.valueOf(obj));
		JSONObject inputobj = (JSONObject) obj;
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Object retobj = gson.fromJson(inputobj.toString(true), beanClass);
		return retobj;
	}

}
