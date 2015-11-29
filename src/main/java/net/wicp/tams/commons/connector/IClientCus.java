package net.wicp.tams.commons.connector;

import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.constant.ColProperty;

public interface IClientCus {
	/****
	 * 配置输入的Class
	 * 
	 * @param cols
	 */
	public List<Map<ColProperty, String>> confClientInput();

	public List<Map<ColProperty, String>> confClientOutput();
}
