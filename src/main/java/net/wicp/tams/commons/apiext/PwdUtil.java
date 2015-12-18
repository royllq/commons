package net.wicp.tams.commons.apiext;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.constant.DES;

public abstract class PwdUtil {
	private final static Logger logger = LogHelp.getLogger(PwdUtil.class);

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
			logger.error("MD5加密失败");
			return null;
		}
	}

	/***
	 * 计算24位长的密码byte值,首先对原始密钥做MD5算hash值，再用前8位数据对应补全后8位
	 * 
	 * @param strKey
	 * @return
	 * @throws Exception
	 */
	private static byte[] GetKeyBytes(String strKey) throws Exception {
		if (null == strKey || strKey.length() < 1)
			throw new Exception("key is null or empty!");
		java.security.MessageDigest alg = java.security.MessageDigest.getInstance("MD5");
		alg.update(strKey.getBytes(Conf.get("common.encode")));
		byte[] bkey = alg.digest();
		int start = bkey.length;
		byte[] bkey24 = new byte[24];
		for (int i = 0; i < start; i++) {
			bkey24[i] = bkey[i];
		}
		for (int i = start; i < 24; i++) {// 为了与.net16位key兼容
			bkey24[i] = bkey[i - start];
		}
		return bkey24;
	}

	/****
	 * 加密数据
	 * 
	 * @param keybyte
	 *            24个字节的加密密钥
	 * @param src
	 *            被加密的数据缓冲区（源）
	 * @param des
	 *            加密算法
	 * @return
	 */
	private static byte[] encryptMode(byte[] keybyte, byte[] src, DES des) {

		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, des.name()); // 加密
			Cipher c1 = Cipher.getInstance(des.name());
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (Exception e1) {
			logger.error("加密数据失败", e1);
		}
		return null;
	}

	private static byte[] decryptMode(byte[] keybyte, byte[] src, DES des) {
		try {
			SecretKey deskey = new SecretKeySpec(keybyte, des.name());// 生成密钥
			Cipher c1 = Cipher.getInstance(des.name());// 解密
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (Exception e) {
			logger.error("解密数据失败", e);
		}
		return null;
	}

	/***
	 * 通过key 用3DES加密
	 * 
	 * @param origValue
	 *            要加密的数数据
	 * @param key
	 *            加密用的key
	 * @return
	 */
	public static String Encrypt3DES(String origValue, String key) {
		String str = null;
		try {
			str = Base64.encodeBase64String(
					encryptMode(GetKeyBytes(key), origValue.getBytes(Conf.get("common.encode")), DES.DESede));
		} catch (Exception e) {
			logger.error("加密失败", e);
		}
		return str;
	}

	/***
	 * 用配置文件的配置做为key加密
	 * 
	 * @param origValue
	 *            要加密的数数据
	 * @return 加密后的数据
	 */
	public static String Encrypt3DES(String origValue) {
		return Encrypt3DES(origValue, Conf.get("3des.key"));
	}

	/***
	 * 通过key 用3DES加密,是Encrypt3DES的逆过程
	 * 
	 * @param value
	 *            要解密的密文
	 * @param key
	 *            要解密的key值
	 * @return
	 */
	public static String Decrypt3DES(String value, String key) {
		try {
			byte[] b = decryptMode(GetKeyBytes(key), new Base64().decode(value), DES.DESede);
			return new String(b);
		} catch (Exception e) {
			logger.error("解密失败", e);
			return null;
		}
	}

	/***
	 * 用配置文件的配置做为key解密
	 * 
	 * @param value
	 *            要解密的密文
	 * @return 解密后的源文
	 */
	public static String Decrypt3DES(String value) {
		return Decrypt3DES(value, Conf.get("3des.key"));
	}
}
