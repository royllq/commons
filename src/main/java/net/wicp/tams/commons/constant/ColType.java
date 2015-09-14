package net.wicp.tams.commons.constant;

import org.apache.commons.lang.ArrayUtils;

/**
 * 动态Bean现在支持的类型： eg: <COL name="orderItem" alias="orderItem" type="dynaBean">
 * type值必须为ColType。name 如果是其它数据类型可以传string，暂不支持二进制类型
 * */
public enum ColType {
	dynaBean("动态Bean", true, null), javaBean("javaBean", false,
			new String[] { "className" }), bytes("二进制类型", false, null), integer(
			"整型", false, null), string("字符型", false, null), datetime("时间型",
			false, null), enums("枚举类型", false, new String[] { "className" });
	private final String desc;
	private final boolean needXPath;// 是否需要记录Xpath路径
	private final String[] needColsStr;

	public ColProperty[] getNeedCols() {// 在构造函数不能做这些，不明白
		if (ArrayUtils.isNotEmpty(needColsStr)) {
			ColProperty[] ret = new ColProperty[needColsStr.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = ColProperty.getByName(needColsStr[i]);
			}
			return ret;
		}
		return new ColProperty[] {};
	}

	private ColType(String desc, boolean needXPath, String[] needColsStr) {
		this.desc = desc;
		this.needXPath = needXPath;
		this.needColsStr = needColsStr;

	}

	public String getDesc() {
		return desc;
	}

	public boolean isNeedXPath() {
		return this.needXPath;
	}

	public static ColType getByName(String name) {
		for (ColType colType : ColType.values()) {
			if (colType.name().equals(name)) {
				return colType;
			}
		}
		return null;
	}
}
