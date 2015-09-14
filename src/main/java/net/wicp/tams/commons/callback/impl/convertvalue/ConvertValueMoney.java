package net.wicp.tams.commons.callback.impl.convertvalue;

import java.math.BigDecimal;

import net.wicp.tams.commons.apiext.NumberUtil;
import net.wicp.tams.commons.callback.IConvertValue;

/***
 * 把钱转为字符
 * @author zhoujunhui
 *
 */
public class ConvertValueMoney implements IConvertValue<String> {

	@Override
	public String getStr(String keyObj) {
		try {
			BigDecimal retObj = NumberUtil.handleScale(String.valueOf(keyObj),
					2);
			return String.valueOf(retObj);
		} catch (Exception e) {
			return keyObj;
		}
	}

}
