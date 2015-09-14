package net.wicp.tams.commons.constant.param.conn;

import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;

public abstract class Request {
	public static final String msgId = "msgId";
	public static final String senderSystem = "senderSystem";
	public static final String senderApplication = "senderApplication";
	public static final String version = "version";
	public static final String requestCommand = "requestCommand";
	public static final String controlInfo = XMLNameSpace.ControlInfo;// 控制信息
}
