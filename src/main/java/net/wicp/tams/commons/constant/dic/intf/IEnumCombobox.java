package net.wicp.tams.commons.constant.dic.intf;

/***
 * 枚举类需要下拉列表必须要实现的接口
 * 
 * @author andy.zhou
 *
 */
public interface IEnumCombobox {
	/****
	 * 得到枚举对象的name值
	 * 
	 * @return
	 */
	public String getName();

	/****
	 * 得到枚举对象的解释，如果没有任何的local或 不支持的local，时会调此方法解释
	 * 
	 * @return 解释值
	 */
	public String getDesc();

	/****
	 * 得到英文的解释值
	 * 
	 * @return 解释值
	 */
	public String getDesc_en();

	/****
	 * 得到中文的解释值
	 * 
	 * @return 解释值
	 */
	public String getDesc_zh();

}
