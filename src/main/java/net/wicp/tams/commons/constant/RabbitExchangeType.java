package net.wicp.tams.commons.constant;

public enum RabbitExchangeType {
	direct("直接", "direct-exchange-example"), topic("主题"), headers("标题"), fanout("分列");

	private final String desc;
	private String defaultExchange;

	private RabbitExchangeType(String desc) {
		this.desc = desc;
	}

	private RabbitExchangeType(String desc, String defaultExchange) {
		this.desc = desc;
		this.defaultExchange = defaultExchange;
	}

	public String getDesc() {
		return desc;
	}

	public String getDefaultExchange() {
		return defaultExchange;
	}

	public void setDefaultExchange(String defaultExchange) {
		this.defaultExchange = defaultExchange;
	}
}
