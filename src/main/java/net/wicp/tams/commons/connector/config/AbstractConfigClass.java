package net.wicp.tams.commons.connector.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.apiext.CollectionUtil;
import net.wicp.tams.commons.callback.impl.optColType.OptAbsColType;
import net.wicp.tams.commons.callback.impl.optColType.ParserDynaClassProperty;
import net.wicp.tams.commons.connector.HelperConn;
import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.beans.CusDynaClass;
import net.wicp.tams.commons.connector.beans.property.AbstractDynaClassProperty;
import net.wicp.tams.commons.connector.beans.property.DynaBeanHandler;
import net.wicp.tams.commons.connector.config.xmlParser.XMLNameSpace;
import net.wicp.tams.commons.constant.ColGType;
import net.wicp.tams.commons.constant.ColProperty;
import net.wicp.tams.commons.constant.ColType;
import net.wicp.tams.commons.constant.param.conn.Request;
import net.wicp.tams.commons.constant.param.conn.Response;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

public abstract class AbstractConfigClass {
	private final static Logger logger = LogHelp.getLogger(AbstractConfigClass.class);
	private String className;
	private CusDynaClass outDynaClass = null;
	private CusDynaClass inDynaClass = null;

	/***
	 * 动态Bean的缓存
	 */
	protected static Map<String, AbstractConfigClass> parseClassMap = new HashMap<String, AbstractConfigClass>();

	/***
	 * 清除缓存
	 */
	public static void clearCache() {
		parseClassMap.clear();
	}

	public static void delCache(String className) {
		parseClassMap.remove(className);
	}

	public AbstractConfigClass(String className) {
		this.className = className;
	}

	public abstract List<Map<ColProperty, String>> createColPropertyByRoot(String nodeName);

	public abstract List<Map<ColProperty, String>> createColPropertyByPath(String nodePath);

	private AbstractDynaClassProperty ParserParaProperty(Map<ColProperty, String> inputMap) throws ProjectException {
		String typeStr = inputMap.get(ColProperty.type);
		ColType type = ColType.getByName(typeStr);
		OptAbsColType parser = new ParserDynaClassProperty();
		Result ret = null;
		if (type == ColType.dynaBean) {
			CusDynaClass retClass = createDynaClassByPath(inputMap.get(ColProperty.name),
					inputMap.get(ColProperty.xpath));
			ret = parser.opt(type, inputMap, retClass);
		} else {
			ret = parser.opt(type, inputMap);
		}
		if (ret == null) {
			String info = String.format("不支持的类型,字段名：[%s],类型：[%s]", inputMap.get(ColProperty.name), typeStr);
			logger.error(info);
			throw new RuntimeException(info);
		}
		AbstractDynaClassProperty returnPropery = ret.isSuc() ? (AbstractDynaClassProperty) ret.getRetObj(0) : null;
		return returnPropery;

	}

	/**
	 * 得到inDynaClass，会加入ControlInfo元素
	 * 
	 */
	private synchronized final CusDynaClass parserInput() throws ProjectException {
		if (this.inDynaClass == null) {
			CusDynaClass useDynaClass = createDynaClassCommon(XMLNameSpace.inputRoot);// XMLNameSpace.inputRoot
			AbstractDynaClassProperty[] propertyAry = useDynaClass.getDynaProperties();
			AbstractDynaClassProperty[] newPropertyAry = new AbstractDynaClassProperty[propertyAry.length + 1];
			System.arraycopy(propertyAry, 0, newPropertyAry, 0, propertyAry.length);
			AbstractDynaClassProperty ControlInfo = new DynaBeanHandler(XMLNameSpace.ControlInfo,
					HelperConn.getInputClass());
			ControlInfo.putAttribute(ColProperty.gtype, ColGType.single.name());
			ControlInfo.putAttribute(ColProperty.strict, "false");
			newPropertyAry[propertyAry.length] = ControlInfo;
			this.inDynaClass = CusDynaClass.createCusDynaClass(getClassNameByNodeName(XMLNameSpace.inputRoot),
					newPropertyAry);
		}
		return this.inDynaClass;
	}

