package net.wicp.tams.commons.constant;

import net.wicp.tams.commons.apiext.StringUtil;

/****
 * 过滤模式
 * 
 * @author andy.zhou
 *
 */
public enum FilterPattern {
	left("左边匹配"), right("右边匹配"), contains("任意匹配"), length("长度相等匹配");

	private final String desc;

	private FilterPattern(String desc) {
		this.desc = desc;
	}

	public boolean check(String checkStr, String fitStr) {
		boolean ret = false;
		checkStr = StringUtil.trimSpace(checkStr);
		fitStr = StringUtil.trimSpace(fitStr);
		switch (this) {
		case left:
			ret = checkStr.startsWith(fitStr);
			break;
		case right:
			ret = checkStr.endsWith(fitStr);
			break;
		case contains:
			ret = checkStr.contains(fitStr);
			break;
		case length:
			ret = checkStr.startsWith(fitStr);
			break;
		default:
			break;
		}
		return ret;
	}

	public static FilterPattern getByName(String pathpath) {
		if (StringUtil.isNull(pathpath)) {// 默认是任意匹配
			return contains;
		}
		for (FilterPattern ele : FilterPattern.values()) {
			if (pathpath.equalsIgnoreCase(ele.name())) {
				return ele;
			}
		}
		return contains;
	}

	public String getDesc() {
		return desc;
	}

}
