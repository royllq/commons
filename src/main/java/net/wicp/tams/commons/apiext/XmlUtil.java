package net.wicp.tams.commons.apiext;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;
import org.apache.commons.jxpath.JXPathContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class XmlUtil {

	private static SAXParserFactory saxFactory;

	static {
		saxFactory = SAXParserFactory.newInstance();
		saxFactory.setNamespaceAware(true);
		saxFactory.setValidating(false);
	}

	public static synchronized XMLReader getXMLReader() {
		try {
			SAXParser parser = saxFactory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
			return parser.getXMLReader();
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException se) {
			return null;
		}
	}

	/**
	 * 解析XML文档
	 * 
	 * @param src
	 *            要解析的字符串
	 * @return 解析后文档
	 * @throws ProjectException
	 *             解析错误
	 */
	public static final Document parserDocment(String src) throws ProjectException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			return db.parse(new InputSource(new StringReader(src)));
		} catch (Exception e) {
			throw new ProjectException(ExceptAll.Project_default, "解析XML文档错误");
		}
	}

	/**
	 * 通过标签名得到第一个元素
	 * 
	 * @param doc
	 *            文档
	 * @param tagName
	 *            标签名
	 * @return 元素
	 */
	public static Element getElementByTagName(Document doc, String tagName) {
		NodeList nodes = doc.getElementsByTagName(tagName);
		if (nodes == null || nodes.getLength() == 0)
			return null;
		else
			return (Element) nodes.item(0);
	}

	/****
	 * 通过属性名得到属性的值
	 * 
	 * @param node
	 *            含有属性的节点
	 * @param attrName
	 *            属性名
	 * @return 属性值
	 */
	@SuppressWarnings("rawtypes")
	public static final String findValueByAttrName(ConfigurationNode node, String attrName) {
		List typeAry = node.getAttributes(attrName);
		if (CollectionUtils.isEmpty(typeAry)) {
			return null;
		}
		return String.valueOf(((DefaultConfigurationNode) typeAry.get(0)).getValue());
	}

	/**
	 * 得到元素的值
	 * 
	 * @param element
	 *            文档元素
	 * @return String 元素的值
	 */
	public static String getElementValue(Element element) {
		if (element.hasChildNodes())
			return element.getFirstChild().getNodeValue();
		else
			return null;
	}

	/*****
	 * 通过名称得到指定节点下第一个元素
	 * 
	 * @param node
	 *            指定节点
	 * @param name
	 *            子节点名称
	 * @return 节点
	 */
	public static final ConfigurationNode getFirstNodeByNodeName(ConfigurationNode node, String name) {
		List<ConfigurationNode> nodes = node.getChildren(name);
		if (CollectionUtils.isEmpty(nodes)) {
			return null;
		}
		return nodes.get(0);
	}

	/**
	 * 通过xpath方式得到对象中的值
	 * 
	 * @param beanObj
	 *            要取值的对象
	 * @param xpath
	 *            取值 xpath 路径
	 * @return 返回的值
	 */
	public static Object getValueByXpath(Object beanObj, String xpath) {
		JXPathContext context = JXPathContext.newContext(beanObj);
		return context.getValue(xpath);
	}
}
