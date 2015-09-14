package net.wicp.tams.commons.connector.beans.property;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.callback.impl.optGType.OptAbsGType;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.constant.ColGType;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.constant.ColType;
import net.wicp.tams.commons.exception.ExceptAll;

import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

@SuppressWarnings("rawtypes")
public abstract class AbstractDynaClassProperty extends DynaProperty implements
		Cloneable {
	private static final long serialVersionUID = -5474164260071583692L;
	private final static Logger logger = LogHelp
			.getLogger(AbstractDynaClassProperty.class);
	protected final Map<ColProperty, String> attributes = new HashMap<ColProperty, String>();// 属性

	// ////////////////////////构造函数/////////////////////////////////////////////////////////////////////////////
	public AbstractDynaClassProperty(String name) {
		super(name);
	}

	public AbstractDynaClassProperty(String name, Class type) {
		super(name, type);
	}

	public AbstractDynaClassProperty(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

	// //////////////////////////////////值的操作////////////////////////////////////////////////////////////////////////
	protected abstract Result checkSingleValue(Object value);// 检查单个值的合法性

	/***
	 * 检查传进来的值合法性
	 * 
	 * @param value
	 * @return
	 */
	public Result checkValue(Object value) {
		OptAbsGType checkobj = new CheckPropertyValue();
		Result result = checkobj.opt(getGType(), value);
		return result;
	}

	protected abstract Object singleObjToJson(Object singleValue);// 把对应值转为String，如果是dynabean或是javabean则返回JsonObj

	protected abstract Object jsonTosingleObj(Object obj);// string或是JsonObj对象转成相应的Object，

	/****
	 * 得到Json对象 如果组装出错，则会返回出错结果 如果调用失败，返回为null在Bean中相应的方法就不会把它添加到json对象中
	 * 
	 * @param dynaBean
	 * @return
	 */
	public JSONObject getJsonObj(CusDynaBean dynaBean) {
		OptAbsGType packobj = new PackJson();
		Result res = packobj.opt(getGType(), dynaBean);
		Object retobj = null;
		if (res.isSuc()) {
			retobj = res.getRetObj(0);
			return new JSONObject(name, retobj);
		} else {
			return null;
		}
	}

	/***
	 * 通过Json对象设置值
	 * 
	 * @param dynaBean
	 * @param obj
	 */
	public void setValueByJson(CusDynaBean dynaBean, Object json) {
		OptAbsGType putobj = new PutJson(json);
		Result res = putobj.opt(getGType(), dynaBean);
		if (!res.isSuc()) {
			logger.error("设置动态Bean值出错，原因：{}", res.getMessage());
		}
	}

	/*****
	 * 得到默认的单值
	 */
	public abstract Object getSingleDefaultColValue();

	/****
	 * 只有是单值类型时才能设置默认值
	 * 
	 * @param dynaBean
	 */
	public void setDefaultColValue(CusDynaBean dynaBean) {
		if (StringUtils.isNotBlank(getAttriValue(ColProperty.defaultValue))
				&& getGType() == ColGType.single) {// 只有单值且有默认值时才进行设置
			Object defaultValue = getSingleDefaultColValue();
			if (defaultValue != null) {
				dynaBean.set(name, defaultValue);
			}
		}
	}

	// ////////////////////////////列的对应属性操作//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 通过ColProperty得到属性值
	 * */
	public String getAttriValue(ColProperty attname) {
		if (attname == null) {
			return null;
		}
		return attributes.get(attname);
	}

	/**
	 * 集合类型
	 * */
	public ColGType getGType() {
		String gType = getAttriValue(ColProperty.gtype);
		return ColGType.getByName(gType);
	}

	/**
	 * 是否允许为空
	 * */
	public boolean isNull() {
		String isnullStr = getAttriValue(ColProperty.isnull);
		return Boolean.parseBoolean(isnullStr);
	}

	public void putAttributes(Map<ColProperty, String> attributes) {
		this.attributes.putAll(attributes);
	}

	public void putAttribute(ColProperty colProperty, String value) {
		if (colProperty == null) {
			return;
		}
		this.attributes.put(colProperty, value);
	}

	public void putAttribute(String attname, String value) {
		ColProperty findCol = ColProperty.getByName(attname);
		putAttribute(findCol, value);
	}

	public Map<ColProperty, String> getAttributes() {
		return this.attributes;
	}

	public String getAttributeValue(ColProperty attname) {
		if (attname == null) {
			return null;
		}
		return attributes.get(attname);
	}

	public String getAttributeValue(String attname) {
		ColProperty findCol = ColProperty.getByName(attname);
		return getAttributeValue(findCol);
	}

	// /////////////////////////////////////检查值//////////////////////////////////////////////////////////////////////////////
	private class CheckPropertyValue extends OptAbsGType {

		int length = StringUtils.isBlank(getAttriValue(ColProperty.length)) ? -1
				: Integer.parseInt(getAttriValue(ColProperty.length));

		@Override
		protected Result doSingle(Object param) {
			return checkSingleValue(param);
		}

		@Override
		protected Result doArray(Object param) {
			if (param != null
					&& !param.getClass().isArray()
					|| (ColType.bytes.name().equals(
							getAttriValue(ColProperty.type)) && param
							.getClass().isAssignableFrom(byte[].class))) {
				return doSingle(param);
			}
			Object[] values = (Object[]) param;
			if (length >= 0 && values.length > length) {
				logger.error("越界[{}]长度为：{} 但是传进来的值大小为：", name, length,
						values.length);
				return new Result(ExceptAll.Param_lengthover);
			} else {
				StringBuffer retbuff = new StringBuffer("");
				for (int i = 0; i < values.length; i++) {
					Result rs = checkSingleValue(values[i]);
					if (!rs.isSuc()) {
						retbuff.append(String.format("第[%s]个参数不合法，原因：%s\n",
								i + 1, rs.getMessage()));
					}
				}
				return retValue(retbuff);
			}
		}

		private Result retValue(StringBuffer retbuff) {
			if (StringUtils.isBlank(retbuff.toString())) {
				return Result.getSuc();
			} else {
				retbuff.insert(0, "检查错误，原因：");
				return Result.getError(retbuff.toString());
			}
		}

		@Override
		protected Result doMap(Object param) {
			if (!Map.class.isAssignableFrom(param.getClass())) {// 如果是
																// set(prop,key,value)这种方式调用会走此路径
				return doSingle(param);
			}
			Map tempValue = (Map) param;
			if (length >= 0 && tempValue.size() > length) {
				logger.error("越界[{}]长度为：{} 但是传进来的值大小为：", name, length,
						tempValue.size());
				return new Result(ExceptAll.Param_lengthover);
			} else {
				StringBuffer retbuff = new StringBuffer("");
				for (Iterator iterator = tempValue.keySet().iterator(); iterator
						.hasNext();) {
					String eleKey = (String) iterator.next();
					Result rs = checkSingleValue(tempValue.get(eleKey));
					if (!rs.isSuc()) {
						retbuff.append(String.format("参数：{} 不合法，原因：{},",
								eleKey, rs.getMessage()));
					}
				}
				return retValue(retbuff);
			}
		}

		@Override
		protected Result doList(Object param) {
			if (!List.class.isAssignableFrom(param.getClass())) {
				return doSingle(param);
			}
			List tempValue = (List) param;
			if (length >= 0 && tempValue.size() > length) {
				logger.error("越界[{}]长度为：{} 但是传进来的值大小为：", name, length,
						tempValue.size());
				return new Result(ExceptAll.Param_lengthover);
			} else {
				StringBuffer retbuff = new StringBuffer("");
				for (int i = 0; i < tempValue.size(); i++) {
					Object object = tempValue.get(i);
					Result rs = checkSingleValue(object);
					if (!rs.isSuc()) {
						retbuff.append(String.format("第{}个参数不合法，原因：{},", i + 1,
								rs.getMessage()));
					}
				}
				return retValue(retbuff);
			}
		}

	}

	// ///////////////////////////////////////组装Json对象/////////////////////////////////////////////////////////////////////////////////
	private class PackJson extends OptAbsGType {

		@Override
		protected Result doSingle(Object param) {
			if (param == null) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			CusDynaBean dynaBean = (CusDynaBean) param;
			Object retobj = singleObjToJson(dynaBean.get(name));
			if (retobj == null) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			Result ret = Result.getSuc();
			ret.setRetObjs(retobj);
			return ret;
		}

		/*
		 * private Object getValueForJson(CusDynaBean dynaBean) { Object retobj
		 * = null; if (AbstractDynaClassProperty.this instanceof BasicNoHandler)
		 * {// TODO // 是非基础类 // 调用bean的toJson方法 CusDynaBean dbsub =
		 * (CusDynaBean) dynaBean.get(name); // retjson=getJsonObj(obj); } else
		 * { retobj = singleObjToJson(dynaBean); } return retobj; }
		 */

		@Override
		protected Result doArray(Object param) {
			if (param == null) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			CusDynaBean dynaBean = (CusDynaBean) param;
			Object[] objary = (Object[]) dynaBean.get(name);
			if (objary == null || objary.length == 0) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			JSONArray retary = new JSONArray();
			for (Object object : objary) {
				Object retobj = null;
				if (object instanceof CusDynaBean) {// TODO 还差JavaBean对象
					retobj = singleObjToJson(object);
				} else {
					retobj = String.valueOf(object);
				}
				retary.put(retobj);
			}
			Result ret = Result.getSuc();
			ret.setRetObjs(retary);
			return ret;
		}

		@Override
		protected Result doMap(Object param) {
			if (param == null) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			CusDynaBean dynaBean = (CusDynaBean) param;
			Map objmap = (Map) dynaBean.get(name);
			if (MapUtils.isEmpty(objmap)) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			JSONObject retobj = new JSONObject();
			for (Object key : objmap.keySet()) {
				if (objmap.get(key) instanceof CusDynaBean) {
					retobj.put(String.valueOf(key),
							singleObjToJson(objmap.get(key)));
				} else {
					retobj.put(String.valueOf(key),
							String.valueOf(objmap.get(key)));
				}
			}
			Result ret = Result.getSuc();
			ret.setRetObjs(retobj);
			return ret;
		}

		@Override
		protected Result doList(Object param) {
			if (param == null) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			CusDynaBean dynaBean = (CusDynaBean) param;
			List objlist = (List) dynaBean.get(name);
			if (CollectionUtils.isEmpty(objlist)) {
				return Result.getError("空值");// 空值，不需要组装到Json对象中
			}
			JSONArray retobj = new JSONArray();
			for (Object object : objlist) {
				if (object instanceof CusDynaBean) {
					// TODO
				} else {
					retobj.put(String.valueOf(object));
				}
			}
			Result ret = Result.getSuc();
			ret.setRetObjs(retobj);
			return ret;

		}
	}

	// /////////////////////////////////通过Json对象设置值////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private class PutJson extends OptAbsGType {

		private final Object obj;// 如果是单值就是String ，数组和List就是 JSonArray，Map就是JSon

		public PutJson(Object json) {
			if (json instanceof BasicHandler) {// 基础数据类型它会是string
				this.obj = json;
			} else {
				this.obj = json;
			}

		}

		@Override
		protected Result doSingle(Object param) {
			CusDynaBean inputBean = (CusDynaBean) param;
			Object valobj = jsonTosingleObj(obj);
			inputBean.set(name, valobj);
			return Result.getSuc();
		}

		@Override
		protected Result doArray(Object param) {
			CusDynaBean inputBean = (CusDynaBean) param;
			JSONArray arry = (JSONArray) obj;
			Object[] retAry = new Object[arry.length()];
			for (int i = 0; i < retAry.length; i++) {
				Object valobj = jsonTosingleObj(arry.get(i));
				retAry[i] = valobj;
			}
			inputBean.set(name, retAry);
			return Result.getSuc();
		}

		@Override
		protected Result doMap(Object param) {
			CusDynaBean inputBean = (CusDynaBean) param;
			JSONObject json = (JSONObject) obj;
			for (String key : json.keys()) {
				Object valobj = jsonTosingleObj(json.get(key));
				inputBean.set(name, key, valobj);
			}
			return Result.getSuc();
		}

		@Override
		protected Result doList(Object param) {
			CusDynaBean inputBean = (CusDynaBean) param;
			JSONArray arry = (JSONArray) obj;
			for (int i = 0; i < arry.length(); i++) {
				Object valobj = jsonTosingleObj(arry.get(i));
				inputBean.set(name, i, valobj);
			}
			return Result.getSuc();
		}

	}
}
