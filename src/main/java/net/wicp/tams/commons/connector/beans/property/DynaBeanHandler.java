package net.wicp.tams.commons.connector.beans.property;

import java.util.HashMap;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.beans.CusDynaClass;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.exception.ExceptAll;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

public class DynaBeanHandler extends BasicNoHandler {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogHelp
			.getLogger(DynaBeanHandler.class);

	private CusDynaClass valueClass;

	public CusDynaClass getValueClass() {
		return this.valueClass;
	}

	public DynaBeanHandler(String name, DynaClass valueClass) {
		super(name, valueClass);
		this.valueClass = (CusDynaClass) valueClass;
	}

	public DynaBeanHandler(String name, Class type, DynaClass valueClass) {
		super(name, type, valueClass.getClass());
		this.valueClass = (CusDynaClass) valueClass;
	}

	@Override
	protected Result checkSingleValue(Object value) {
		String strict = getAttributeValue(ColProperty.strict);// 是否执行严格Class比较，服务端与客户端解析相同的xml的Class也不相同，如controlinfo等
		boolean isStrict = true;
		if (StringUtils.isNotBlank(strict)) {
			isStrict = Boolean.parseBoolean(strict);
		}
		boolean isSame = false;
		if (isStrict) {
			isSame = ((CusDynaBean) value).getDynaClass() != this.valueClass;
		} else {
			isSame = isSameClass(value);
		}
		if (!isSame) {
			logger.error("[{}]的类型不匹配，应该是CusDynaClass,但传进来的参数是[{}]类型", name,
					value.getClass().getName());
			return new Result(ExceptAll.Param_typenofit);
		}
		return Result.getSuc();
	}

	private boolean isSameClass(Object value) {
		HashMap<String, AbstractDynaClassProperty> thisProps = this.valueClass.getAllPropertys();
		AbstractDynaClassProperty[] valProps = ((CusDynaBean) value)
				.getDynaClass().getDynaProperties();
		if (thisProps.size() != valProps.length) {
			return false;
		}
		boolean ret = true;
		for (AbstractDynaClassProperty valprop : valProps) {
			if (!thisProps.keySet().contains(valprop.getName())) {
				ret = false;
				break;
			}
		}
		return ret;
	}

	@Override
	protected Object jsonTosingleObj(Object obj) {
		JSONObject retobj = (JSONObject) obj;
		CusDynaBean dynabean = this.valueClass.newInstance();
		for (String key : retobj.keys()) {
			AbstractDynaClassProperty prop = (AbstractDynaClassProperty) dynabean
					.getDynaClass().getDynaProperty(key);
			if (prop != null) {
				prop.setValueByJson(dynabean, retobj.get(key));
			}
		}
		return dynabean;
	}

	@Override
	public Object getSingleDefaultColValue() {
		String defaultValue = getAttriValue(ColProperty.defaultValue);
		if (StringUtils.isNotBlank(defaultValue)) {
			JSONObject json = new JSONObject(defaultValue);
			CusDynaBean db = (CusDynaBean) this.jsonTosingleObj(json);
			return db;
		} else {
			return null;
		}
	}

	@Override
	protected Object singleObjToJson(Object singleValue) {
		if (singleValue == null) {
			return null;
		}
		JSONObject retobj = new JSONObject();
		CusDynaBean cus = (CusDynaBean) singleValue;
		AbstractDynaClassProperty[] props = cus.getDynaClass()
				.getDynaProperties();

		for (AbstractDynaClassProperty prop : props) {
			Object propValue=cus.get(prop.getName());
			retobj.put(prop.getName(), prop.singleObjToJson(propValue));
		}
		return retobj;
	}

}
