package net.wicp.tams.commons.test.exe;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.executor.IBusiApp;
import net.wicp.tams.commons.constant.param.conn.Response;

public class TestStringTest implements IBusiApp {

	@Override
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri) {
		String single=inputBean.getStrValueByName("single");
		System.out.println("single="+single);
		outBeanOri.set(Response.result, 1);
		outBeanOri.set(Response.errorCode, "aaaaaaaa");
		return outBeanOri;
	}

}
