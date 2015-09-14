package net.wicp.tams.commons.callback.impl.optGType;

import net.wicp.tams.commons.Result;
import net.wicp.tams.commons.callback.IOpt;
import net.wicp.tams.commons.constant.ColGType;

/*****
 * 对于Gtype所做的操作
 * 
 * @author zhoujunhui
 *
 */
public abstract class OptAbsGType implements IOpt<ColGType> {
	@Override
	public Result opt(ColGType enumObj, Object... params) {
		Result retobj = null;
		switch (enumObj) {
		case single:
			retobj = doSingle(params[0]);
			break;
		case list:
			retobj = doList(params[0]);
			break;
		case map:
			retobj = doMap(params[0]);
			break;
		case array:
			retobj = doArray(params[0]);
			break;
		default:
			break;
		}
		return retobj;
	}

	protected abstract Result doSingle(Object param);

	protected abstract Result doArray(Object param);

	protected abstract Result doMap(Object param);

	protected abstract Result doList(Object param);

}
