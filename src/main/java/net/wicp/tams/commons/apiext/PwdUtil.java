package net.wicp.tams.commons.apiext;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

public abstract class PwdUtil {
	/***
	 * Base64压缩
	 * 
	 * @param key
	 * @return
	 */
	public final static String StringToBase64(String key) {
		return new String(Base64.encodeBase64(StringUtil.hexStringToBytes(key)));
	}

	/***
	 * Base64解压
	 * 
	 * @param key
	 * @return
	 */
	public final static String Base64ToString(String key) {
		return StringUtil.bytesToHexString(Base64.decodeBase64(key.getBytes()));
	}

	/***
	 * 产生MD5编码
	 * 
	 * @param s
	 *            要编码的字符串
	 * @return MD5编码
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
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
