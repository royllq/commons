package net.wicp.tams.commons.connector.beans;

import java.util.HashMap;
import java.util.Map;

import net.wicp.tams.commons.connector.beans.property.AbstractDynaClassProperty;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.constant.ColProperty;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONObject;

public class CusDynaClass extends BasicDynaClass implements Cloneable {
	private static final long serialVersionUID = 1L;
	private transient java.util.HashMap<String, AbstractDynaClassProperty> alaisMapping = new HashMap<String, AbstractDynaClassProperty>();

	/**
	 * 构造函数,私有
	 * */
	@SuppressWarnings("rawtypes")
	private CusDynaClass(String arg0, Class arg1, DynaProperty[] arg2) {
		super(arg0, arg1, arg2);
	}

	/**
	 * 工厂方法产生Class
	 * */
	public static CusDynaClass createCusDynaClass(String name,
			AbstractDynaClassProperty[] propertyes) {
		propertyes = propertyes == null ? new AbstractDynaClassProperty[0]
				: propertyes;
		CusDynaClass retDynaClass = new CusDynaClass(name, CusDynaBean.class,
				propertyes);
		return retDynaClass;
	}

	/**
	 * 通过名字找到字段
	 * */
	public AbstractDynaClassProperty findProperty(String name) {
		if (this.propertiesMap.containsKey(name)) {
			return (AbstractDynaClassProperty) this.propertiesMap.get(name);
		}
		return null;
	}

	/**
	 * 通过别名找到字段
	 * */
	public AbstractDynaClassProperty findPropertyByAlais(String alais) {
		return this.alaisMapping.containsKey(alais) ? this.alaisMapping
				.get(alais) : null;
	}

	public HashMap<String, AbstractDynaClassProperty> getAllPropertys() {
		HashMap<String, AbstractDynaClassProperty> ret = new HashMap<>();
		for (String cla : this.propertiesMap.keySet()) {
			ret.put(cla,
					(AbstractDynaClassProperty) this.propertiesMap.get(cla));
		}
		return ret;
	}

	@Override
	protected CusDynaClass clone() throws CloneNotSupportedException {
		return (CusDynaClass) super.clone();
	}

	@Override
	public CusDynaBean newInstance() {
		CusDynaBean dynaBean = new CusDynaBean(this);
		return dynaBean;
	}

	@Override
	public AbstractDynaClassProperty[] getDynaProperties() {
		return (AbstractDynaClassProperty[]) super.getDynaProperties();
	}

}
