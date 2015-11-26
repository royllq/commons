package net.wicp.tams.commons.connector.executor;

import net.wicp.tams.commons.connector.beans.CusDynaBean;

/***
 * 客户端执行的接口,为rmi等Java分布式渠道的连接实现接口
 * 
 * @author zhoujunhui
 *
 */
public interface IConnTreaty {
	/****
	 * 通过appKey获得对应的输入参数Bean
	 * 
	 * @param appKey
	 * @return
	 */
	public CusDynaBean exe(String appKey, CusDynaBean inputBean);
}
