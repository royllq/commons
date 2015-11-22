package net.wicp.tams.commons.callback.impl.convertvalue;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;

import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.callback.IConvertValue;

/***
 * 从一个list选出解释值
 * 
 * @author andy.zhou
 *
 */
public class ConvertValueList implements IConvertValue<String> {
	private final List<?> valList;
	private final String idCol;
	private final String valCol;

	public ConvertValueList(List<?> valList, String idCol, String valCol) {
		this.valList = valList;
		this.idCol = idCol;
		this.valCol = valCol;
	}

	@Override
	public String getStr(String keyObj) {
		if (CollectionUtils.isEmpty(valList) || StringUtil.isNull(idCol) || StringUtil.isNull(valCol)) {
			return keyObj;
		}
		String retstr = keyObj;
		for (Object object : valList) {
			try {
				String id = BeanUtils.getProperty(object, idCol);
				if (StringUtil.hasNull(keyObj).equals(id)) {
					retstr = BeanUtils.getProperty(object, valCol);
					break;
				}
			} catch (Exception e) {
			}
		}
		return retstr;
	}

}
