package net.wicp.tams.commons.callback.impl.optColType;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.callback.IOpt;
import net.wicp.tams.commons.constant.ColType;

/****
 * 对ColType的所有操作
 * 
 * @author zhoujunhui
 *
 */
public abstract class OptAbsColType implements IOpt<ColType> {
	protected ColType colType;

	@Override
	public Result opt(ColType enumObj, Object... params) {
		this.colType = enumObj;
		Result retobj = null;
		switch (enumObj) {
		case dynaBean:
			retobj = doDynaBean(params);
			break;
		case javaBean:
			retobj = doJavaBean(params);
			break;
		case bytes:
			retobj = doBytes(params);
			break;
		case integer:
			retobj = doInteger(params);
			break;
		case string:
			retobj = doString(params);
			break;
		case datetime:
			retobj = doDatetime(params);
			break;
		case enums:
			retobj = doEnums(params);
		default:
			break;
		}
		return retobj;
	}

	protected abstract Result doString(Object... param);

	protected abstract Result doInteger(Object... param);

	protected abstract Result doDatetime(Object... param);

	protected abstract Result doJavaBean(Object... param);

	protected abstract Result doDynaBean(Object... param);

	protected abstract Result doBytes(Object... param);
	
	protected abstract Result doEnums(Object... param);

}
