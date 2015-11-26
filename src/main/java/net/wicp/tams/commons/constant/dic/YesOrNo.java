package net.wicp.tams.commons.constant.dic;

import org.apache.commons.lang3.StringUtils;

import net.wicp.tams.commons.constant.dic.intf.IEnumCombobox;

/***
 * 是与否枚举
 * 
 * @author andy.zhou
 * 
 */
public enum YesOrNo implements IEnumCombobox {
	yes("是"), no("否");

	private final String desc;

	private YesOrNo(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return this.name();
	}

	@Override
	public String getDesc_zh() {
		return this.name();
	}

	@Override
	public String getDesc_en() {
		return this.name();
	}

	public static YesOrNo find(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (YesOrNo ele : YesOrNo.values()) {
			if (name.equalsIgnoreCase(ele.name())) {
				return ele;
			}
		}
		return null;
	}
}
