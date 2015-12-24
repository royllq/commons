package net.wicp.tams.commons.web.service.impl;

import javax.servlet.http.HttpServletRequest;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.web.PageAssist;
import net.wicp.tams.commons.web.service.IPageBuild;

public class PageBuild implements IPageBuild {

	private final String fieldName_page = Conf.get("page.fieldName_page");

	private final String fieldName_rows = Conf.get("page.fieldName_rows");

	private final String fieldName_allNum = Conf.get("page.fieldName_allNum");

	@Override
	public PageAssist build(HttpServletRequest request) {
		String page = request == null ? Conf.get("page.fieldValue_page") : request.getParameter(fieldName_page);
		String rows = request == null ? Conf.get("page.fieldValue_rows") : request.getParameter(fieldName_rows);
		String allNumStr = request == null ? Conf.get("page.fieldValue_allNum")
				: request.getParameter(fieldName_allNum);
		int pageNo = StringUtil.isNull(page) ? 1 : Integer.parseInt(page);
		int rowsNum = StringUtil.isNull(rows) ? 10 : Integer.parseInt(rows);
		int allNum = StringUtil.isNull(allNumStr) ? -1 : Integer.parseInt(allNumStr);
		return new PageAssist(rowsNum, pageNo, allNum);
	}

}
