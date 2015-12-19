package net.wicp.tams.commons.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public abstract class J2EEAssist {

	/***
	 * 通过response得到输出流
	 * 
	 * @param response
	 *            响应信息
	 * @param fileName
	 *            导出excel文件的名字
	 * @param isclear
	 *            清空输出流
	 * @return 输出流
	 */
	public static OutputStream getExcelOS(HttpServletResponse response, String fileName, boolean isclear) {
		try {
			OutputStream os = response.getOutputStream();
			if (isclear) {
				response.reset();// 清空输出流
			}
			response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");// 设定输出文件头
			response.setContentType("application/msexcel");// 定义输出类型
			return os;
		} catch (IOException e) {
			return null;
		}
	}
}
