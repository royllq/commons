package net.wicp.tams.commons.apiext;

import java.security.MessageDigest;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName: StringUtil
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 周俊辉
 * @date 2010-10-29 下午01:36:36
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
	 * @return
	 */
	public static boolean isNull(boolean NullIsTrue, Object inputObj) {
		if (inputObj == null) {
			return true;
		}
		String inputstr = trimSpace(String.valueOf(inputObj));
		if (NullIsTrue) {
			return inputstr.equalsIgnoreCase("null");
		} else {
			return StringUtils.isBlank(inputstr);
		}
	}

	public static boolean isNull(Object inputObj) {
		return isNull(true, inputObj);
	}

	/**
	 * 把为空的字符按指定字符返回，<br>
	 * 如果inputStr[0]为null或"" 则取inputStr[1]值<br>
	 * 
	 * @param isDelNullStr
	 *            是否要排除为"null"的字符串，因为String.value(null)=="null"
	 * @param inputStr
	 *            输入要转换的数组
	 * @return String
	 * */
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
	 * @return String
	 */
	public static String trimSpace(String str) {
		if (str == null || str.trim().length() == 0) {
			return "";
		}

		int len = str.length();
		int first = 0;
		int last = str.length() - 1;
		char c;

		for (c = str.charAt(first); (first < len)
				&& (c == '\u3000' || c == ' '); first++) {
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
	 * @return String
	 * 
	 * */
	public static String convertFenToYuan(String fen) {
		return convertFenToYuan(Double.parseDouble(fen));
	}

	/**
	 * 把分转换为元
	 * 
	 * @param fen
	 *            要转换的分数值
	 * @return String
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

	/**
	 * 获取字符串中含数字和字母的个数<br>
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
	 * */
	public final static Format formatCommon = new Format() {
		private static final long serialVersionUID = -8271124584977967310L;

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return source;
		}

		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos) {
			return toAppendTo.append(obj);
		}
	};

	public static String combo(String[] paramArrayOfString, String paramString) {
		String str1 = paramString;
		if ((paramArrayOfString == null) || (paramArrayOfString.length < 1))
			return "";
		if ((paramString == null) || (paramString.trim().equals("")))
			str1 = ",";
		String str2 = trimSpace(paramArrayOfString[0]);
		int i = paramArrayOfString.length;
		for (int j = 1; j < i; ++j)
			str2 = new StringBuilder().append(str2).append(str1)
					.append(trimSpace(paramArrayOfString[j])).toString();
		return str2;
	}

	/**
	 * 首字母转成大写
	 * 
	 * @param s
	 * @return
	 */
	public static String toUpperCaseFirstOne(String s) {
		if (StringUtils.isBlank(s)) {
			return "";
		} else if (Character.isUpperCase(s.charAt(0))) {
			return s;
		} else {
			return (new StringBuilder())
					.append(Character.toUpperCase(s.charAt(0)))
					.append(s.substring(1)).toString();
		}
	}

	/***
	 * 产生MD5编码
	 * 
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}
}