	/****
	 * 得到纯净版的inDynaClass，没有任何附加的协议信息ControlInfo
	 * 
	 * @return CusDynaClass
	 * @throws ProjectException
	 *             执行错误
	 */
	public final CusDynaClass parserInputNoCI() throws ProjectException {
		return createDynaClassCommon(XMLNameSpace.inputRoot);// XMLNameSpace.inputRoot
	}

	/***
	 * 得到inputBean并设置ControlInfo信息
	 * 
	 * @param clientBean
	 *            控制信息
	 * @return 动态Bean
	 */
	public final CusDynaBean newInputBean(CusDynaBean clientBean) {
		try {
			CusDynaBean retDynaBean = parserInput().newInstance();
			if (clientBean == null) {
				return retDynaBean;
			}
			retDynaBean.set(Request.controlInfo, clientBean);
			return retDynaBean;
		} catch (Exception e) {
			logger.error(String.format("动态类%s新建InputBean实例出错", className), e);
			return null;
		}
	}

	/***
	 * 得到空的inputBean，没有ControlInfo信息
	 * 
	 * @return 动态Bean
	 */
	public final CusDynaBean newInputBean() {
		CusDynaBean clientBean = null;
		return newInputBean(clientBean);
	}

	/***
	 * 通过Properties数据设置ControlInfo信息并返回inputobj
	 * 
	 * @param inputobj
	 *            属性值
	 * @return 动态Bean
	 */
	public final CusDynaBean newInputBean(Properties inputobj) {
		CusDynaBean clientBean = HelperConn.newControlInfo(inputobj);
		return newInputBean(clientBean);
	}

	/***
	 * 通过Map数据设置ControlInfo信息并返回inputobj
	 * 
	 * @param inputobj
	 *            map值
	 * @return 动态Bean
	 */
	public final CusDynaBean newInputBean(Map<String, String> inputobj) {
		CusDynaBean clientBean = HelperConn.newControlInfo(inputobj);
		return newInputBean(clientBean);
	}

	/***
	 * 得到outDataBean 南要加入：result msg 元素
	 * 
	 * @return 动态class
	 * @throws ProjectException
	 *             解析异常
	 */
	private synchronized final CusDynaClass parserOut() throws ProjectException {
		if (this.outDynaClass == null) {
			CusDynaClass useDynaClass = (CusDynaClass) createDynaClassCommon(XMLNameSpace.outputRoot);
			CusDynaClass addclass = HelperConn.getOutClass();
			AbstractDynaClassProperty respinfoProp = (AbstractDynaClassProperty) addclass
					.getDynaProperty(Response.respInfo.toString());
			AbstractDynaClassProperty resultProp = (AbstractDynaClassProperty) addclass
					.getDynaProperty(Response.result);
			AbstractDynaClassProperty errorCodeProp = (AbstractDynaClassProperty) addclass
					.getDynaProperty(Response.errorCode);
			AbstractDynaClassProperty[] oriProps = useDynaClass.getDynaProperties();
			AbstractDynaClassProperty[] newPropertyAry = new AbstractDynaClassProperty[oriProps.length + 3];
			System.arraycopy(oriProps, 0, newPropertyAry, 0, oriProps.length);
			newPropertyAry[oriProps.length] = respinfoProp;
			newPropertyAry[oriProps.length + 1] = resultProp;
			newPropertyAry[oriProps.length + 2] = errorCodeProp;
			this.outDynaClass = CusDynaClass.createCusDynaClass(getClassNameByNodeName(XMLNameSpace.outputRoot),
					newPropertyAry);
		}
		return this.outDynaClass;
	}

