package net.wicp.tams.commons.callback.impl.optColType;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.beans.CusDynaClass;
import net.wicp.tams.commons.connector.beans.property.AbstractDynaClassProperty;
import net.wicp.tams.commons.connector.beans.property.BytesHandler;
import net.wicp.tams.commons.connector.beans.property.DateHandler;
import net.wicp.tams.commons.connector.beans.property.DynaBeanHandler;
import net.wicp.tams.commons.connector.beans.property.EnumHandler;
import net.wicp.tams.commons.connector.beans.property.IntegerHandler;
import net.wicp.tams.commons.connector.beans.property.JavaBeanHandler;
import net.wicp.tams.commons.connector.beans.property.StringHandler;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.constant.ColGType;
import net.wicp.tams.commons.constant.ColProperty;

/*****
 * 解析动态Bean的相关操作
 * 
 * @author zhoujunhui
 *
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ParserDynaClassProperty extends OptAbsColType {
	private Map<ColProperty, String> inputMap;
	private ColGType gtype;
	private String proName;

	@Override
	protected Result doString(Object... param) {
		init(param);
		AbstractDynaClassProperty returnPropery = null;
		switch (gtype) {
		case single:
			returnPropery = new StringHandler(proName, String.class);
			break;
		case array:
			returnPropery = new StringHandler(proName, String[].class,
					String.class);
			break;
		case map:
			returnPropery = new StringHandler(proName, Map.class, String.class);
			break;
		case list:
			returnPropery = new StringHandler(proName, List.class, String.class);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	
	@Override
	protected Result doEnums(Object... param) {
		init(param);
		AbstractDynaClassProperty returnPropery = null;
		String className = inputMap.get(ColProperty.className);
		Class classObj = null;
		try {
			classObj = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		switch (gtype) {
		case single:
			returnPropery = new EnumHandler(proName, classObj);
			break;
		case array:
			returnPropery = new EnumHandler(proName, Enum[].class, classObj);
			break;
		case map:
			returnPropery = new EnumHandler(proName, Map.class, classObj);
			break;
		case list:
			returnPropery = new EnumHandler(proName, List.class, classObj);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	@Override
	protected Result doInteger(Object... param) {
		init(param);
		AbstractDynaClassProperty returnPropery = null;
		switch (gtype) {
		case single:
			returnPropery = new IntegerHandler(proName, Integer.class);
			break;
		case array:
			returnPropery = new IntegerHandler(proName, Integer[].class,
					String.class);
			break;
		case map:
			returnPropery = new IntegerHandler(proName, Map.class,
					Integer.class);
			break;
		case list:
			returnPropery = new IntegerHandler(proName, List.class,
					Integer.class);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	@Override
	protected Result doDatetime(Object... param) {
		init(param);
		AbstractDynaClassProperty returnPropery = null;
		switch (gtype) {
		case single:
			returnPropery = new DateHandler(proName, Date.class);
			break;
		case array:
			returnPropery = new DateHandler(proName, Date[].class, Date.class);
			break;
		case map:
			returnPropery = new DateHandler(proName, Map.class, Date.class);
			break;
		case list:
			returnPropery = new DateHandler(proName, List.class, Date.class);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	@Override
	protected Result doDynaBean(Object... param) {
		init(param);
		AbstractDynaClassProperty returnPropery = null;
		CusDynaClass retClass = (CusDynaClass) param[1];
		switch (gtype) {
		case single:
			returnPropery = new DynaBeanHandler(proName, retClass);
			break;
		case array:
			returnPropery = new DynaBeanHandler(proName, CusDynaBean[].class,
					retClass);
			break;
		case map:
			returnPropery = new DynaBeanHandler(proName, Map.class, retClass);
			break;
		case list:
			returnPropery = new DynaBeanHandler(proName, List.class, retClass);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	@Override
	protected Result doJavaBean(Object... param) {
		init(param);
		String className = inputMap.get(ColProperty.className);
		Class classObj = null;
		try {
			classObj = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		AbstractDynaClassProperty returnPropery = null;
		switch (gtype) {
		case single:
			returnPropery = new JavaBeanHandler(proName, classObj);
			break;
		case array:
			returnPropery = new JavaBeanHandler(proName, Object[].class,
					classObj);
			break;
		case map:
			returnPropery = new JavaBeanHandler(proName, Map.class, classObj);
			break;
		case list:
			returnPropery = new JavaBeanHandler(proName, List.class, classObj);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	@Override
	protected Result doBytes(Object... param) {
		init(param);
		AbstractDynaClassProperty returnPropery = null;
		switch (gtype) {
		case single:
			returnPropery = new BytesHandler(proName, byte[].class);
			break;
		case array:
			returnPropery = new BytesHandler(proName, byte[][].class,
					byte[].class);
			break;
		case map:
			returnPropery = new BytesHandler(proName, Map.class, byte[].class);
			break;
		case list:
			returnPropery = new BytesHandler(proName, List.class, byte[].class);
			break;
		default:
			break;
		}
		return retobj(returnPropery);
	}

	private Result retobj(AbstractDynaClassProperty returnPropery) {
		if (returnPropery == null) {
			return Result.getError("不支持的GType");
		} else {
			Result retobj = Result.getSuc();
			returnPropery.putAttributes(inputMap);
			retobj.setRetObjs(returnPropery);
			return retobj;
		}
	}

	private void init(Object... param) {
		Map<ColProperty, String> inputMap = (Map<ColProperty, String>) param[0];
		AbstractConfigClass.setDefaultColProperty(inputMap);// /统一放到组装Bean前面做，其它地方不再设置
		this.inputMap = inputMap;
		String gtypestr = inputMap.get(ColProperty.gtype);
		this.gtype = ColGType.getByName(gtypestr);
		this.proName = inputMap.get(ColProperty.name);
	}

}
