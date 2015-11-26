package net.wicp.tams.commons.callback;

/***
 * 把某个对象通过某种规则转换成想要的值
 * 
 * @author andy.zhou
 *
 * @param <V>
 *            对象Class
 */
public interface IConvertValue<V> {
	/***
	 * 把对象转换成解释
	 * 
	 * @param keyObj
	 * @return
	 */
	public String getStr(V keyObj);
}
