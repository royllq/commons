package net.wicp.tams.commons.callback;

import net.wicp.tams.commons.Result;

/***
 * 对于枚举类型要做的操作
 * @author zhoujunhui
 *
 */
@SuppressWarnings("rawtypes")
public interface IOpt<V extends Enum> {
	public Result opt(V enumObj,Object... params);
}