	/***
	 * 得到Bean
	 * 
	 * @param exceptAll
	 *            异常
	 * @param msgId
	 *            通信Id
	 * @return
	 */
	public final CusDynaBean newOutBean(ExceptAll exceptAll, String msgId) {
		try {
			CusDynaBean retBean = parserOut().newInstance();
			CusDynaBean respInfoBean = HelperConn.newRespInfo(msgId);
			retBean.set(Response.respInfo.toString(), respInfoBean);
			if (exceptAll == null || exceptAll == ExceptAll.no) {
				retBean.set(Response.result, HelperConn.OK);
				retBean.set(Response.errorCode, ExceptAll.no.name());
			} else {
				retBean.set(Response.result, HelperConn.ERROR);
				retBean.set(Response.errorCode, exceptAll.name());
			}
			return retBean;
		} catch (ProjectException e) {
			logger.error(String.format("动态类%s新建OutBean实例出错", className), e);
			return null;
		}
	}

	public final CusDynaBean newOutBean(ExceptAll exceptAll) {
		return newOutBean(exceptAll, null);
	}

	/***
	 * 得到成功的输入Bean，并设置好msgId，如果是异步通信可以通过msgId来确认请求
	 * 
	 * @param msgId
	 *            请求过来的请求号，一般来说是时间戳
	 * @return 动态Bean
	 */
	public final CusDynaBean newOutSuc(String msgId) {
		return newOutBean(ExceptAll.no, msgId);
	}

	/***
	 * 得到成功的输入Bean
	 * 
	 * @return
	 */
	public final CusDynaBean newOutSuc() {
		return newOutSuc(null);
	}

	public synchronized final CusDynaClass parserOutNoCI() throws ProjectException {
		return (CusDynaClass) createDynaClassCommon(XMLNameSpace.outputRoot);
	}

	public final CusDynaBean newOutputBean() throws ProjectException {
		try {
			CusDynaClass tempClass = parserOut();
			return tempClass.newInstance();
		} catch (Exception e) {
			throw new ProjectException(ExceptAll.Project_default, "新建动态Bean实例出错");
		}
	}

	/***
	 * 设置ColProperty的默认值，当没有定义ColProperty的相当项时，<br>
	 * 系统会把ColProperty中相关项配置中有默认值项跟据默认值设置一次并添加到attributesOri中
	 * 
	 * @param attributesOri
	 */
	public static void setDefaultColProperty(Map<ColProperty, String> attributesOri) {
		// 检查是否有name值或alias，两者必须要有一个， 如果没有直接抛异常,名字与别名如果有空值就设置另外的值
		String namevalue = attributesOri.get(ColProperty.name);
		String aliasvalue = attributesOri.get(ColProperty.alias);
		if (StringUtils.isBlank(namevalue) && StringUtils.isBlank(aliasvalue)) {
			throw new IllegalArgumentException("name与alias两个属性必须设置其中一个" + attributesOri);
		}
		if (StringUtils.isBlank(namevalue))
			attributesOri.put(ColProperty.name, aliasvalue);
		if (StringUtils.isBlank(aliasvalue))
			attributesOri.put(ColProperty.alias, namevalue);
		// 设置valueName的值，如果valueName值没有设置则为Name，valueName用于在dynaBean转为javaBean时有用。
		String valueName = attributesOri.get(ColProperty.valueName);
		if (StringUtils.isBlank(valueName)) {
			attributesOri.put(ColProperty.valueName, namevalue);
		}

		for (ColProperty colProperty : ColProperty.values()) {
			if (!colProperty.equals(ColProperty.xpath)) {// ColProperty.xpath在后面处理
				String tempValue = attributesOri.get(colProperty);
				if (StringUtils.isBlank(tempValue)) {
					if (StringUtils.isNotBlank(colProperty.getDefaultSelStringValue())) {// 默认值不为空
						attributesOri.put(colProperty, colProperty.getDefaultSelStringValue());
					}
				}
			}
		}
	}

