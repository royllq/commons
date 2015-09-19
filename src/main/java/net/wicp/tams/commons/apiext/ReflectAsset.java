package net.wicp.tams.commons.apiext;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.callback.IConvertValue;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 反身辅助类
 * 
 * @author andy.zhou
 *
 */
@SuppressWarnings("rawtypes")
public class ReflectAsset {
	private final static Logger logger = LogHelp.getLogger(ReflectAsset.class);
	private final static String[] excludeGet = new String[] { "getClass" };

	/***
	 * 方法调用
	 * 
	 * @param invokeObj
	 *            拥有此方法的对象
	 * 
	 * @param methodName
	 *            方法名
	 * 
	 * @param param
	 *            调用参数
	 * @return 调用结果
	 * @throws ProjectException
	 *             执行失败
	 */
	public static Object invokeMothed(Object invokeObj, String methodName, Object... param) throws ProjectException {
		if (invokeObj == null || StringUtils.isBlank(methodName)) {
			throw new ProjectException(ExceptAll.Project_default, "反射中缺少类或方法");
		}
		Class c = invokeObj.getClass();
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
			} catch (IllegalArgumentException e) {
				throw new ProjectException(ExceptAll.Project_default);
			} catch (IllegalAccessException e) {
				throw new ProjectException(ExceptAll.Project_default);
			} catch (InvocationTargetException e) {
				throw new ProjectException(ExceptAll.Project_default);
			}
		} else {
			return null;
		}
	}

	/****
	 * 调用静态方法
	 * 
	 * @param className
	 *            静态方法所在类名
	 * @param methodName
	 *            静态方法名
	 * @param paramclass
	 *            方法参数类型
	 * @param param
	 *            调用时传进来的参数
	 * @return 返回对象
	 * @throws Exception
	 *             调用异常
	 */
	public static Object invokeStaticMothed(String className, String methodName, Class[] paramclass, Object... param)
			throws Exception {
		Class c = Class.forName(className);
		Method m = c.getMethod(methodName, paramclass);
		Object retobj = m.invoke(c, param);
		return retobj;
	}

	/***
	 * 是否是基本数据类型
	 * 
	 * @param clz
	 *            要检查的类型
	 * @return true 是，false 不是
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
	 *            指定的类
	 * @return 所有get方法
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
	 *            指定的类
	 * @return 所有get方法对应的域
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
	 *            要转换的对象
	 * @param convermap
	 *            要转换的规则
	 * @param allowNull
	 *            空值要不要进入Map
	 * @return 转换结果
	 */
	public static Map<String, String> convertMapFromBean(Object obj, Map<String, IConvertValue> convermap,
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
						value = convert.getStr(BeanUtils.getProperty(obj, field));
					}
					if (!allowNull && StringUtils.isBlank(value)) {// 不允许为空但又是空值
						continue;
					}
					if (StringUtils.isBlank(value) && value.startsWith("org.apache.openjpa.enhance")) {// 由jpa生成的对象不放入
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
	 * 判断类是否实现某个接口
	 * 
	 * @param c
	 *            类
	 * @param szInterface
	 *            接口
	 * @return true 实现，false没有实现
	 */
	public static boolean isInterface(Class c, String szInterface) {
		Class[] face = c.getInterfaces();
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
		if (null != c.getSuperclass()) {
			return isInterface(c.getSuperclass(), szInterface);
		}
		return false;
	}

	/***
	 * 得到对象的属性描述
	 * 
	 * @param clazz
	 *            类
	 * @return 属性描述
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
	 * 通过属性描述得到该属性对应的Class类型
	 * 
	 * @param propertyDescriptor
	 *            属性描述
	 * @return 异性的类型
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

	/****
	 * 得到类的域对应的类型，如果是Map，则值Class[]有两个值，key和value的类型，其它Class[]只有一个值
	 * 
	 * @param classz
	 *            指定类
	 * @return 域类型Map
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
	 * 对象的复制
	 * 
	 * @param dest
	 *            目标对象
	 * @param orig
	 *            原对象
	 */
	public static void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (Exception e) {
			logger.error("复制属性出错", e);
		}
	}

}
