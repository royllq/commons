package net.wicp.tams.commons.connector.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.callback.impl.optGType.OptAbsGType;
import net.wicp.tams.commons.connector.beans.property.AbstractDynaClassProperty;
import net.wicp.tams.commons.connector.beans.property.BasicHandler;
import net.wicp.tams.commons.connector.beans.property.BytesHandler;
import net.wicp.tams.commons.connector.beans.property.DynaBeanHandler;
import net.wicp.tams.commons.connector.beans.property.JavaBeanHandler;
import net.wicp.tams.commons.constant.ColGType;
import net.wicp.tams.commons.exception.ExceptAll;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.lang.ArrayUtils;
import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

public class CusDynaBean extends BasicDynaBean {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LogHelp.getLogger(CusDynaBean.class);

	/*
	 * public JSONObject toJSONObject() { AbstractDynaClassProperty[] pro =
	 * this.dynaClass.getDynaProperties(); JSONObject retObj = new JSONObject();
	 * if (pro == null || pro.length == 0) { return retObj; } for
	 * (AbstractDynaClassProperty abstractDynaClassProperty : pro) { Object obj
	 * = abstractDynaClassProperty.getJsonObj(this);
	 * retObj.put(abstractDynaClassProperty.getName(), obj); } return retObj; }
	 */

	/****
	 * 通过json对象设置到动态Bean
	 * 
	 * @param jsonObj
	 */
	/*
	 * public void set(JSONObject jsonObj) { if (jsonObj == null) { return; }
	 * Set<String> keyset = jsonObj.keys(); if (CollectionUtils.isEmpty(keyset))
	 * { return; } AbstractDynaClassProperty[] pro =
	 * this.dynaClass.getDynaProperties(); if (pro == null || pro.length == 0) {
	 * return; } for (AbstractDynaClassProperty abstractDynaClassProperty : pro)
	 * { Object obj = jsonObj.opt(abstractDynaClassProperty.getName()); if (obj
	 * == null) { continue; } abstractDynaClassProperty.setValueByName(this,
	 * obj); }
	 * 
	 * }
	 */
	/***
	 * 新建实例，并设置好默认值
	 * 
	 * @param dynaClass
	 */
	public CusDynaBean(DynaClass dynaClass) {
		super(dynaClass);
		if (dynaClass != null) {
			this.dynaClass = (CusDynaClass) dynaClass;
			setDefaultColValue();
		}

	}

	@Override
	public CusDynaClass getDynaClass() {
		return (CusDynaClass) super.dynaClass;
	}

	/**
	 * 通过别名得到属性值
	 * 
	 * @param aliasName
	 *            别名
	 * @return 属性值
	 */
	public Object getByAliasName(String aliasName) {
		AbstractDynaClassProperty col = ((CusDynaClass) super.dynaClass).findPropertyByAlais(aliasName);
		if (col != null) {
			return super.get(col.getName());
		} else {
			return null;
		}
	}

	/***
	 * 通过列名得到json的string值，如果是单值则都原值，其它或是非基础类型则为json值
	 * 
	 * @param name
	 *            属性名称
	 * @return 属性值
	 */
	public String getStrValueByName(String name) {
		AbstractDynaClassProperty col = getDynaClass().findProperty(name);
		JSONObject json = col.getJsonObj(this);
		if (col instanceof BasicHandler || col instanceof BytesHandler) {
			if (json == null) {
				return "";
			}
			return json.getString(name);
		} else {
			if (json == null) {
				return "{}";
			}
			return json.getJSONObject(name).toString(true);
		}
	}

	@Override
	public void set(String name, Object value) {
		AbstractDynaClassProperty prop = (AbstractDynaClassProperty) this.dynaClass.getDynaProperty(name);
		checkValue(name, value, prop);
		PackObject packObject = new PackObject(prop); // new
														// PackObject(prop,super);
		Result ret = packObject.opt(prop.getGType(), value);// 设置值
		if (!ret.isSuc()) {
			throw new IllegalArgumentException(String.format("对属性%s设置值%s失败", name, value));
		}
	}

