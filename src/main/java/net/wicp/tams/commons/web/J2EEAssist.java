package net.wicp.tams.commons.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.callback.IConvertValue;

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

	/***
	 * 返回json数据
	 * 
	 * @param response
	 * @param jsonMsg
	 */
	public static void returnJsonResponse(HttpServletResponse response, String jsonMsg) {
		response.setContentType("text/html");
		try {
			PrintWriter out = response.getWriter();
			out.print(jsonMsg);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 返回json数据
	 * 
	 * @param response
	 * @param optResult
	 *            要返回的结果封装
	 */
	public static void returnJsonResponse(HttpServletResponse response, Result optResult) {
		returnJsonResponse(response, optResult.getJsonObj().toString(true));
	}



}
