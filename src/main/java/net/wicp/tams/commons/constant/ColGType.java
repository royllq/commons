package net.wicp.tams.commons.constant;

/**
 * list 与 array 相同
 * */
public enum ColGType {
	single("单值"), array("数组"), map("map"),list("list");
	private String desc;

	private ColGType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public static ColGType getByName(String name) {
		for (ColGType colGType : ColGType.values()) {
			if (colGType.name().equals(name)) {
				return colGType;
			}
		}
		return null;
	}
}
