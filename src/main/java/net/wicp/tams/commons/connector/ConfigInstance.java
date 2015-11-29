package net.wicp.tams.commons.connector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.beans.CusDynaClass;
import net.wicp.tams.commons.connector.beans.property.AbstractDynaClassProperty;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.constant.param.conn.Request;
import net.wicp.tams.commons.constant.param.conn.Response;
import net.wicp.tams.commons.exception.IExcept;
import net.wicp.tams.commons.exception.ProjectException;

public class ConfigInstance {
	private static Logger logger = LogHelp.getLogger(ConfigInstance.class);
	private static volatile ConfigInstance INSTANCE;
	public final static Integer OK = 1;
	public final static Integer ERROR = 0;
	private static Object lockobj = new Object();
	// 配置属性
	private CusDynaClass inputClass = null;// 协议用的输入Bean
	private CusDynaClass outClass = null;// 协议用的返回Bean
	private AbstractConfigClass confNull = null;// 空的动态Bean

	public static final ConfigInstance getInstance() {
		if (INSTANCE == null) {
			synchronized (lockobj) {
				if (INSTANCE == null) {
					ConfigInstance tempobj = new ConfigInstance();
					InputStream clientInputStream = ConfigInstance.class.getResourceAsStream("/conf/ClientInfo.xml");
					InputStream nullInputStream = ConfigInstance.class.getResourceAsStream("/conf/null.xml");
					try {
						AbstractConfigClass conf = ConfigClassXml.createConfigClassXml("default.client",
								clientInputStream);
						tempobj.inputClass = conf.parserInputNoCI();
						tempobj.outClass = conf.parserOutNoCI();

						String cusconf = Conf.get("connector.dynabean.client.cus.conf");// 有配置客户自定义的模板
						if (tempobj.inputClass != null && StringUtils.isNotBlank(cusconf)) {
							try {
								IClientCus clientCus = (IClientCus) Class.forName(cusconf).newInstance();
								List<Map<ColProperty, String>> input = clientCus.confClientInput();
								if (CollectionUtils.isNotEmpty(input)) {
									tempobj.inputClass = conf.createNewClass(tempobj.inputClass, null, input);
								}
								List<Map<ColProperty, String>> output = clientCus.confClientOutput();
								if (CollectionUtils.isNotEmpty(output)) {
									tempobj.outClass = conf.createNewClass(tempobj.outClass, null, output);
								}
							} catch (Exception e) {
								logger.error("客户自定义的模板错误", e);
							}

						}
						tempobj.confNull = ConfigClassXml.createConfigClassXml("default.null", nullInputStream);
					} catch (ProjectException e) {
						e.printStackTrace();
					} finally {
						try {
							if (clientInputStream != null) {
								clientInputStream.close();
							}
							if (nullInputStream != null) {
								nullInputStream.close();
							}
						} catch (IOException e) {
							logger.error("流关闭失败", e);
						}

					}
					INSTANCE = tempobj;
				}
			}
		}
		return INSTANCE;
	}

	public static void destroy() {
		AbstractConfigClass.clearCache();
		INSTANCE.inputClass = null;
		INSTANCE.outClass = null;
		INSTANCE.confNull = null;
		INSTANCE = null;
	}

	public CusDynaClass getInputClass() {
		return this.inputClass;
	}

	public CusDynaClass getOutClass() {
		return this.outClass;
	}

	/****
	 * 通过Properties设置客户端信息
	 * 
	 * @param inputobj
	 * @return
	 */
	public CusDynaBean newControlInfo(Properties inputobj) {
		CusDynaBean retbean = this.inputClass.newInstance();
		if (inputobj == null || inputobj.size() == 0) {
			return retbean;
		}
		AbstractDynaClassProperty[] props = this.inputClass.getDynaProperties();
		for (AbstractDynaClassProperty prop : props) {
			String name = prop.getName();
			if (inputobj.containsKey(name)) {
				retbean.set(name, inputobj.getProperty(name));
			}
		}
		return retbean;
	}

	public CusDynaBean newControlInfo() {
		CusDynaBean retbean = this.inputClass.newInstance();
		retbean.set(Request.senderSystem, Conf.get("connector.dynabean.client.system"));
		retbean.set(Request.senderApplication, Conf.get("connector.dynabean.client.application"));
		retbean.set(Request.version, Conf.get("connector.dynabean.client.version"));
		return retbean;
	}

	/***
	 * 通过Map设置
	 * 
	 * @param inputobj
	 * @return
	 */
	public CusDynaBean newControlInfo(Map<String, String> inputobj) {
		CusDynaBean retbean = this.inputClass.newInstance();
		if (MapUtils.isEmpty(inputobj)) {
			return retbean;
		}
		AbstractDynaClassProperty[] props = this.inputClass.getDynaProperties();
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

	private CusDynaBean newRespInfo(String msgId, String receiptSystem, String receiptApplication) {
		CusDynaBean nullbean = (CusDynaBean) this.outClass.newInstance().newCusDynaBean(Response.respInfo.toString());
		nullbean.set(Response.respInfo.msgId, msgId);
		if (StringUtils.isBlank(msgId)) {
			nullbean.set(Response.respInfo.msgIdResp, String.valueOf(new Date().getTime()));
		} else {
			nullbean.set(Response.respInfo.msgIdResp, msgId);
		}
		nullbean.set(Response.respInfo.receiptSystem, receiptSystem);
		nullbean.set(Response.respInfo.receiptApplication, receiptApplication);
		return nullbean;
	}

	public CusDynaBean newRespInfo(String msgId) {
		return newRespInfo(msgId, Conf.get("connector.dynabean.client.system"),
				Conf.get("connector.dynabean.client.application"));
	}

	public CusDynaBean newRespInfo() {
		return newRespInfo(null, Conf.get("connector.dynabean.client.system"),
				Conf.get("connector.dynabean.client.application"));
	}

	public CusDynaBean getNullOutBean(IExcept except) {
		return this.confNull.newOutBean(except);
	}

}
