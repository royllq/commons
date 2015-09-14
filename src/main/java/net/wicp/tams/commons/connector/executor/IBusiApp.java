package net.wicp.tams.commons.connector.executor;

import net.wicp.tams.commons.connector.beans.CusDynaBean;

public interface IBusiApp {
	/***
	 * 业务必须实现的接口
	 * 
	 * @param inputBean
	 *            输入参数
	 * @param outBeanOri
	 *            输出参数的原型，最后返回的结果是在原型基础上设置好返回值
	 * @return
	 */
	public CusDynaBean exe(CusDynaBean inputBean, CusDynaBean outBeanOri);
}
