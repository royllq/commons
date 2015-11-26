package net.wicp.tams.commons.connector.config.xmlParser;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.lang.StringUtils;

import net.wicp.tams.commons.apiext.XmlUtil;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.constant.ColType;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

public class ConfigClassXml extends AbstractConfigClass {

	private XMLConfigurationExt xml;

	private ConfigClassXml(String className, String path, String xmlName)
			throws ProjectException {
		super(className);
		File inputFile = new File(path, xmlName);
		try {
			java.io.Reader reader = new java.io.FileReader(inputFile);
			this.xml = new XMLConfigurationExt();
			this.xml.setEncoding("UTF-8");
			this.xml.setExpressionEngine(new XPathExpressionEngine());// 设置Xpath解析器
			this.xml.load(reader);
		} catch (Exception e) {
			throw new ProjectException(ExceptAll.Project_default, "加载XML文件错误");
		}
		AbstractConfigClass.parseClassMap.put(className, this);
	}

	private ConfigClassXml(String className, InputStream stream)
			throws ProjectException {
		super(className);
		try {
			this.xml = new XMLConfigurationExt();
			this.xml.setEncoding("UTF-8");
			this.xml.setExpressionEngine(new XPathExpressionEngine());// 设置Xpath解析器
			this.xml.load(stream);
		} catch (Exception e) {
			throw new ProjectException(ExceptAll.Project_default,
					"xml流组装成XML错误");
		}
		AbstractConfigClass.parseClassMap.put(className, this);
	}

	public static ConfigClassXml createConfigClassXml(String className,
			String path, String xmlName) throws ProjectException {
		AbstractConfigClass retobj = AbstractConfigClass.parseClassMap
				.get(className);
		if (retobj == null) {
			retobj = new ConfigClassXml(className, path, xmlName);
			AbstractConfigClass.parseClassMap.put(className, retobj);
		}
		return (ConfigClassXml) retobj;
	}

	public static ConfigClassXml createConfigClassXml(String className,
			InputStream stream) throws ProjectException {
		AbstractConfigClass retobj = AbstractConfigClass.parseClassMap
				.get(className);
		if (retobj == null) {
			retobj = new ConfigClassXml(className, stream);
			AbstractConfigClass.parseClassMap.put(className, retobj);
		}
		return (ConfigClassXml) retobj;
	}

	private List<Map<ColProperty, String>> retXmlMap(ConfigurationNode node) {
		List<ConfigurationNode> nodeList = node.getChildren();
		List<Map<ColProperty, String>> retObj = null;
		if (CollectionUtils.isNotEmpty(nodeList)) {
			retObj = new ArrayList<Map<ColProperty, String>>();
			for (ConfigurationNode tempNode : nodeList) {
				retObj.add(packAttributesFromXml(tempNode));
			}
		}
		return retObj;
	}

	@Override
	public List<Map<ColProperty, String>> createColPropertyByRoot(
			String nodeName) {
		if (this.xml == null) {
			return null;
		}
		ConfigurationNode node = XmlUtil.getFirstNodeByNodeName(
				this.xml.getRoot(), nodeName);
		return retXmlMap(node);
	}

	@Override
	public List<Map<ColProperty, String>> createColPropertyByPath(
			String nodePath) {
		if (this.xml == null) {
			return null;
		}
		ConfigurationNode node = getFirstNodesByPath(nodePath);
		return retXmlMap(node);
	}

	public List<ConfigurationNode> getNodesByPath(String path) {
		List<ConfigurationNode> nodes = this.xml.fetchNodeList(path);
		return nodes;
	}

	public ConfigurationNode getFirstNodesByPath(String path) {
		List<ConfigurationNode> nodes = this.getNodesByPath(path);
		if (CollectionUtils.isNotEmpty(nodes)) {
			return nodes.iterator().next();
		}
		return null;
	}

	// //////////////////辅助方法/////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 得到属性值通过属性名
	 * 
	 * @param node
	 *            XML节点
	 * @param colProperty
	 *            属性名
	 * @return 属性值
	 * */
	private static final String findValueByAttrName(ConfigurationNode node,
			ColProperty colProperty) {
		List<ConfigurationNode> typeAry = node
				.getAttributes(colProperty.name());
		if (CollectionUtils.isEmpty(typeAry)) {
			return null;
		}
		return String.valueOf(((DefaultConfigurationNode) typeAry.get(0))
				.getValue());
	}

	/**
	 * 把Xml数据组装到attributes中
	 * */
	private static final Map<ColProperty, String> packAttributesFromXml(
			ConfigurationNode pnode) {
		Map<ColProperty, String> attributes = new HashMap<ColProperty, String>();
		for (ColProperty colProperty : ColProperty.values()) {
			if (!colProperty.equals(ColProperty.xpath)) {// ColProperty.xpath在后面处理
				String tempValue = findValueByAttrName(pnode, colProperty);
				if (StringUtils.isNotBlank(tempValue)) {
					attributes.put(colProperty, tempValue);
				}
			}
		}
		ColType colType = ColType.getByName(attributes.get(ColProperty.type));
		if (colType != null && colType.isNeedXPath()) {// 如果是动态Bean等则要记录xpath
			String xpath = getXPath(pnode);
			attributes.put(ColProperty.xpath, xpath);
		}
		return attributes;
	}

	private static String getNodeName(ConfigurationNode node) {
		if (XMLNameSpace.col.equals(node.getName())) {// COL[@name = 'money']
			return findValueByAttrName(node, ColProperty.name);
		} else {
			return node.getName();
		}
	}

	public static String getXPath(ConfigurationNode node) {
		if (node == null || node.getParentNode() == null) {
			return null;
		}
		StringBuffer retBuf = new StringBuffer();
		upXPath(node, retBuf);
		return retBuf.toString();
	}

	private static void upXPath(ConfigurationNode node, StringBuffer addPath) {
		if (node == null) {
			return;
		}
		if (XMLNameSpace.col.equals(node.getName())) {// COL[@name = 'money']
			addPath.insert(0, "/COL[@name = '" + getNodeName(node) + "']");
		} else {
			addPath.insert(0, getNodeName(node));
		}
		ConfigurationNode parentNode = node.getParentNode();
		if (parentNode != null
				&& !XMLNameSpace.InterFaceMapping.equals(parentNode.getName())) {
			upXPath(parentNode, addPath);
		}
	}

}
