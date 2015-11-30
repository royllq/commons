package net.wicp.tams.commons.connector;

import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.constant.ColProperty;

/***
 * 项目需要定制的输入输出模板
 * 
 * @author andy.zhou
 *
 */
public interface IClientCus {
	/****
	 * 配置输入的Class
	 * 
	 * @param cols
	 */
	public List<Map<ColProperty, String>> confClientInput();

	/***
	 * 配置输出的Class
	 * 
	 * @return
	 */
	public List<Map<ColProperty, String>> confClientOutput();
}
