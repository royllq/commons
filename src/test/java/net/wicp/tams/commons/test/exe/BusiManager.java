package net.wicp.tams.commons.test.exe;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.connector.executor.IBusiApp;
import net.wicp.tams.commons.connector.executor.IBusiManager;
import net.wicp.tams.commons.connector.executor.IConfigManager;
import net.wicp.tams.commons.exception.ProjectException;

import org.apache.commons.lang.StringUtils;

public class BusiManager implements IBusiManager, IConfigManager {
	private static Map<String, AbstractConfigClass> configmap = new HashMap<String, AbstractConfigClass>();
	private static Map<String, IBusiApp> busimap = new HashMap<String, IBusiApp>();
	private Properties props;
	protected static final String dir = "D:/workspace/commons-util/src/test/resources/connector";

	public BusiManager() {
		this.props = IOUtil.fileToProperties("/connector/service.properties");
	}

	@Override
	public IBusiApp getBean(String appKey) {
		if (StringUtils.isBlank(appKey)) {
			return null;
		}
		IBusiApp busi = busimap.get(appKey);
		if (busi == null) {
			Map<String, String> appprop = getRedisServerPropByKey(props, appKey);
			String classname = appprop.get("classname");
			try {
				busi = (IBusiApp) Class.forName(classname).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return busi;
	}

	@Override
	public AbstractConfigClass getConfig(String appKey) {
		if (StringUtils.isBlank(appKey)) {
			return null;
		}
		AbstractConfigClass retobj = configmap.get(appKey);
		if (retobj == null) {
			Map<String, String> appprop = getRedisServerPropByKey(props, appKey);
			try {
				retobj = ConfigClassXml.createConfigClassXml("TestString", dir,
						appprop.get("xml"));
				configmap.put(appKey, retobj);
			} catch (ProjectException e) {
				e.printStackTrace();
			}
		}
		return retobj;
	}

	private static Map<String, String> getRedisServerPropByKey(
			final Properties prop, final String key) {
		Set<Object> propKeys = prop.keySet();
		Map<String, String> retMap = new HashMap<String, String>();
		for (Object object : propKeys) {
			String tempKey = String.valueOf(object);
			String tempStr = String.format("%s.", key);
			if (tempKey.startsWith(tempStr)) {
				retMap.put(tempKey.replace(tempStr, ""),
						prop.getProperty(tempKey));
			}
		}
		return retMap;
	}

	@Override
	public CusDynaBean getInputBean(String appKey) {
		return this.getConfig(appKey).newInputBean();
	}

}
