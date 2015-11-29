package net.wicp.tams.commons.test;

import net.wicp.tams.commons.apiext.IOUtil;

import org.apache.log4j.PropertyConfigurator;

public class AbsToDynaBean {
	protected static final String dir = "D:/source/github/commons/src/test/resources/connector";
	static {
		PropertyConfigurator.configure(IOUtil.class
				.getResourceAsStream("/log_util.properties"));
	}

}
