package net.wicp.tams.commons.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.wicp.tams.commons.connector.HelperConn;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;
import net.wicp.tams.commons.connector.executor.IBusiManager;
import net.wicp.tams.commons.connector.executor.IConfigManager;
import net.wicp.tams.commons.connector.executor.IConnTreaty;
import net.wicp.tams.commons.connector.executor.impl.CommonService;
import net.wicp.tams.commons.constant.param.conn.Request;
import net.wicp.tams.commons.constant.param.conn.Response;
import net.wicp.tams.commons.test.exe.BusiManager;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestExecutor {
	private static IBusiManager busi = null;
	private static IConfigManager configManager = null;

	@BeforeClass
	public static void initCalss() {
		busi = new BusiManager();
	}

	@Test
	public void testaaa() {
		IConnTreaty treaty = new CommonService() {
		};
		String appKey = "TestString";
		CusDynaBean inputBean = configManager.getInputBean(appKey);
		Map<String, String> clientinfo = new HashMap<String, String>();
		clientinfo.put(Request.msgId, String.valueOf(new Date().getTime()));
		clientinfo.put(Request.senderApplication, "PA_CD");
		clientinfo.put(Request.senderSystem, "PA_SY");
		CusDynaBean ci = HelperConn.newControlInfo(clientinfo);
		inputBean.set(XMLNameSpace.ControlInfo, ci);

		CusDynaBean retBean = treaty.exe(appKey, inputBean);
		retBean.set(Response.result, 1);
	}
}
