package net.wicp.tams.commons.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.json.JSONArray;

import net.wicp.tams.commons.apiext.CollectionUtil;
import net.wicp.tams.commons.apiext.JSONUtil;
import net.wicp.tams.commons.apiext.ReflectAssist;
import net.wicp.tams.commons.callback.IConvertValue;
import net.wicp.tams.commons.web.easyuibean.EasyUINode;
import net.wicp.tams.commons.web.easyuibean.EasyUINodeConf;

/***
 * easyui辅助类
 * 
 * @author andy.zhou
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class EasyUiAssist {

	// {"total":28,"rows":[
	// {"productid":"FI-SW-01","productname":"Koi","unitcost":10.00,"status":"P","listprice":36.50,"attr1":"Large","itemid":"EST-1"},
	// {"productid":"K9-DL-01","productname":"Dalmation","unitcost":12.00,"status":"P","listprice":18.50,"attr1":"Spotted
	// Adult Female","itemid":"EST-10"},
	// {"productid":"RP-SN-01","productname":"Rattlesnake","unitcost":12.00,"status":"P","listprice":38.50,"attr1":"Venomless","itemid":"EST-11"},
	// {"productid":"RP-SN-01","productname":"Rattlesnake","unitcost":12.00,"status":"P","listprice":26.50,"attr1":"Rattleless","itemid":"EST-12"},
	// ]}
	/***
	 * 返回格式： {"total":12,"rows":[{"itemCode":"checkNoPass","itemName":"质检不通过"},{
	 * "itemCode":"checkPass","itemName":"质检通过"}]}
	 * 
	 * @param fromList
	 *            要取的源数据
	 * @param titles
	 *            要取的标题，支持别名，如：new
	 *            String[]{""itemCode,itemCode","itemName_zh,itemName""}
	 *            itemName_zh为是取值的列名,itemName要显示的列名
	 * @param recordNum
	 *            记录总数
	 * @return Grid的String形式
	 */
	public static String getJsonForGrid(List<?> fromList, String[] titles, long recordNum) {
		StringBuffer buff = new StringBuffer("{\"total\":" + recordNum + ",\"rows\":");
		buff.append(JSONUtil.getJsonForList(fromList, titles));
		buff.append("}");
		return buff.toString();
	}

	/***
	 * 可以自定义转换格式
	 * 
	 * @param fromList
	 *            要取的源数据
	 * @param titles
	 *            要取的标题，支持别名，如：new
	 *            String[]{""itemCode,itemCode","itemName_zh,itemName""}
	 *            itemName_zh为是取值的列名,itemName要显示的列名
	 * @param converts
	 *            转换器数组
	 * @param recordNum
	 *            记录总数
	 * @return Grid的String形式
	 */

	public static String getJsonForGrid(List<?> fromList, String[] titles, IConvertValue[] converts, long recordNum) {
		StringBuffer buff = new StringBuffer("{\"total\":" + recordNum + ",\"rows\":");
		buff.append(JSONUtil.getJsonForList(fromList, converts, titles));
		buff.append("}");
		return buff.toString();
	}

	/***
	 * 
	 * @param fromList
	 * @param titles
	 * @param convertsMap
	 * @param recordNum
	 * @return Grid的String形式
	 */
	public static String getJsonForGrid(List<?> fromList, String[] titles, Map<String, IConvertValue> convertsMap,
			long recordNum) {
		StringBuffer buff = new StringBuffer("{\"total\":" + recordNum + ",\"rows\":");
		buff.append(JSONUtil.getJsonForList(fromList, convertsMap, titles));
		buff.append("}");
		return buff.toString();
	}

	/****
	 * 把数据以json格式返回，不需要指定已有字段。
	 * 
	 * @param fromList
	 * @param aliasTitles
	 *            别名列表
	 * @param convertsMap
	 *            别名转换字段
	 * @param recordNum
	 * @return Grid的String形式
	 */
	public static String getJsonForGridAlias(List<?> fromList, String[] aliasTitles,
			Map<String, IConvertValue> convertsMap, long recordNum) {
		if (CollectionUtils.isEmpty(fromList)) {
			return getJsonForGrid(fromList, new String[] {}, convertsMap, 0L);
		}
		Object object = fromList.get(0);
		String[] titles = null;
		if (ReflectAssist.isInterface(object.getClass(), "java.util.Map")) {
			Map temp = (Map) object;
			titles = new String[temp.size()];
			int i = 0;
			for (Object keyObj : temp.keySet()) {
				titles[i++] = String.valueOf(keyObj);
			}
		} else {
			List<String> fields = ReflectAssist.findGetField(object.getClass());
			titles = fields.toArray(new String[fields.size()]);
		}
		if (aliasTitles != null && aliasTitles.length > 0) {
			titles = CollectionUtil.arrayMerge(String[].class, titles, aliasTitles);
		}
		return getJsonForGrid(fromList, titles, convertsMap, recordNum);
	}

	/***
	 * 构造treegrid方法
	 * 
	 * @param fromList
	 * @param parentCol
	 *            存放父字段的列名
	 * @param aliasTitles
	 * @param convertsMap
	 * @param recordNum
	 * @return
	 */
	public static String getJsonForTreeGridAlias(List<?> fromList, String parentCol, String[] aliasTitles,
			Map<String, IConvertValue> convertsMap, long recordNum) {
		if (StringUtils.isNotBlank(parentCol)) {
			aliasTitles = ArrayUtils.add(aliasTitles, String.format("%s,_parentId", parentCol));
		}
		return getJsonForGridAlias(fromList, aliasTitles, convertsMap, recordNum);
	}

	public static String getJsonForTreeGridAlias(List<?> fromList, String parentCol, long recordNum) {
		return getJsonForTreeGridAlias(fromList, parentCol, null, null, recordNum);
	}

	/****
	 * 返回空的集合值
	 * 
	 * @return 空的集合值
	 */
	public static String getJsonForGridEmpty() {
		return getJsonForGrid(null, new String[] {}, 0L);
	}

	/****
	 * 把数据以json格式返回，不需要指定已有字段。
	 * 
	 * @param fromList
	 *            源业务对象集合
	 * @param recordNum
	 *            记录数量（所有页）
	 * @return Grid的String
	 */
	public static String getJsonForGridAlias(List<?> fromList, long recordNum) {
		return getJsonForGridAlias(fromList, null, null, recordNum);
	}

	/***
	 * 指定数据放到Grid里显示
	 * 
	 * @param inputObj
	 *            要放到Grid的业务对象数组
	 * @return Grid的String
	 */

	public static String getJsonForGridByObj(Object... inputObj) {
		List retList = new ArrayList();
		if (ArrayUtils.isEmpty(inputObj)) {
			return getJsonForGridAlias(retList, 0);
		}
		for (Object eleObj : inputObj) {
			retList.add(eleObj);
		}
		return getJsonForGridAlias(retList, retList.size());
	}

	/****
	 * 把根节点转为json Str字符串
	 * 
	 * @param nodes
	 *            根节点集合
	 * @return 树结果
	 */
	public static String getTreeFromList(EasyUINode... nodes) {
		String retstr = "[]";
		if (ArrayUtils.isEmpty(nodes)) {
			return retstr;
		}
		JSONArray arry = new JSONArray();
		for (EasyUINode easyUINode : nodes) {
			arry.put(easyUINode.toJson());
		}
		return arry.toString();
	}

	/****
	 * 把根节点转为json Str字符串
	 * 
	 * @param nodes
	 *            根节点集合
	 * @return 树结果
	 */
	public static String getTreeFromList(List<EasyUINode> nodes) {
		if (CollectionUtils.isEmpty(nodes)) {
			return "[]";
		}
		return getTreeFromList(nodes.toArray(new EasyUINode[nodes.size()]));
	}

	/****
	 * 把List转为根节点集合
	 * 
	 * @param oriList
	 *            源业务对象集合
	 * @param conf
	 *            树的配置信息
	 * @return 多棵树的对象集合
	 * @throws Exception
	 *             转换异常
	 */
	public static <T> List<EasyUINode> getTreeRoot(List<T> oriList, EasyUINodeConf conf) throws Exception {
		if (CollectionUtils.isEmpty(oriList) || conf == null) {
			return new ArrayList<EasyUINode>();
		}
		List<EasyUINode> roots = new ArrayList<EasyUINode>();
		List<EasyUINode> nodes = new ArrayList<EasyUINode>();
		for (Object oriObj : oriList) {
			String id = BeanUtils.getProperty(oriObj, conf.getIdCol());
			String text = "";
			if (conf.getTextConvert() != null) {
				text = conf.getTextConvert().getStr(oriObj);
			} else {
				text = BeanUtils.getProperty(oriObj, conf.getTextCol());
			}
			String parentId = null;
			try {
				parentId = BeanUtils.getProperty(oriObj, conf.getParentCol());
			} catch (Exception e) {
			}
			EasyUINode tempObj = new EasyUINode(id, text);
			tempObj.setParent(new EasyUINode(parentId));
			if (StringUtils.isNotBlank(conf.getIndexCol())) {
				Object indexObj = PropertyUtils.getProperty(oriObj, conf.getIndexCol());
				if (indexObj != null) {
					tempObj.setIndex(new Integer(String.valueOf(indexObj)));
				}
			}
			if (StringUtils.isNotBlank(conf.getIconClsCol())) {
				if (conf.getIconClsCol().startsWith(":")) {
					tempObj.setIconCls(conf.getIconClsCol().substring(1));
				} else {
					tempObj.setIconCls(BeanUtils.getProperty(oriObj, conf.getIconClsCol()));
				}
			}

			if (StringUtils.isNotBlank(conf.getIsCloseCol())) {
				tempObj.setClose(getBoolean(conf.getIsCloseCol(), oriObj, tempObj));
			}

			if (CollectionUtils.isNotEmpty(conf.getCheckedList())) {// 以getCheckedList为主
				if (conf.getCheckedList().contains(id))
					tempObj.setChecked(true);
				else
					tempObj.setChecked(false);
			} else if (StringUtils.isNotBlank(conf.getCheckedCol())) {// 不是Checklist机制则考虑从对象列存放机制
				tempObj.setChecked(getBoolean(conf.getCheckedCol(), oriObj, tempObj));
			}

			// 附加属性
			if (org.apache.commons.lang3.ArrayUtils.isNotEmpty(conf.getAttrCols())) {
				for (String attrCol : conf.getAttrCols()) {
					String attrValue = BeanUtils.getProperty(oriObj, attrCol);
					tempObj.addAttributes(attrCol, attrValue);
				}
			}

			if (StringUtils.isBlank(parentId)) {
				tempObj.setParent(null);
				roots.add(tempObj);
			} else if (conf.getIsRoot() != null && conf.getIsRoot().evaluate(parentId)) {
				tempObj.setParent(null);
				roots.add(tempObj);
			} else {
				nodes.add(tempObj);
			}
		}
		if (CollectionUtils.isNotEmpty(nodes)) {
			packNode(roots, nodes);
		}
		return roots;
	}

	private static boolean getBoolean(String valueCol, Object oriObj, EasyUINode tempObj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object isCloseObj = PropertyUtils.getProperty(oriObj, valueCol);
		String isCloseStr = String.valueOf(isCloseObj);
		if ("true".equals(isCloseStr) || "yes".equalsIgnoreCase(isCloseStr) || "y".equals(isCloseStr)
				|| "1".equals(isCloseStr)) {
			return true;
		} else {
			return false;
		}
	}

	private static void packNode(List<EasyUINode> parentNodes, List<EasyUINode> nodes) {
		if (CollectionUtils.isNotEmpty(parentNodes) && CollectionUtils.isNotEmpty(nodes)) {
			final List<String> rootIds = (List<String>) CollectionUtil.getColFromObj(parentNodes, "id");
			List<EasyUINode> selNodes = (List<EasyUINode>) CollectionUtils.select(nodes, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					EasyUINode tempObj = (EasyUINode) object;
					return rootIds.contains(tempObj.getParent().getId());
				}
			});
			if (CollectionUtils.isNotEmpty(selNodes)) {
				for (final EasyUINode parentNode : parentNodes) {
					List<EasyUINode> rootSel = (List<EasyUINode>) CollectionUtils.select(selNodes, new Predicate() {
						@Override
						public boolean evaluate(Object object) {
							EasyUINode tmpObj = (EasyUINode) object;
							return parentNode.getId().equals(tmpObj.getParent().getId());
						}
					});
					parentNode.addChildres(rootSel);
				}
				nodes.removeAll(selNodes);
				packNode(selNodes, nodes);
			} else {
				nodes.clear();
			}
		}
	}

	public static Map<String, IConvertValue> getConvertMap(Object... keysAndValues) {
		Map<String, IConvertValue> convertmap = new HashMap<String, IConvertValue>();
		if (ArrayUtils.isEmpty(keysAndValues)) {
			return convertmap;
		}
		int i = 0;
		while (i < keysAndValues.length) {
			convertmap.put(String.valueOf(keysAndValues[i++]), (IConvertValue) keysAndValues[i++]);
		}
		return convertmap;
	}

}
