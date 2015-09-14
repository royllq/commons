package net.wicp.tams.commons.callback;

/****
 * 把某个值通过某种规则转换成想要的值
 */
public interface IConvertValue<V> {
	public String getStr(V keyObj);
}
