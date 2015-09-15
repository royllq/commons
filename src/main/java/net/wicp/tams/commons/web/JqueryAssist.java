package net.wicp.tams.commons.web;

import java.util.List;

import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mvel2.templates.TemplateRuntime;

public abstract class JqueryAssist {
	/**
	 * 把List转为Jquery select的model
	 * 
	 * @param fromList
	 *            要转为json的List
	 * @param nameFiled
	 *            label的字段名
	 * @param codeFiled
	 *            code的字段名
	 * @param idName
	 *            属性字段名
	 * @return String
	 *         [{label:'男',value:'M'},{label:'女',value:'F'},{label:'未知',value:''}
	 *         ]
	 * @throws ProjectException
	 * */
	public static String getJsonFromList(List<Object> fromList,
			String nameFiled, String codeFiled, String idName)
			throws ProjectException {
		if (CollectionUtils.isEmpty(fromList) || StringUtils.isBlank(nameFiled)
				|| StringUtils.isBlank(codeFiled)) {
			throw new ProjectException(ExceptAll.param_error, "要解析的参数错误");
		}
		if (StringUtils.isBlank(idName)) {
			idName = codeFiled;// 如果缺少ID则把code字段做为ＩＤ
		}
		String jsonTempStr = "@['{label:\"'+" + nameFiled + "+'\",value:\"'+"
				+ codeFiled + "+'\",id:\"'+" + idName + "+'\"},']";
		StringBuffer returnBuff = new StringBuffer();
		for (Object object : fromList) {
			returnBuff.append(TemplateRuntime.eval(jsonTempStr, object));
		}
		returnBuff.delete(returnBuff.length() - 1, returnBuff.length());// 去除最后一个“,”
		// returnBuff.append("]}");
		return returnBuff.toString();
	}
}
