package net.wicp.tams.commons.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.apiext.CollectionUtil;
import net.wicp.tams.commons.apiext.JSONUtil;
import net.wicp.tams.commons.apiext.ReflectAsset;
import net.wicp.tams.commons.callback.IConvertValue;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

public abstract class EasyUiAssist {

	// {"total":28,"rows":[
	// {"productid":"FI-SW-01","productname":"Koi","unitcost":10.00,"status":"P","listprice":36.50,"attr1":"Large","itemid":"EST-1"},
	// {"productid":"K9-DL-01","productname":"Dalmation","unitcost":12.00,"status":"P","listprice":18.50,"attr1":"Spotted Adult Female","itemid":"EST-10"},
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
	 * @return
	 */
	public static String getJsonForGrid(List<?> fromList, String[] titles,
			long recordNum) {
		StringBuffer buff = new StringBuffer("{\"total\":" + recordNum
				+ ",\"rows\":");
		buff.append(JSONUtil.getJsonForList(fromList, titles));
		buff.append("}");
		return buff.toString();
	}

	/***
	 * 可以自定义转换格式
	 * 
	 * @param fromList
	 * @param titles
	 * @param converts
	 * @param recordNum
	 * @return
	 */
	public static String getJsonForGrid(List<?> fromList, String[] titles,
			IConvertValue[] converts, long recordNum) {
		StringBuffer buff = new StringBuffer("{\"total\":" + recordNum
				+ ",\"rows\":");
		buff.append(JSONUtil.getJsonForList(fromList, converts, titles));
		buff.append("}");
		return buff.toString();
	}

	public static String getJsonForGrid(List<?> fromList, String[] titles,
			Map<String, IConvertValue> convertsMap, long recordNum) {
		StringBuffer buff = new StringBuffer("{\"total\":" + recordNum
				+ ",\"rows\":");
		buff.append(JSONUtil.getJsonForList(fromList, convertsMap, titles));
		buff.append("}");
		return buff.toString();
	}

	/****
	 * 把数据以json格式返回，不需要指定已有字段。
	 * 
	 * @param fromList
	 * @param aliasTitles
	 *            　别名列表
	 * @param convertsMap
	 *            别名转换字段
	 * @param recordNum
	 * @return
	 */
	public static String getJsonForGridAlias(List<?> fromList,
			String[] aliasTitles, Map<String, IConvertValue> convertsMap,
			long recordNum) {
		if (CollectionUtils.isEmpty(fromList)) {
			return getJsonForGrid(fromList, new String[] {}, convertsMap, 0L);
		}
		Object object = fromList.get(0);
		String[] titles = null;
		if (ReflectAsset.isInterface(object.getClass(), "java.util.Map")) {
			Map temp = (Map) object;
			titles = new String[temp.size()];
			int i = 0;
			for (Object keyObj : temp.keySet()) {
				titles[i++] = String.valueOf(keyObj);
			}
		} else {
			List<String> fields = ReflectAsset.findGetField(object.getClass());
			titles = fields.toArray(new String[fields.size()]);
		}
		if (aliasTitles != null && aliasTitles.length > 0) {
			titles = CollectionUtil.arrayMerge(String[].class,titles, aliasTitles);
		}
		return getJsonForGrid(fromList, titles, convertsMap, recordNum);
	}

	/****
	 * 返回空的集合值
	 * 
	 * @return
	 */
	public static String getJsonForGridEmpty() {
		return getJsonForGrid(null, new String[] {}, 0L);
	}

	/****
	 * 把数据以json格式返回，不需要指定已有字段。
	 * 
	 * @param fromList
	 * @param recordNum
	 * @return
	 */
	public static String getJsonForGridAlias(List<?> fromList, long recordNum) {
		return getJsonForGridAlias(fromList, null, null, recordNum);
	}

	/***
	 * 指定数据放到Grid里显示
	 * 
	 * @param inputObj
	 * @return
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

}
