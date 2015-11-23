package net.wicp.tams.commons.web.service;

import javax.servlet.http.HttpServletRequest;

import net.wicp.tams.commons.web.PageAssist;

/***
 * 构造翻页辅助信息 每线程一个实例，不是默认的单例
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
	
	/***
	 * 构造默认的翻页
	 * 
	 * @return
	 */
	public PageAssist build();

	/***
	 * 
	 * @param pageSize
	 *            每页大小
	 * @param pageNo
	 *            第几页
	 * @return
	 */
	public PageAssist build(int pageSize, int pageNo);

	/***
	 * 
	 * @param pageSize
	 *            页面大小
	 * @param pageNo
	 *            第几页
	 * @param allNum
	 *            总记录数
	 * @return
	 */
	public PageAssist build(int pageSize, int pageNo, int allNum);

}
