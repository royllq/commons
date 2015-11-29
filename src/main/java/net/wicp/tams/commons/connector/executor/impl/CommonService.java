package net.wicp.tams.commons.connector.executor.impl;

import org.apache.tapestry5.json.JSONObject;
import org.slf4j.Logger;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.connector.ConfigInstance;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;
import net.wicp.tams.commons.connector.executor.IBusiApp;
import net.wicp.tams.commons.connector.executor.IBusiManager;
import net.wicp.tams.commons.connector.executor.IConfigManager;
import net.wicp.tams.commons.connector.executor.IConnInner;
import net.wicp.tams.commons.connector.executor.IConnStr;
import net.wicp.tams.commons.constant.param.conn.Request;
import net.wicp.tams.commons.exception.ExceptAll;

public class CommonService implements IConnInner, IConnStr {
	private static Logger logger = LogHelp.getLogger(CommonService.class);

	private IBusiManager busiManager;

	private IConfigManager configManager;

	private CusDynaBean exeCommon(String appKey, CusDynaBean inputBean, boolean needCheck) {
		if (busiManager == null) {
			logger.error("busiManager需要业务系统的初始化，请确认");
			CusDynaBean nullBean = ConfigInstance.getInstance().getNullOutBean(ExceptAll.project_nonull);
			return nullBean;
		}
		if (configManager == null) {
			logger.error("configManager需要业务系统的初始化，请确认");
			CusDynaBean nullBean = ConfigInstance.getInstance().getNullOutBean(ExceptAll.project_nonull);
			return nullBean;
		}
		AbstractConfigClass conf = configManager.getConfig(appKey);
		CusDynaBean outBean = null;
		if (needCheck) {// 需要检查客户端
			CusDynaBean clientInfo = (CusDynaBean) inputBean.get(XMLNameSpace.ControlInfo);
			if (clientInfo == null) {
				return conf.newOutBean(ExceptAll.conn_nocontrol);
			}
			String msgId = clientInfo.getStrValueByName(Request.msgId);
			outBean = conf.newOutSuc(msgId);
			// TODO 客户端较验
		} else {
			outBean = conf.newOutSuc();
		}
		IBusiApp busiApp = busiManager.getBean(appKey);
		CusDynaBean retBean = busiApp.exe(inputBean, outBean);
		return retBean;
	}

	@Override
	public CusDynaBean exeNoCheck(String appKey, CusDynaBean inputBean) {
		return exeCommon(appKey, inputBean, false);
	}

	@Override
	public CusDynaBean exe(String appKey, CusDynaBean inputBean) {
		return exeCommon(appKey, inputBean, true);
	}

	/***
	 * 支持非Java的渠道接入，API由业务定义，因为它要加上注解等信息
	 * 
	 * @param appKey
	 * @param inputStr
	 * @return
	 */
	@Override
	public String exe(String appKey, String inputStr) {
		if (busiManager == null) {
			logger.error("busiManager需要业务系统的初始化，请确认");
			CusDynaBean nullBean = ConfigInstance.getInstance().getNullOutBean(ExceptAll.project_nonull);
			return nullBean.getJsonObj().toString(true);
		}
		AbstractConfigClass conf = configManager.getConfig(appKey);
		CusDynaBean inputBean = conf.newInputBean();
		inputBean.setByJson(new JSONObject(inputStr));
		CusDynaBean retBean = this.exe(appKey, inputBean);
		return retBean.getJsonObj().toString(true);
	}

	public IBusiManager getBusiManager() {
		return busiManager;
	}

	public void setBusiManager(IBusiManager busiManager) {
		this.busiManager = busiManager;
	}

	public IConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(IConfigManager configManager) {
		this.configManager = configManager;
	}

}
