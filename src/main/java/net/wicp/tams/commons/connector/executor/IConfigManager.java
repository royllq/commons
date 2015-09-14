package net.wicp.tams.commons.connector.executor;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;

public interface IConfigManager {
	/*****
	 * 通过key得到解析文件，这个留给业务系统来实现，如果有缓存可以给缓存里拿出返回
	 * 
	 * @param appKey
	 * @return
	 */
	public AbstractConfigClass getConfig(String appKey);
	
	/****
	 * 通过appKey获得对应的输入参数Bean
	 * 
	 * @param appKey
	 * @return
	 */
	public CusDynaBean getInputBean(String appKey);
}
