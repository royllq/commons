package net.wicp.tams.commons.apiext;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import net.wicp.tams.commons.callback.IValueEncoder;

/***
 * 字符串辅助类
 * 
 * @author andy.zhou
 *
 */
public abstract class StringUtil {

	/****
	 * <p>
	 * 判断字符串是否为空值
	 * </p>
	 *
	 * <pre>
	 * StringUtil.isNull(true,"null")      = true
	 * StringUtil.isNull(true,"Null")      = true
	 * StringUtil.isNull(true,"   ")       = true
	 * StringUtil.isNull(true,"bob")      = false
	 * </pre>
	 * 
	 * @param NullIsTrue
	 *            出现"null"值是否判断为true
	 * @param inputObj
	 *            要判断的字符串
	 * @return true是空值，false不是空值
	 */
	public static boolean isNull(boolean NullIsTrue, Object inputObj) {
		if (inputObj == null) {
			return true;
		}
		String inputstr = trimSpace(String.valueOf(inputObj));
		if (NullIsTrue) {
			return inputstr.equalsIgnoreCase("null")||StringUtils.isBlank(inputstr);
		} else {
			return StringUtils.isBlank(inputstr);
		}
	}

	/****
	 * 判断对象是否为空"null"字符串也为空
	 * 
	 * @param inputObj
	 *            要判断的对象
	 * @return true:是空值 false：不是
	 */
	public static boolean isNull(Object inputObj) {
		return isNull(true, inputObj);
	}

	/****
	 * 判断对象是否不为空，"null"字符串也为空
	 * 
	 * @param inputObj
	 *            要判断的对象
	 * @return true:不是空 false：是
	 */
	public static boolean isNotNull(Object inputObj) {
		return !isNull(true, inputObj);
	}

	/**
	 * 把为空的字符按指定字符返回，<br>
	 * 如果inputStr[0]为null或"" 则取inputStr[1]值<br>
	 * 
	 * @param isDelNullStr
	 *            是否要排除为"null"的字符串，因为String.value(null)=="null"
	 * @param inputStr
	 *            输入要转换的数组
	 * @return String 处理后字符串
	 */
	public static String hasNull(boolean isDelNullStr, String... inputStr) {
		if (inputStr == null)
			return "";
		String returnStr;
		switch (inputStr.length) {
		case 0:
			returnStr = "";
			break;
		case 1:
			returnStr = trimSpace(inputStr[0]);
			break;
		default:
			if (inputStr[0] == null || inputStr[0].trim().length() <= 0)
				returnStr = trimSpace(inputStr[1]);
			else
				returnStr = trimSpace(inputStr[0]);
			break;
		}
		if (isDelNullStr) {
			returnStr = returnStr.equalsIgnoreCase("null") ? "" : returnStr;
		}
		return returnStr;
	}

	public static String hasNull(String... inputStr) {
		return hasNull(true, inputStr);
	}

	/**
	 * 去掉字符串前后的空格(半角,全角空格)
	 * 
	 * @param str
	 *            要处理的字符串
	 * @return String 处理后字符串
	 */
	public static String trimSpace(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}

		int len = str.length();
		int first = 0;
		int last = str.length() - 1;
		char c;

