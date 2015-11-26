package net.wicp.tams.commons.connector.config.xmlParser;

import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

@SuppressWarnings("serial")
public class XMLConfigurationExt extends XMLConfiguration {
	public List<ConfigurationNode> fetchNodeList(String key) {
		return super.fetchNodeList(key);
	}
}