	@Override
	public void set(String name, int index, Object value) {
		AbstractDynaClassProperty prop = (AbstractDynaClassProperty) this.dynaClass.getDynaProperty(name);
		if (prop.getGType() == ColGType.array || prop.getGType() == ColGType.list) {
			checkValue(name, value, prop);
			super.set(name, index, value);
		} else {
			logger.error("属性[{}]要求array或list类型，不适合此方法", name);
			throw new IllegalArgumentException("要求List类型");
		}
	}

	@Override
	public void set(String name, String key, Object value) {
		AbstractDynaClassProperty prop = (AbstractDynaClassProperty) this.dynaClass.getDynaProperty(name);
		if (prop.getGType() == ColGType.map) {
			checkValue(name, value, prop);
			super.set(name, key, value);
		} else {
			logger.error("属性[{}]要求Map类型，不适合此方法", name);
			throw new IllegalArgumentException("要求Map类型");
		}
	}

	/***
	 * 返回Json对象
	 * 
	 * @return json对象
	 */
	public JSONObject getJsonObj() {
		JSONObject retobj = new JSONObject();
		AbstractDynaClassProperty[] props = ((CusDynaClass) super.dynaClass).getDynaProperties();
		for (AbstractDynaClassProperty prop : props) {
			JSONObject tempjson = prop.getJsonObj(this);
			if (tempjson != null) {
				retobj.put(prop.getName(), tempjson.get(prop.getName()));
			}
		}
		return retobj;
	}

	/***
	 * 通过Json数据设置值
	 * 
	 * @param obj
	 *            要设置的值 如果是单值就是String ，数组和List就是 JSonArray，Map就是JSon
	 */
	public void setByJson(JSONObject obj) {
		if (obj == null || obj.length() == 0) {
			return;
		}
		HashMap<String, AbstractDynaClassProperty> propmap = ((CusDynaClass) super.dynaClass).getAllPropertys();
		for (String key : obj.keys()) {
			AbstractDynaClassProperty prop = propmap.get(key);
			ColGType gtype = prop.getGType();
			if (prop != null) {
				switch (gtype) {
				case single:
					if (prop instanceof BasicHandler) {
						prop.setValueByJson(this, obj.getString(key));
					} else {
						prop.setValueByJson(this, obj.getJSONObject(key));
					}
					break;
				case array:
				case list:
					prop.setValueByJson(this, obj.getJSONArray(key));
					break;
				case map:
					prop.setValueByJson(this, obj.getJSONObject(key));
					break;
				}
			}
		}

	}

	/****
	 * 得到此动态Bean的Ｃlass对象的克隆类，这样就保证对Class不被修改
	 * 
	 * @return 动态Class
	 */
	public CusDynaClass copyClass() {
		try {
			return ((CusDynaClass) super.dynaClass).clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException("克隆失败");
		}
	}

	
	@Override
	public CusDynaBean clone() {
		try {
			return (CusDynaBean) super.clone();
		} catch (CloneNotSupportedException e) {
			logger.error("克隆");
			return null;
		}
	}

	private void parentSet(String name, Object value) {
		super.set(name, value);
	}

	// ///////////////////////////////////跟据具体设置值//////////////////////////////////////////////////////////////////////////////////////////////////
	private class PackObject extends OptAbsGType {
		private final AbstractDynaClassProperty prop;

		public PackObject(AbstractDynaClassProperty prop) {
			this.prop = prop;
		}

		@Override
		protected Result doSingle(Object param) {
			// bytes要不同处理
			if (prop instanceof BytesHandler) {
				if (param.getClass().isAssignableFrom(byte[].class)) {
					parentSet(prop.getName(), param);
				} else if (param.getClass().isAssignableFrom(byte[][].class)) {
					logger.warn("属性[{}]在参数定义中为单值对象.但在接受参数中却又多个值,系统将自动过滤后面得值", prop.getName());
					byte[][] tempValue = (byte[][]) param;
					parentSet(prop.getName(), tempValue[0]);
				}
				return Result.getSuc();
			}

			if (param != null && param.getClass().isArray()) {// 要求不是数组，传过来的是数组
				logger.warn("属性[{}]在参数定义中为单值对象.但在接受参数中却又多个值,系统将自动过滤后面得值", prop.getName());
				Object[] tempValue = (Object[]) param;
				parentSet(prop.getName(), tempValue[0]);
			} else {
				parentSet(prop.getName(), param);
			}
			return Result.getSuc();
		}

