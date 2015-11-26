package net.wicp.tams.commons.callback.impl.convertvalue;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.ArrayUtils;

import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.callback.IConvertValue;
import net.wicp.tams.commons.constant.dic.intf.IEnumCombobox;
/****
 * 枚举类转换器，此枚举类必须继承net.wicp.tams.commons.constant.dic.intf.IEnumCombobox
 * @author zhoujunhui
 *
 */
@SuppressWarnings("rawtypes")
public class ConvertValueEnum implements IConvertValue<String> {
	private Object[] objs;
	private String lan;

	public ConvertValueEnum(Object[] obj, String lan) {
		this.objs = obj;
		this.lan = lan;
	}

	
	public ConvertValueEnum(Class classobj, String lan) {
		this.objs = classobj.getEnumConstants();
		this.lan = lan;
	}

	public ConvertValueEnum(String className, String lan) {
		try {
			Class classobj = Class.forName(className);
			this.objs = classobj.getEnumConstants();
			this.lan = lan;
		} catch (ClassNotFoundException e) {
		}
	}

	@Override
	public String getStr(String key) {
		if (StringUtil.isNull(key) || ArrayUtils.isEmpty(objs))
			return key;
		IEnumCombobox curobj = null;
		for (Object object : objs) {
			IEnumCombobox tempobj = (IEnumCombobox) object;
			if (key.equals(tempobj.getName())) {
				curobj = tempobj;
				break;
			}
		}
		if (curobj == null) {
			return key;
		}
		String filed = StringUtil.isNull(lan) ? "desc" : String.format("desc_%s", lan);
		String retvalue;
		try {
			retvalue = BeanUtils.getSimpleProperty(curobj, filed);
		} catch (Exception e) {
			retvalue = key;
		}
		return retvalue;
	}

}
