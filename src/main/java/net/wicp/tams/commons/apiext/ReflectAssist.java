package net.wicp.tams.commons.apiext;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.callback.IConvertValue;
import net.wicp.tams.commons.constant.DateFormatCase;
import net.wicp.tams.commons.constant.dic.YesOrNo;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class ReflectAssist {
	public static Logger logger = LoggerFactory.getLogger(ReflectAssist.class);

	private static String[] excludeGet = new String[] { "getClass" };

	public static Object invokeStaticMothed(String className, String methodName, Class[] paramclass, Object... param)
			throws Exception {
		Class c = Class.forName(className);
		Method m = c.getMethod(methodName, paramclass);
		Object retobj = m.invoke(c, param);
		return retobj;
	}

	/****
	 * 用简单参数调用静态方法
	 * 
	 * @param className
	 *            要调用的静态方法所在的类名
	 * @param methodName
	 *            静态方法名
	 * @param param
	 *            调用的参数
	 * @return
	 * @throws Exception
	 *             调用时错误
	 */
	public static Object invokeStaticMothed(String className, String methodName, Object... param) throws Exception {
		Class[] paramClass = null;
		if (!org.apache.commons.lang3.ArrayUtils.isEmpty(param)) {
			paramClass = new Class[param.length];
			for (int i = 0; i < param.length; i++) {
				paramClass[i] = param.getClass();
			}
		}
		return invokeStaticMothed(className, methodName, paramClass, param);
	}

	/***
	 * 调用对象中的方法
	 * 
	 * @param invokeObj
	 *            方法所在的对象
	 * @param methodName
	 *            方法名
	 * @param param
	 *            调用的参数
	 * @return 调用方法返回的结果
	 */
	public static Object invokeMothed(Object invokeObj, String methodName, Object... param) {
		Class c = invokeObj.getClass();
		if (StringUtil.isNull(methodName)) {
			logger.error("反射中缺少方法");
			return null;
		}
		Method[] m = c.getMethods();// .getMethod(methodName,ptypes);
		Method exeMethod = null;
		for (int i = 0; i < m.length; i++) {
			Method tempMethod = m[i];
			if (!methodName.equals(tempMethod.getName())) {// 方法名不相等
				continue;
			}
			Class[] classAry = tempMethod.getParameterTypes();
			if (classAry.length != param.length) {// 方法的参数不匹配
				continue;
			}
			if ((param == null && classAry == null) || (classAry.length == 0 && param.length == 0)) {// 无参数调用
				exeMethod = tempMethod;
				break;
			}

			boolean isthisMethod = true;
			for (int j = 1; j < classAry.length; j++) {
				Class classAryEle = classAry[j];
				Object paramEle = param[j];
				if (classAryEle.isArray() && paramEle.getClass().isArray()) {// TODO
																				// 参数是数组的如何查询它的元类型？？？？？
					try {
						Object[] paramArry = (Object[]) param;
						Object[] classInstArry = (Object[]) classAryEle.newInstance();
						if (paramArry[0].getClass().isAssignableFrom(classInstArry[0].getClass())) {
							isthisMethod = false;
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (!paramEle.getClass().isArray() && !classAryEle.isArray()) {
					if (!paramEle.getClass().isAssignableFrom(classAryEle)) {
						isthisMethod = false;
						break;
					}
				} else {
					isthisMethod = false;
					break;
				}
			}
			if (isthisMethod) {
				exeMethod = tempMethod;
				break;
			}

		}
		if (exeMethod != null) {
			try {
				return exeMethod.invoke(invokeObj, param);
			} catch (Exception e) {
				logger.error("反射调用方法出错。");
			}
		}
		return null;
	}

	/***
	 * 判断类是否基本数据类型
	 * 
	 * @param clz
	 *            要判断的类
	 * @return 是否基本数据类型 true:是，false:否
	 */
	public static boolean isPrimitieClass(Class clz) {
		try {
			return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
		} catch (Exception e) {
			return false;
		}
	}

	/***
	 * 找到get方法且没有参数的方法
	 * 
	 * @param clz
	 *            get方法所在的类
	 * @return 所有的get方法的方法名，排除"getClass"方法
	 */
	public static List<String> findGetMethod(Class clz) {
		List<String> methList = new ArrayList<String>();
		Method[] m = clz.getMethods();
		if (m.length == 0) {
			return methList;
		}
		for (int i = 0; i < m.length; i++) {
			Method method = m[i];
			String methodName = method.getName();
			if (!methodName.startsWith("get")) {
				continue;
			}
			if (ArrayUtils.contains(excludeGet, methodName)) {
				continue;
			}
			Class[] classAry = method.getParameterTypes();
			if (classAry.length == 0) {
				methList.add(method.getName());
			}
		}
		return methList;
	}

	/***
	 * 找到get方法对应的域
	 * 
	 * @param clz
	 *            域所在的类
	 * @return 所有的域的名称
	 */
	public static List<String> findGetField(Class clz) {
		List<String> retList = new ArrayList<String>();
		List<String> methodList = findGetMethod(clz);
		for (String methodname : methodList) {
			String ele = methodname.substring(3);
			retList.add(ele.substring(0, 1).toLowerCase() + ele.substring(1));
		}
		return retList;
	}

	/***
	 * 把Bean对象转为Map
	 * 
	 * @param obj
	 *            要转换的Bean对象
	 * @param convermap
	 *            对象值的转换器，key：对象的域名称,value：转换器，它会把对应域的值按转换器规则转换值，把转换后的值放入结果Map
	 * @param allowNull
	 *            是否允许为空，true:允许，false：不允许，如果允许为空，则对象域的值为null也会加到结果map中
	 * @return 转换后的结果
	 */
	public static Map<String, String> convertMapFromBeanForConvert(Object obj, Map<String, IConvertValue> convermap,
			boolean allowNull) {
		Map<String, String> retmap = new HashMap<String, String>();
		if (obj == null) {
			return retmap;
		}
		List<String> fields = findGetField(obj.getClass());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (String field : fields) {
				try {
					String value = null;
					if (convermap == null || !convermap.containsKey(field)) {
						value = BeanUtils.getProperty(obj, field);
					} else {
						IConvertValue convert = convermap.get(field);
						if (convert != null) {
							Object oriDate = PropertyUtils.getProperty(obj, field);
							value = convert.getStr(oriDate);
						} else {
							value = BeanUtils.getProperty(obj, field);
						}
					}
					if (!allowNull && StringUtil.isNull(value)) {// 不允许为空但又是空值
						continue;
					}
					if (StringUtil.isNotNull(value) && value.startsWith("org.apache.openjpa.enhance")) {// 由jpa生成的对象不放入
						continue;
					}
					retmap.put(field, value);
				} catch (Exception e) {
				}
			}
		}
		return retmap;
	}

	/***
	 * 把对象转为Map值, 主要用于把对象放到redis中(未测试)
	 * 
	 * @param obj
	 *            要转换的对象
	 * @return 转换后的Map值
	 */
	public static <T extends Serializable> Map<String, String> convertMapFromBean(T obj) {
		Map<String, String> retmap = new HashMap<String, String>();
		if (obj == null) {
			return retmap;
		}
		List<String> fields = findGetField(obj.getClass());
		if (CollectionUtils.isNotEmpty(fields)) {
			for (String field : fields) {
				packMap(retmap, null, obj, field);
			}
		}
		return retmap;
	}

	/***
	 * 把对象的某个域设置到map中(未测试)，使用递归的方法找出子对象的值放入map
	 * 
	 * @param map
	 *            要放结果的map
	 * @param oldfield
	 *            父对象的域名称
	 * @param obj
	 *            要操作的对象
	 * @param field
	 *            要操作的域名称
	 */
	private static void packMap(Map<String, String> map, String oldfield, Object obj, String field) {
		String key = StringUtil.isNull(oldfield) ? field : String.format("%s.%s", oldfield, field);
		Object fieldObj = null;
		try {
			fieldObj = PropertyUtils.getProperty(obj, field);
		} catch (Exception e) {
		}
		if (fieldObj == null) {
			return;
		}
		if (isPrimitieClass(fieldObj.getClass()) || fieldObj instanceof String || fieldObj instanceof Enum) {
			String value = String.valueOf(fieldObj);
			if (StringUtil.isNotNull(value) && value.startsWith("org.apache.openjpa.enhance")) {// 由jpa生成的对象不放入
				return;
			}
			map.put(key, value);
		} else if (fieldObj instanceof Date) {
			String datestr = DateFormatCase.YYYY_MM_DD_hhmmss.getInstanc().format(fieldObj);
			map.put(key, datestr);
		} else {
			// 可能是一个复合对象
			List<String> fields = findGetField(fieldObj.getClass());
			if (CollectionUtils.isNotEmpty(fields)) {
				for (String subFields : fields) {
					packMap(map, key, fieldObj, subFields);
				}
			}
		}
	}

	/***
	 * 把map对象转为可序列化的对象，支持用.来级联 主要用于把redis的Map值转为相应的对象
	 * 
	 * @param clazz
	 *            要返回对象的类
	 * @param valueMap
	 *            要返回对象的值，key:如果是 a.b 那么它对应的value则是子对象的值
	 * @return 转换后的对象值
	 */
	public static <T extends Serializable> T convertMapToBean(Class clazz, Map<String, String> valueMap) {
		if (MapUtils.isEmpty(valueMap)) {
			return null;
		}
		try {
			T t = (T) clazz.newInstance();
			for (String key : valueMap.keySet()) {
				String value = valueMap.get(key);
				StringUtil.packObj(t, key, value);
			}
			return t;
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * 判断类是否实现某个接口
	 * 
	 * @param classz
	 *            要判断的类
	 * @param szInterface
	 *            要判断的接口
	 * @return 是否继承了接口，true：是 false：否
	 */
	public static boolean isInterface(Class classz, String szInterface) {
		Class[] face = classz.getInterfaces();
		for (int i = 0, j = face.length; i < j; i++) {
			if (face[i].getName().equals(szInterface)) {
				return true;
			} else {
				Class[] face1 = face[i].getInterfaces();
				for (int x = 0; x < face1.length; x++) {
					if (face1[x].getName().equals(szInterface)) {
						return true;
					} else if (isInterface(face1[x], szInterface)) {
						return true;
					}
				}
			}
		}
		if (null != classz.getSuperclass()) {
			return isInterface(classz.getSuperclass(), szInterface);
		}
		return false;
	}

	/***
	 * 得到类的属性描述
	 * 
	 * @param clazz
	 *            要操作的类
	 * @return 属性描述数组
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return beanInfo.getPropertyDescriptors();
	}

	/***
	 * 得到属性描述所对应的类
	 * 
	 * @param propertyDescriptor
	 *            指定的属性描述
	 * @return 对应的类
	 */
	public static Class getClassRefType(PropertyDescriptor propertyDescriptor) {
		Field[] fields = propertyDescriptor.getClass().getSuperclass().getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			return null;
		} else {
			for (Field field : fields) {
				if ("classRef".equals(field.getName())) {
					try {
						field.setAccessible(true); // 一定要设置为可访问
						return (Class) ((Reference) field.get(propertyDescriptor)).get();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	/***
	 * 得到类所有域及对应的类
	 * 
	 * @param classz
	 *            要操作的类
	 * @return 所有域及对应的类
	 */
	public static Map<String, Class[]> getContextType(Class classz) {
		Field[] fs = classz.getDeclaredFields(); // 得到所有的fields
		Map<String, Class[]> retMap = new HashMap<String, Class[]>();
		for (Field f : fs) {
			Class fieldClazz = f.getType(); // 得到field的class及类型全路径
			if (fieldClazz.isPrimitive())
				continue; // 【1】 //判断是否为基本类型
			if (fieldClazz.getName().startsWith("java.lang"))
				continue; // getName()返回field的类型全路径；
			if (fieldClazz.isAssignableFrom(List.class)) // 【2】
			{
				Type fc = f.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
				if (fc == null)
					continue;
				if (fc instanceof ParameterizedType) // 【3】如果是泛型参数的类型
				{
					ParameterizedType pt = (ParameterizedType) fc;
					Class genericClazz = (Class) pt.getActualTypeArguments()[0]; // 【4】
																					// 得到泛型里的class类型对象。
					retMap.put(f.getName(), new Class[] { genericClazz });
					// Map<String, Class> m1 = prepareMap(genericClazz);
					// m.putAll(m1);
				}
			} else if (fieldClazz.isAssignableFrom(Map.class)) {
				Type fc = f.getGenericType();
				if (fc == null)
					continue;
				if (fc instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) fc;
					Class param0 = (Class) pt.getActualTypeArguments()[0]; // 【4】
					Class param1 = (Class) pt.getActualTypeArguments()[1]; // 【4】
					retMap.put(f.getName(), new Class[] { param0, param1 });
				}
			} else if (fieldClazz.isArray()) {
				retMap.put(f.getName(), new Class[] { fieldClazz.getComponentType() });
			}
		}
		return retMap;
	}

	/***
	 * 复制对象
	 * 
	 * @param dest
	 *            目标对象
	 * @param orig
	 *            源对象
	 * @return 复制是否成功 result.isSuc() 为true:成功，false失败
	 */
	public static Result copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
			return Result.getSuc();
		} catch (Exception e) {
			logger.error("复制属性出错", e);
			return Result.getError(e.getMessage());
		}
	}

	/***
	 * 合并对象，不合并空对象和空字符串
	 * 
	 * @param to
	 *            目标对象
	 * @param from
	 *            要被合并的对象
	 * @param removes
	 *            希望排除的字段
	 */
	public static void mergeObj(Object to, Object from, String... removes) {
		mergeObj(to, from, false, false, removes);
	}

	/***
	 * 合并对象，空字符串还是会合并。
	 * 
	 * @param to
	 *            目标对象
	 * @param from
	 *            要被合并的对象
	 * @param copyNull
	 *            是否复制空对象 true:空值也合并 false:空值不合并
	 * @param removes
	 *            希望排除的字段
	 */
	public static void mergeObj(Object to, Object from, boolean copyNull, String... removes) {
		mergeObj(to, from, copyNull, true, removes);
	}

	/***
	 * 合并对象
	 * 
	 * @param to
	 *            目标对象
	 * @param from
	 *            要被合并的对象
	 * @param copyNull
	 *            是否复制空对象 true:空值也合并 false:空值不合并
	 * @param copyBlank
	 *            是否复制空字符吕 true:空字符串也合并 false:空字符串不合并
	 * @param removes
	 *            希望排除的字段
	 */
	public static void mergeObj(Object to, Object from, boolean copyNull, boolean copyBlank, String... removes) {
		if (from == null)
			return;
		List<String> fields = findGetField(from.getClass());
		for (String field : fields) {
			if (ArrayUtils.contains(removes, field)) {
				continue;
			}
			try {
				Object value = PropertyUtils.getProperty(from, field);
				if ((!copyNull && value == null) || (!copyBlank && StringUtil.isNull(value))) {
					continue;
				}
				BeanUtils.copyProperty(to, field, value);
			} catch (Exception e) {
			}
		}
	}

}
