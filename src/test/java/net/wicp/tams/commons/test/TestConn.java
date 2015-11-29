package net.wicp.tams.commons.test;

import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.connector.ConfigInstance;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.constant.param.conn.Request;
import net.wicp.tams.commons.constant.param.conn.Response;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

/***
 * 输入Bean、输出Bean通讯时附加信息测试
 * 
 * @author zhoujunhui
 *
 */
@SuppressWarnings("static-access")
public class TestConn extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestString", dir, "TestString.xml");
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testClientInputBean() {
		CusDynaBean inputBean = ConfigInstance.getInstance().getInputClass().newInstance();
		inputBean.set(Request.msgId, "aaa");
		inputBean.set(Request.version, "1");
		Assert.assertEquals(inputBean.get(Request.version), "1");

	}

	@Test
	public void testClientOutBean() {
		CusDynaBean outBean = ConfigInstance.getInstance().getOutClass().newInstance();
		outBean.set(Response.errorCode, "10000");
		CusDynaBean resinfo = outBean.newCusDynaBean(Response.respInfo.toString());
		resinfo.set(Response.respInfo.msgId, "aaa");
		Assert.assertEquals(resinfo.get(Response.respInfo.msgId), "aaa");
	}

	@Test
	public void testInitClientBean() {
		Properties clientInfo = IOUtil.fileToProperties("/connector/defaultClient.properties");
		CusDynaBean clientbean = ConfigInstance.getInstance().newControlInfo(clientInfo);
		Assert.assertEquals(clientbean.getStrValueByName(Request.msgId), "aaaa");
	}

	@Test
	public void testInputBean() {
		Properties clientInfo = IOUtil.fileToProperties("/connector/defaultClient.properties");
		CusDynaBean inputBean = conf.newInputBean(clientInfo);
		CusDynaBean clientbean = ConfigInstance.getInstance().getControlInfo(inputBean);
		Assert.assertEquals(clientbean.getStrValueByName(Request.msgId), "aaaa");
	}

	@Test
	public void testOutBean() {
		CusDynaBean outBean = conf.newOutBean(ExceptAll.project_undefined, "34555566");
		String errorCode = (String) outBean.get(Response.errorCode);
		Assert.assertEquals(errorCode, "param_00000");
	}

}
