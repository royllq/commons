package net.wicp.tams.commons.connector.executor;

import net.wicp.tams.commons.connector.config.AbstractConfigClass;

public interface IBusiManager {
	/***
	 * 通过key得到执行业务用的Bean,这个留给业务系统来实现，如果业务系统用spring，则可以把返回spring的Bean
	 * 
	 * @param appKey
	 * @return
	 */
	public IBusiApp getBean(String appKey);

	
}