		@Override
		protected Result doArray(Object param) {
			// bytes要不同处理
			if (prop instanceof BytesHandler) {
				if (param.getClass().isAssignableFrom(byte[].class)) {
					parentSet(prop.getName(), new byte[][] { (byte[]) param });
				} else if (param.getClass().isAssignableFrom(byte[][].class)) {
					parentSet(prop.getName(), param);
				}
				return Result.getSuc();
			}
			if (param.getClass().isArray()) {
				parentSet(prop.getName(), param);
			} else {
				Object[] addValue = new Object[] { param };
				parentSet(prop.getName(), addValue);
			}
			return Result.getSuc();
		}

		@Override
		protected Result doMap(Object param) {
			if (Map.class.isAssignableFrom(param.getClass())) {
				parentSet(prop.getName(), param);
				return Result.getSuc();
			} else {
				logger.error("属性[{}]要求Map类型，却传来来{}类型", prop.getName(), param.getClass());
				return new Result(ExceptAll.Param_typenofit);
			}
		}

		@Override
		protected Result doList(Object param) {
			if (List.class.isAssignableFrom(param.getClass())) {
				parentSet(prop.getName(), param);
				return Result.getSuc();
			} else {
				logger.error("属性[{}]要求List类型，却传来来{}类型", prop.getName(), param.getClass());
				return new Result(ExceptAll.Param_typenofit);
			}
		}

	}

	// ////////////////////////////////////为了使用方便，特殊方法，不适合于全部////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/***
	 * 适合对象：DynaBeanHandler 作用：得到可用的Bean对象
	 * 
	 * @param name
	 * @param saveDefault
	 *            是否要保留Bean的默认值
	 * @return
	 */
	public CusDynaBean newCusDynaBean(String name, boolean saveDefault) {
		AbstractDynaClassProperty prop = (AbstractDynaClassProperty) this.dynaClass.getDynaProperty(name);
		if (prop instanceof DynaBeanHandler) {
			if (saveDefault) {
				CusDynaBean retobj = (CusDynaBean) prop.getSingleDefaultColValue();
				if (retobj != null) {// 有默认值则直接用默认值
					return retobj;
				}
			}
			DynaBeanHandler optClass = (DynaBeanHandler) prop;
			CusDynaBean retBean = new CusDynaBean(optClass.getValueClass());
			return retBean;
		} else {
			logger.error("方法[newCusDynaBean]只支持DynaBeanHandler的类型");
			throw new IllegalArgumentException("方法[newCusDynaBean]只支持DynaBeanHandler的类型");
		}
	}

	public CusDynaBean newCusDynaBean(String name) {
		return newCusDynaBean(name, true);
	}

	/****
	 * 适合对象：JavaBeanHandler 作用：得到JavaBean的默认对象
	 * 
	 * @param name
	 * @return
	 */
	public Object newDefaultObj(String name) {
		AbstractDynaClassProperty prop = (AbstractDynaClassProperty) this.dynaClass.getDynaProperty(name);
		if (prop instanceof JavaBeanHandler) {
			return prop.getSingleDefaultColValue();
		} else {
			logger.error("方法[newDefaultObj]只支持JavaBeanHandler的类型");
			throw new IllegalArgumentException("方法[newDefaultObj]只支持JavaBeanHandler的类型");
		}
	}

	// ////////////////////////////////////帮助方法///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void checkValue(String name, Object value, AbstractDynaClassProperty prop) {
		if (prop == null) {
			throw new IllegalArgumentException(String.format("不存在名为[%s]的参数。", name));
		}
		Result checkRest = prop.checkValue(value);
		if (!checkRest.isSuc()) {// 检查值是否合法
			throw new IllegalArgumentException(String.format("参数[%s]检查不通过,%s", name, checkRest.getMessage()));
		}
	}

	private void setDefaultColValue() {
		if (this.dynaClass == null) {// TODO 会为空，不明白什么时候出现
			return;
		}
		// 设置好默认值
		AbstractDynaClassProperty[] allProperty = ((CusDynaClass) this.dynaClass).getDynaProperties();
		if (ArrayUtils.isNotEmpty(allProperty)) {
			for (AbstractDynaClassProperty abstractDynaClassProperty : allProperty) {
				abstractDynaClassProperty.setDefaultColValue(this);
			}
		}

	}
}
