package net.wicp.tams.commons.constant.dic;

import org.apache.commons.lang3.StringUtils;

import net.wicp.tams.commons.constant.dic.intf.IEnumCombobox;

/***
 * 性别
 * 
 * @author andy.zhou
 * 
 */
public enum Gender implements IEnumCombobox {
	M("男", "Male"), F("女", "Female"), U("未知", "Unknown");
	private final String desc;
	private final String desc_en;

	private Gender(String desc, String desc_en) {
		this.desc = desc;
		this.desc_en = desc_en;
	}

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return this.name();
	}

	@Override
	public String getDesc_zh() {
		return this.desc;
	}

	@Override
	public String getDesc_en() {
		return this.desc_en;
	}

	public static Gender find(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (Gender ele : Gender.values()) {
			if (name.equalsIgnoreCase(ele.name())) {
				return ele;
			}
		}
		return null;
	}
}
