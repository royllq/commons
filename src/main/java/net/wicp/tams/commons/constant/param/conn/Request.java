package net.wicp.tams.commons.constant.param.conn;

import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;

/***
 * 请求dynaBean系统会封装的字段名称列表
 * 
 * @author andy.zhou
 *
 */
public abstract class Request {
	/***
	 * 消息唯一标识符，缺省会使用时间戳
	 */
	public static final String msgId = "msgId";
	/***
	 * 发送方系统名称
	 */
	public static final String senderSystem = "senderSystem";
	/***
	 * 发送方应用名称
	 */
	public static final String senderApplication = "senderApplication";
	/***
	 * 版本，用于控制解析的过程，以及适应未来可能出现的变化
	 */
	public static final String version = "version";
	/***
	 * 请求的命令代码
	 */
	public static final String requestCommand = "requestCommand";
	/***
	 * 控制信息
	 */
	public static final String controlInfo = XMLNameSpace.ControlInfo;
}
