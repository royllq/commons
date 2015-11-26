package net.wicp.tams.commons.constant.param.conn;

public class RespInfo {
	/***
	 * 接收方系统名称
	 */
	public static final String receiptSystem = "receiptSystem";
	/***
	 * 接收方应用名称
	 */
	public static final String receiptApplication = "receiptApplication";
	/***
	 * 请求的消息Id
	 */
	public static final String msgId = "msgId";
	/***
	 * 回应的消息唯一标识符，缺省会使用对方消息ID
	 */
	public static final String msgIdResp = "msgIdResp";

	@Override
	public String toString() {
		return "respInfo";
	}
}