		for (c = str.charAt(first); (first < len) && (c == '\u3000' || c == ' '); first++) {
			c = str.charAt(first);
		}
		if (first > 0) {
			first--;
		}
		if (len > 0) {
			c = str.charAt(last);
			while ((last > 0) && (c == '\u3000' || c == ' ')) {
				last--;
				c = str.charAt(last);
			}
			last = last + 1;
		}
		if (first >= last) {
			return "";
		}
		return ((first > 0) || (last < len)) ? str.substring(first, last) : str;
	}

	/**
	 * 把分转为元
	 * 
	 * @param fen
	 *            要转换的分数值
	 * @return String 返回元
	 * 
	 */
	public static String convertFenToYuan(String fen) {
		return convertFenToYuan(Double.parseDouble(fen));
	}

	/**
	 * 把分转换为元
	 * 
	 * @param fen
	 *            要转换的分数值
	 * @return String 返回元
	 */
	public static String convertFenToYuan(double fen) {
		String result;
		try {
			double yuan = fen / 100;
			result = NumberUtil.handleScale(yuan, 2).toString();
		} catch (Exception ex) {
			result = "0.00";
		}
		return result;
	}

	/***
	 * 获取字符串中含数字和字母的个数
	 * 
	 * @param src
	 *            输计算的字符串
	 * @return 计算的个数
	 */
	public static int sumOfNumLet(String src) {
		String figures = "0123456789";
		String letters = "abcdefghijklmnopqrstuvwxyz";
		int sum = 0;
		for (int i = 0; (src != null) && (i < src.length()); i++) {
			char ch = src.charAt(i);
			if ((figures.indexOf(ch) != -1) || (letters.indexOf(ch) != -1))
				sum++;
		}
		return sum;
	}

	/**
	 * tapestry输出变量 时要填此格式化对象
	 */
	public final static Format formatCommon = new Format() {
		private static final long serialVersionUID = -8271124584977967310L;

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return source;
		}

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
			return toAppendTo.append(obj);
		}
	};

	/**
	 * 首字母转成大写
	 * 
	 * @param s
	 *            要转换字符串
	 * @return 转换后字符串
	 */
	public static String toUpperCaseFirstOne(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		} else if (Character.isUpperCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
		}
	}

	/***
	 * String转为对象，handler为空就是基本数据类型
	 *
	 * @param type
	 *            对象类型
	 * @param v
	 *            对象值
	 * @param handler
	 *            转换规则
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T str2Object(Class<T> type, String v, IValueEncoder<T> handler) {
		Object param = null;
		if (handler != null)
			return handler.toValue(v);

		if (type != String.class && StringUtils.isEmpty(v)) {
			return null;
		}
		if (type == String.class)
			param = v;
		else if (type == int.class || type == Integer.class)
			param = Integer.parseInt(v);
		else if (type == long.class || type == Long.class)
			param = Long.parseLong(v);
		else if (type == byte.class || type == Byte.class)
			param = Byte.parseByte(v);
		else if (type == char.class || type == Character.class)
			param = v.charAt(0);
		else if (type == float.class || type == Float.class)
			param = Float.parseFloat(v);
		else if (type == double.class || type == Double.class)
			param = Double.parseDouble(v);
		else if (type == short.class || type == Short.class)
			param = Short.parseShort(v);
		else if (type == boolean.class || type == Boolean.class)
			param = Boolean.parseBoolean(v);
		else if (Date.class.isAssignableFrom(type))
			param = DateUtil.objToDate(v);
		else if (Enum.class.isAssignableFrom(type)) {
			try {
				param = type.getField(v).get(null);
			} catch (Exception e) {
			}
		} else if (type == BigDecimal.class) {
			try {
				param = new BigDecimal(v);
			} catch (Exception e) {
			}
		} else
			throw new IllegalArgumentException(String.format("object type '%s' not valid", type));
		return (T) param;
	}

	/***
	 * String转为基本数据类型
	 * 
	 * @param type
	 *            对象类型
	 * @param v
	 *            简单值
	 * @return 简单对象
	 */
	public static final <T> T str2Object(Class<T> type, String v) {
		return str2Object(type, v, null);
	}

	/****
	 * 用值设置对象的域，支持级联
	 *
	 * @param retobj
	 *            要设置的对象
	 * @param fieldName
	 *            要设置的对象的域名, a.b 表示子对象
	 * @param value
	 *            要设置的值
	 * @throws Exception
	 *             设置时异常
	 */
	public static void packObj(Object retobj, String fieldName, String value) throws Exception {
		int indexdot = fieldName.indexOf(".");
		if (indexdot <= 0) {
			Field field = null;
			try {
				field = retobj.getClass().getDeclaredField(fieldName);
			} catch (Exception e) {
				return;
			}
			Class<?> fildeClass = field.getType();
			Object valueObj = StringUtil.str2Object(fildeClass, value);
			if (valueObj != null) {
				BeanUtils.setProperty(retobj, fieldName, valueObj);
			}
		} else {
			String fieldNameTrue = fieldName.substring(0, indexdot);
			Field field = null;
			try {
				field = retobj.getClass().getDeclaredField(fieldNameTrue);
			} catch (Exception e) {
				return;
			}
			Class<?> filedClass = field.getType();
			Object fieldvalue = filedClass.newInstance();
			packObj(fieldvalue, fieldName.substring(indexdot + 1), value);
			BeanUtils.setProperty(retobj, fieldNameTrue, fieldvalue);
		}
	}

	/**
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 *            byte[] data
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
}