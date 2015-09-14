package net.wicp.tams.commons.apiext;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.exception.ProjectException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.mvel2.MVEL;

@SuppressWarnings("rawtypes")
public abstract class CollectionUtil {
	/**
	 * 把List通过分隔符进行分隔
	 * 
	 * @param fromList
	 *            要连接的List
	 * @param joinStr
	 *            连接的字符串
	 * @return String
	 */

	public static String listJoin(List<?> fromList, String joinStr) {
		if (CollectionUtils.isEmpty(fromList))
			return null;
		joinStr = StringUtils.isBlank(joinStr) ? "," : joinStr;
		StringBuffer toList = new StringBuffer();
		Iterator iterator = fromList.iterator();
		toList.append(String.valueOf(iterator.next()));
		while (iterator.hasNext()) {
			String string = String.valueOf(iterator.next());
			toList.append(joinStr);
			toList.append(string);
		}
		return toList.toString();
	}

	/**
	 * 把Array通过分隔符进行分隔
	 * 
	 * @param fromList
	 *            要连接的数组
	 * @param joinStr
	 *            连接的字符串
	 */
	public static String arrayJoin(Object[] fromList, String joinStr)
			throws ProjectException {
		if (ArrayUtils.isEmpty(fromList))
			return null;
		List<?> objlist = Arrays.asList(fromList);
		return listJoin(objlist, joinStr);
	}

	/****
	 * 数组的合并
	 * 
	 * @param clazz
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T> T[] arrayMerge(Class<T[]> clazz, T[] a, T[] b) {
		if (ArrayUtils.isEmpty(a)) {
			return b;
		}
		if (ArrayUtils.isEmpty(b)) {
			return a;
		}
		T[] newArray = newArrayByArrayClass(clazz, a.length + b.length);
		System.arraycopy(a, 0, newArray, 0, a.length);
		System.arraycopy(b, 0, newArray, a.length, b.length);
		return newArray;
	}

	/***
	 * 通过类型创建数组
	 * 
	 * @param clazz
	 * @param length
	 * @return
	 */
	public static <T> T[] newArrayByArrayClass(Class<T[]> clazz, int length) {
		return (T[]) Array.newInstance(clazz.getComponentType(), length);
	}

	/**
	 * 把list分成sumPerRow一组
	 * 
	 * @param inputList
	 *            要分隔的List
	 * @param sumPerRow
	 *            第个List的个数
	 * */
	public static List<List<?>> splitList(List<?> inputList, int sumPerRow) {
		if (CollectionUtils.isEmpty(inputList) || sumPerRow == 0) {
			return null;
		}
		List<List<?>> returnList = new ArrayList<List<?>>();
		List<Object> addList = new ArrayList<Object>();
		for (int i = 0; i < inputList.size(); i++) {
			if (i >= sumPerRow && i % sumPerRow == 0) {
				returnList.add(addList);
				addList = new ArrayList<Object>();
			}
			addList.add(inputList.get(i));
		}
		returnList.add(addList);
		return returnList;
	}

	/**
	 * 通过List得到对象的单个列值
	 * 
	 * @param fromList
	 *            要操作的数据源
	 * @param colName
	 *            要提取的列名
	 * @return List 提取预定列的List
	 * */
	public static List<?> getColFromObj(List<?> fromList, String colName) {
		List<Object> retList = new ArrayList<Object>();
		if (CollectionUtils.isEmpty(fromList)) {
			return retList;
		}
		for (Object object : fromList) {
			Object result = null;
			if (ReflectAsset.isInterface(object.getClass(), "java.util.Map")) {
				Map tempObjMap = (Map) object;
				result = tempObjMap.get(colName);
			} else {
				result = MVEL.eval(colName, object);
			}
			retList.add(result);
		}
		return retList;
	}

	/**
	 * @param inputCollection
	 *            要操作的字符
	 * @param predicate
	 *            规则
	 * @return 返回符合条件的集合并把这个集合从inputCollection删除（即inputCollection只剩余不合条件的数据）
	 * */
	public static Collection selectFilter(Collection inputCollection,
			Predicate predicate) {
		Collection retCollection = (Collection) CollectionUtils.find(
				inputCollection, predicate);
		CollectionUtils.filter(inputCollection, predicate);
		return retCollection;
	}

	/***
	 * 过滤空值
	 * 
	 * @param orimap
	 */
	public static void filterNull(Map<String, String> orimap) {
		if (orimap == null) {
			return;
		}
		for (String key : orimap.keySet()) {
			if (StringUtils.isBlank(orimap.get(key))) {
				orimap.remove(key);
			}
		}
	}

	/*****
	 * 过滤原始的　List
	 * 
	 * @param oriList
	 *            　原对象列表
	 * @param colName
	 *            对象列名
	 * @param include
	 *            允许的值
	 * @param exclude
	 *            排除的值
	 * @return
	 */
	public static List filter(List oriList, final String colName,
			String include, String exclude) {
		List retAry = new ArrayList();
		if (CollectionUtils.isEmpty(oriList)) {
			return retAry;
		}
		if (StringUtils.isNotBlank(include)) {
			String[] iAry = include.split(",");
			for (int i = 0; i < iAry.length; i++) {
				String includeValue = iAry[i];
				for (Object eleObj : oriList) {
					try {
						if (ReflectAsset.isInterface(eleObj.getClass(),
								"java.util.Map")) {
							Map tempObj = (Map) eleObj;
							if (includeValue.equalsIgnoreCase(String
									.valueOf(tempObj.get(colName)))) {
								retAry.add(eleObj);
								break;// 退出本层循环
							}
						} else {
							Object tempObj = PropertyUtils.getProperty(eleObj,
									colName);
							if (includeValue.equalsIgnoreCase(String
									.valueOf(tempObj))) {
								retAry.add(eleObj);
								break;// 退出本层循环
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		}

		if (StringUtils.isNotBlank(exclude)) {
			final String[] eAry = exclude.split(",");
			retAry = CollectionUtils.isEmpty(retAry) ? oriList : retAry;
			CollectionUtils.filter(retAry, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					try {
						if (ReflectAsset.isInterface(object.getClass(),
								"java.util.Map")) {
							Map tempObj = (Map) object;
							if (ArrayUtils.contains(eAry,
									String.valueOf(tempObj.get(colName)))) {
								return false;
							}
						} else {
							Object tempObj = PropertyUtils.getProperty(object,
									colName);
							if (ArrayUtils.contains(eAry, tempObj)) {
								return false;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					return true;
				}
			});
		}

		retAry = CollectionUtils.isEmpty(retAry) ? oriList : retAry;
		return retAry;

	}

}
