package net.wicp.tams.commons.apiext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONObject;
import org.mvel2.templates.TemplateRuntime;

import net.wicp.tams.commons.callback.IConvertValue;

/***
 * JSON对象的扩展
 * 
 * @author andy.zhou
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class JSONUtil {
	// js里面需要处理的特殊字符集
	public static final String[][] specialChar = new String[][] { { "\\\\", "\\\\\\\\" }, { "\"null\"", "\"\"" } };

	/**
	 * 合并JSon
	 * 
	 * @param res1
	 *            要合并的json对象1
	 * @param res2
	 *            要合并的json对象2
	 * @return JSONObject 合并后的json对象
	 */
	public static JSONObject mergeJSON(JSONObject res1, JSONObject res2) {
		JSONObject addJSON = res1.keys().size() < res2.keys().size() ? res1 : res2;
		JSONObject mainJSON = res1.keys().size() < res2.keys().size() ? res2 : res1;
		for (Iterator iterator = addJSON.keys().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			mainJSON.append(key, addJSON.get(key));
		}
		return mainJSON;
	}

	/**
	 * 把JSONObject对象转为list，里面的每个元素为String[2]
	 * 
	 * @param jsonObject
	 *            要合并的json对象1
	 * @return List 转换后列表
	 */
	public static List<String[]> getValues(JSONObject jsonObject) {
		List<String[]> resultList = new ArrayList<String[]>();// 因为是有序的，不能用map
		String key;
		for (Iterator keys = jsonObject.keys().iterator(); keys.hasNext(); resultList
				.add(new String[] { key, String.valueOf(jsonObject.get(key)) }))
			key = (String) keys.next();
		return resultList;
	}

	/***
	 * 把Map转为json格式的的json数据，全部为String输出 <br>
	 * 结果为：{"itemCode":"returnCheck","itemName":"待退货检查"}
	 * 
	 * @param fromMap
	 *            需要转的map
	 * @param convert
	 *            转换规则，可以为null
	 * @param keys
	 *            需要转的key值，如果不填则为全部 要取的标题，支持别名。 <br>
	 *            如：new String[]{""itemCode,itemCode","itemName_zh,itemName""}
	 *            <br>
	 *            itemName_zh为是取值的列名,itemName要显示的列名
	 * @return json格式字符串
	 */

	public static String getJsonForMap(Map<String, Object> fromMap, IConvertValue[] convert, String... keys) {
		if (fromMap == null || fromMap.size() == 0) {
			return null;
		}
		keys = ArrayUtils.isNotEmpty(keys) ? keys : (String[]) fromMap.keySet().toArray();
		StringBuffer buff = new StringBuffer("{");
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			String[] keyAry = key.split(",");
			String valCol = StringUtil.trimSpace(keyAry[0]);
			String showCol = StringUtil.trimSpace(keyAry[keyAry.length - 1]);
			Object value = fromMap.get(valCol);
			String valueTrue = value == null ? null : String.valueOf(value);
			if (convert != null && convert.length > i && convert[i] != null) {
				IConvertValue convertTrue = convert[i];
				valueTrue = valueTrue == null ? "" : convertTrue.getStr(valueTrue);
			}
			if (i != 0) {
				buff.append(",");
			}
			buff.append("\"" + showCol + "\":\"" + StringUtil.hasNull(valueTrue) + "\"");

		}
		buff.append("}");
		return buff.toString();
	}

	public static String getJsonForMap(Map<String, Object> fromMap, String... keys) {
		return getJsonForMap(fromMap, null, keys);
	}

	/****
	 * 返回格式 [{"itemCode":"checkNoPass","itemName":"质检不通过"},<br>
	 * { "itemCode":"checkPass","itemName":"质检通过"}]
	 * 
	 * @param fromList
	 *            要取的源数据,支持Map和Object对象
	 * @param converts
	 *            要转换的规则，可以为空，与title要一一对应
	 * @param titles
	 *            要取的标题，支持别名，<br>
	 *            如：new String[]{""itemCode,itemCode","itemName_zh,itemName""}
	 *            <br>
	 *            itemName_zh为是取值的列名,itemName要显示的列名
	 * 
	 * @return json格式字符串
	 */
	public static String getJsonForList(List<?> fromList, IConvertValue[] converts, String... titles) {
		if (CollectionUtils.isEmpty(fromList) || ArrayUtils.isEmpty(titles)) {
			return "[]";
		}
		StringBuffer buff = new StringBuffer("[");
		for (Object object : fromList) {
			if (ReflectAssist.isInterface(object.getClass(), "java.util.Map")) {
				String singJoson = getJsonForMap((Map) object, converts, titles);
				buff.append(singJoson + ",");
			} else {
				StringBuffer jsonTempStr = new StringBuffer("@{'{");
				for (int i = 0; i < titles.length; i++) {
					String[] titleAry = titles[i].split(",");
					String valCol = StringUtil.trimSpace(titleAry[0]);
					String showCol = StringUtil.trimSpace(titleAry[titleAry.length - 1]);
					jsonTempStr.append("\"" + showCol + "\":\"'+" + valCol + "+'\"");
					if (i != titles.length - 1) {
						jsonTempStr.append(",");
					}
				}
				jsonTempStr.append("}'}");
				// 通过规则转换字符
				String tempStr = String.valueOf(TemplateRuntime.eval(jsonTempStr.toString(), object));
				if (ArrayUtils.isNotEmpty(converts)) {
					JSONObject jsObj = new JSONObject(tempStr);
					for (int i = 0; i < converts.length; i++) {
						IConvertValue convert = converts[i];
						String colName = i < titles.length ? titles[i] : null;
						if (convert != null && StringUtils.isNotBlank(colName)) {
							int index = colName.indexOf(",");
							String key = "";
							//String oriKey = "";
							if (index > 0) {
								key = colName.substring(index + 1);
								//oriKey = colName.substring(0, index);
							} else {
								key = colName;
								//oriKey = colName;
							}
							String value = convert.getStr(jsObj.getString(key));
							jsObj.put(key, value);
						}
					}
					tempStr = jsObj.toString();
				}
				for (int i = 0; i < specialChar.length; i++) {
					String[] tempAry = specialChar[i];
					tempStr = tempStr.replaceAll(tempAry[0], tempAry[1]);
				}
				buff.append(tempStr + ",");
			}
		}
		buff.delete(buff.length() - 1, buff.length());// 去除最后一个“,”
		buff.append("]");
		return buff.toString();
	}

	public static String getJsonForList(List<?> fromList, String... titles) {
		return getJsonForList(fromList, new IConvertValue[] {}, titles);
	}

	/****
	 * 支持Map, Map &lt; String, IConvertValue &gt; key为title 如果是标题有别名方式：aaa,bbb
	 * <br>
	 * 则以别名主识别IConvertValue
	 * 
	 * @param fromList
	 *            要取的源数据,支持Map和Object对象
	 * @param convertsMap
	 *            转换器Map
	 * @param titles
	 *            标题
	 * @return json格式字符串
	 */
	public static String getJsonForList(List<?> fromList, Map<String, IConvertValue> convertsMap, String... titles) {
		IConvertValue[] convert = null;
		if (convertsMap != null && CollectionUtils.isNotEmpty(convertsMap.keySet())) {
			convert = new IConvertValue[titles.length];
			for (String title : convertsMap.keySet()) {
				int index = -1;
				for (int i = 0; i < titles.length; i++) {
					String eleTitle = titles[i];
					if (StringUtils.isNotBlank(eleTitle)) {
						String[] tempTitleAry = eleTitle.split(",");
						String trueKey = tempTitleAry.length > 1 ? tempTitleAry[1] : tempTitleAry[0];
						if (title.equalsIgnoreCase(trueKey)) {
							index = i;
							break;
						}
					}
				}
				// int index = ArrayUtils.indexOf(titles, title);
				if (index >= 0) {
					convert[index] = convertsMap.get(title);
				}
			}
		}
		return getJsonForList(fromList, convert, titles);
	}

	/****
	 * 别名
	 * 
	 * @param fromList
	 *            要取的源数据,支持Map和Object对象
	 * @param aliasTitles
	 *            标题的别名
	 * @param convertsMap
	 *            要取的源数据,支持Map和Object对象
	 * @return json格式字符串
	 */
	public static String getJsonForListAlias(List<?> fromList, String[] aliasTitles,
			Map<String, IConvertValue> convertsMap) {
		if (CollectionUtils.isEmpty(fromList)) {
			return "[]";
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
		return getJsonForList(fromList, convertsMap, titles);
	}

	public static String getJsonForListAlias(List<?> fromList) {
		return getJsonForListAlias(fromList, null, null);
	}

}
