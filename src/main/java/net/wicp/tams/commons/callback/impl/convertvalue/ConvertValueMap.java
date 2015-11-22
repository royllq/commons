package net.wicp.tams.commons.callback.impl.convertvalue;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;

import net.wicp.tams.commons.apiext.ReflectAssist;
import net.wicp.tams.commons.callback.IConvertValue;

/***
 * 从hashmap中拿解释,如果map的值是对象，则还需传入valCol指示要出哪个field做解释字段
 * 
 * @author andy.zhou
 *
 */
public class ConvertValueMap implements IConvertValue<String> {
	private final Map<String, Object> valmap;
	private final String valCol;

	public ConvertValueMap(Map<String, Object> valmap) {
		this.valmap = valmap;
		this.valCol = null;
	}

	public ConvertValueMap(Map<String, Object> valmap, String valCol) {
		this.valmap = valmap;
		this.valCol = valCol;
	}

	@Override
	public String getStr(String keyObj) {
		if (MapUtils.isEmpty(valmap)) {
			return keyObj;
		}
		Object valObj = valmap.get(keyObj);
		if (valObj == null) {
			return keyObj;
		}
		String retstr = keyObj;
		if (valCol == null || ReflectAssist.isPrimitieClass(valObj.getClass())) {
			retstr = String.valueOf(valObj);
		} else {
			try {
				retstr = BeanUtils.getProperty(valObj, valCol);
			} catch (Exception e) {
				retstr = keyObj;
			}
		}
		return retstr;
	}

}