	private CusDynaClass createUtil(String nodeName, List<Map<ColProperty, String>> colProperty)
			throws ProjectException {
		AbstractDynaClassProperty[] propertyAry = null;
		if (CollectionUtils.isNotEmpty(colProperty)) {
			propertyAry = new AbstractDynaClassProperty[colProperty.size()];
			for (int i = 0; i < colProperty.size(); i++) {
				Map<ColProperty, String> tempMap = colProperty.get(i);
				propertyAry[i] = ParserParaProperty(tempMap);
			}
		}
		return CusDynaClass.createCusDynaClass(nodeName, propertyAry);
	}

	@SuppressWarnings("unchecked")
	private CusDynaClass createDynaClassCommon(String nodeName) throws ProjectException {
		final List<Map<ColProperty, String>> colProperty = createColPropertyByRoot(nodeName);
		// 检查配置文件的相关字段的属性是否齐全
		if (CollectionUtils.isEmpty(colProperty)) {
			logger.error("没有找到该节点[{}]", nodeName);
			throw new ProjectException(ExceptAll.param_error);
		}
		for (final Map<ColProperty, String> map : colProperty) {
			String type = map.get(ColProperty.type);
			ColType temptype = ColType.getByName(type);
			if (ArrayUtils.isNotEmpty(temptype.getNeedCols())) {
				List<ColProperty> retlist = (List<ColProperty>) CollectionUtils
						.select(Arrays.asList(temptype.getNeedCols()), new Predicate() {
							@Override
							public boolean evaluate(Object object) {
								ColProperty temobj = (ColProperty) object;
								return !map.containsKey(temobj) || StringUtils.isBlank(map.get(temobj));
							}
						});
				if (CollectionUtils.isNotEmpty(retlist)) {
					logger.error("配置文件错误，字段[{}->{}],需要出现下列配置项[{}]却没有出现", nodeName, map.get(ColProperty.name),
							CollectionUtil.listJoin(retlist, ","));
					throw new ProjectException(ExceptAll.param_error);
				}
			}
		}
		return createUtil(nodeName, colProperty);
	}

	/****
	 * 通过路径来创建动态Class
	 * 
	 * @param nodeName
	 * @param nodePath
	 * @return
	 * @throws ProjectException
	 */
	private CusDynaClass createDynaClassByPath(String nodeName, String nodePath) throws ProjectException {
		List<Map<ColProperty, String>> colProperty = createColPropertyByPath(nodePath);
		return createUtil(nodeName, colProperty);
	}

	private String getClassNameByNodeName(String nodeName) {
		return className + "_" + nodeName;
	}

	@SuppressWarnings("unused")
	private Map<ColProperty, String> packColMap(String name, String alias, boolean isnull, ColGType gtype, ColType type,
			String defaultValue, Integer length, String max, String min, String format, String className) {
		Map<ColProperty, String> sysColPro_result = new HashMap<ColProperty, String>();
		if (StringUtils.isNotBlank(name)) {
			sysColPro_result.put(ColProperty.name, name);
		}
		if (StringUtils.isNotBlank(alias)) {
			sysColPro_result.put(ColProperty.alias, StringUtils.isBlank(alias) ? name : alias);
		}
		sysColPro_result.put(ColProperty.isnull, String.valueOf(isnull));
		if (gtype != null) {
			sysColPro_result.put(ColProperty.gtype, gtype.name());
		} else {
			sysColPro_result.put(ColProperty.gtype, ColGType.single.name());
		}
		if (type != null) {
			sysColPro_result.put(ColProperty.type, type.name());
		}
		if (StringUtils.isNotBlank(defaultValue)) {
			sysColPro_result.put(ColProperty.defaultValue, defaultValue);// 调用成功
		}
		if (length != null) {
			sysColPro_result.put(ColProperty.length, String.valueOf(length));
		}
		if (max != null) {
			sysColPro_result.put(ColProperty.max, max);
		}
		if (min != null) {
			sysColPro_result.put(ColProperty.min, min);
		}
		if (StringUtils.isNotBlank(format)) {
			sysColPro_result.put(ColProperty.format, format);
		}
		if (StringUtils.isNotBlank(className)) {
			sysColPro_result.put(ColProperty.className, className);
		}
		return sysColPro_result;
	}

}
