package net.wicp.tams.commons.connector;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.beans.CusDynaClass;
import net.wicp.tams.commons.connector.beans.property.AbstractDynaClassProperty;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;
import net.wicp.tams.commons.constant.param.conn.Response;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

/****
 * 通讯帮助
 * 
 * @author zhoujunhui
 *
 */
@SuppressWarnings("static-access")
public abstract class HelperConn {
	private static AbstractConfigClass conf = null;
	private static CusDynaClass inputClass = null;// 协议用的输入Bean
	private static CusDynaClass outClass = null;// 协议用的返回Bean
	public final static Integer OK = 1;
	public final static Integer ERROR = 0;

	private static AbstractConfigClass confNull = null;
	static {
		try {
			conf = ConfigClassXml.createConfigClassXml("Conn", HelperConn.class
					.getResourceAsStream("/conf/ClientInfo.xml"));
			inputClass = conf.parserInputNoCI();
			outClass = conf.parserOutNoCI();

			confNull = ConfigClassXml.createConfigClassXml("Conn",
					HelperConn.class.getResourceAsStream("/conf/null.xml"));
			// nullOutBean = confNull.newOutBean(ExceptAll.project_00001);
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	public static CusDynaClass getInputClass() {
		return HelperConn.inputClass;
	}

	public static CusDynaClass getOutClass() {
		return HelperConn.outClass;
	}

	/****
	 * 通过Properties设置客户端信息
	 * 
	 * @param inputobj
	 * @return
	 */
	public static CusDynaBean newControlInfo(Properties inputobj) {
		CusDynaBean retbean = getInputClass().newInstance();
		if (inputobj == null || inputobj.size() == 0) {
			return retbean;
		}
		AbstractDynaClassProperty[] props = inputClass.getDynaProperties();
		for (AbstractDynaClassProperty prop : props) {
			String name = prop.getName();
			if (inputobj.containsKey(name)) {
				retbean.set(name, inputobj.getProperty(name));
			}
		}
		return retbean;
	}

	/***
	 * 通过Map设置
	 * 
	 * @param inputobj
	 * @return
	 */
	public static CusDynaBean newControlInfo(Map<String, String> inputobj) {
		CusDynaBean retbean = getInputClass().newInstance();
		if (MapUtils.isEmpty(inputobj)) {
			return retbean;
		}
		AbstractDynaClassProperty[] props = inputClass.getDynaProperties();
		for (AbstractDynaClassProperty prop : props) {
			String name = prop.getName();
			if (inputobj.containsKey(name)) {
				retbean.set(name, inputobj.get(name));
			}
		}
		return retbean;
	}

	/****
	 * 通过inputBean得到ControlInfo
	 * 
	 * @param inputBean
	 * @return
	 */
	public static CusDynaBean getControlInfo(CusDynaBean inputBean) {
		return (CusDynaBean) inputBean.get(XMLNameSpace.ControlInfo);
	}

	private static CusDynaBean newRespInfo(String msgId, String receiptSystem,
			String receiptApplication) {
		CusDynaBean nullbean = (CusDynaBean) getOutClass().newInstance()
				.newCusDynaBean(Response.respInfo.toString());
		nullbean.set(Response.respInfo.msgId, msgId);
		if (StringUtils.isBlank(msgId)) {
			nullbean.set(Response.respInfo.msgIdResp,
					String.valueOf(new Date().getTime()));
		} else {
			nullbean.set(Response.respInfo.msgIdResp, msgId);
		}
		nullbean.set(Response.respInfo.receiptSystem, receiptSystem);
		nullbean.set(Response.respInfo.receiptApplication, receiptApplication);
		return nullbean;
	}

	public static CusDynaBean newRespInfo(String msgId) {
		return newRespInfo(msgId, ConfigServer.receiptSystem,
				ConfigServer.receiptApplication);
	}

	public static CusDynaBean newRespInfo() {
		return newRespInfo(null, ConfigServer.receiptSystem,
				ConfigServer.receiptApplication);
	}

	public static CusDynaBean getNullOutBean(ExceptAll exceptAll) {
		return confNull.newOutBean(exceptAll);
	}

}
