package net.wicp.tams.commons.connector;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.wicp.tams.commons.constant.param.conn.Request;

/***
 * 客户端需要设置的变量
 * 
 * @author zhoujunhui
 *
 */
public abstract class ConfigClient {
	public static String senderSystem = "senderSystem";
	public static String senderApplication = "senderApplication";

	public static final Map<String, String> getClientinfo() {
		Map<String, String> clientinfo = new HashMap<String, String>();
		clientinfo.put(Request.senderSystem, senderSystem);
		clientinfo.put(Request.senderApplication, senderApplication);
		clientinfo.put(Request.msgId, String.valueOf(new Date().getTime()));
		clientinfo.put(Request.version, "1.0");// 版本号默认为1.0
		return clientinfo;
	}

	public static final Map<String, String> getClientinfo(String version) {
		Map<String, String> retobj = getClientinfo();
		retobj.put(Request.version, version);
		return retobj;
	}
}
