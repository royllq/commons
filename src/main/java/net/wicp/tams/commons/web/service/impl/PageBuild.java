package net.wicp.tams.commons.web.service.impl;

import javax.servlet.http.HttpServletRequest;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.web.PageAssist;
import net.wicp.tams.commons.web.service.IPageBuild;

public class PageBuild implements IPageBuild {

	private String fieldName_page = Conf.get("page.fieldName_page");

	private String fieldName_rows = Conf.get("page.fieldName_rows");

	private String fieldName_allNum = Conf.get("page.fieldName_allNum");

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

	@Override
	public PageAssist build(int pageSize, int pageNo) {
		return new PageAssist(pageSize, pageNo, -1);
	}

	@Override
	public PageAssist build(int pageSize, int pageNo, int allNum) {
		return new PageAssist(pageSize, pageNo, allNum);
	}

	@Override
	public PageAssist build() {
		return build(null);
	}

	/////////////////////// get
	/////////////////////// set方法区///////////////////////////////////////////////////
	public String getFieldName_page() {
		return fieldName_page;
	}

	public void setFieldName_page(String fieldName_page) {
		this.fieldName_page = fieldName_page;
	}

	public String getFieldName_rows() {
		return fieldName_rows;
	}

	public void setFieldName_rows(String fieldName_rows) {
		this.fieldName_rows = fieldName_rows;
	}

	public String getFieldName_allNum() {
		return fieldName_allNum;
	}

	public void setFieldName_allNum(String fieldName_allNum) {
		this.fieldName_allNum = fieldName_allNum;
	}

}
