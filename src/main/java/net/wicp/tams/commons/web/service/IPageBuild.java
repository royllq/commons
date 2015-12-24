package net.wicp.tams.commons.web.service;

import javax.servlet.http.HttpServletRequest;

import net.wicp.tams.commons.web.PageAssist;

/***
 * 构造翻页辅助信息 单例，如果业务上要屏蔽request，参数，请封装这个实例，但业务的实例就要求每线程一个实例，不是默认的单例
 * 
 * @author Administrator
 *
 */
public interface IPageBuild {
	/***
	 * 跟据请求构建的翻页
	 * 
	 * @return
	 */
	public PageAssist build(HttpServletRequest request);

}
