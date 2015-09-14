package net.wicp.tams.commons.connector.executor;

import net.wicp.tams.commons.connector.beans.CusDynaBean;

/****
 * 服务端内部执行的接口，比客户端（IConnTreaty）接口会多一些方法
 * 
 * @author zhoujunhui
 *
 */
public interface IConnInner extends IConnTreaty {
	/****
	 * 内部服务的调用，不用检查客户端
	 * 
	 * @param appKey
	 * @param inputBean
	 * @return
	 */
	public CusDynaBean exeNoCheck(String appKey, CusDynaBean inputBean);
}
