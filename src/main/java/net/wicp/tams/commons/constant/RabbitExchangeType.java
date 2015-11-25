package net.wicp.tams.commons.constant;

public enum RabbitExchangeType {
	direct("直接"), topic("主题"), headers("标题"), fanout("分列");

	private final String desc;

	private RabbitExchangeType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
