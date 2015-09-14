package net.wicp.tams.commons.constant;

/**
 * XML配置文件 COL的属性枚举 format：如果是时间则为时间格式，字符串则是正则表达式
 * */
public enum ColProperty {
	name("列名", null, ""), 
	isnull("是否为空", Boolean.TRUE, Boolean.TRUE.toString()), 
	alias("列别名",null, ""), 
	gtype("集合类型", ColGType.single, ColGType.single.name()), 
	type("类型",ColType.string, ColType.string.name()), 
	defaultValue("默认值", null, ""), 
	length("字符串长度", null, ""), 
	max("最大值", null, ""), 
	min("最小值", null, ""), 
	format("格式", null, ""), 
	xpath("xml路径", null, ""), 
	className("bean的Class名",null, null), 
	valueName("用bean赋值时的字段名", null, null), 
	strict("是否要求严格比较class,非严格只要比较字段名相同就行了，主要用于DynaBeanHander",Boolean.TRUE, Boolean.TRUE.toString()),
	convert("optionGroup国际化显示optionitem用", null, null);
	private String desc;
	private Object defaultSelValue;// 默认值
	private String defaultSelStringValue;// 默认值的字符串

	private ColProperty(String desc, Object defaultSelValue,
			String defaultSelStringValue) {
		this.desc = desc;
		this.defaultSelValue = defaultSelValue;
		this.defaultSelStringValue = defaultSelStringValue;
	}

	public String getDesc() {
		return desc;
	}

	public Object getDefaultSelValue() {
		return defaultSelValue;
	}

	public String getDefaultSelStringValue() {
		return defaultSelStringValue;
	}

	public static ColProperty getByName(String name) {
		for (ColProperty colProperty : ColProperty.values()) {
			if (colProperty.name().equals(name)) {
				return colProperty;
			}
		}
		return null;
	}
}
