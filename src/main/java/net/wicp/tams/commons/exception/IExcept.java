package net.wicp.tams.commons.exception;

/****
 * 自定义的异常需要实现的接口
 * 
 * @author zhoujunhui
 *
 */
public interface IExcept {
	/****
	 * 得到错误的描述，一般来说提供给开发人员查看或记录到log
	 * 
	 * @return
	 */
	public String getDesc();

	/***
	 * 得到错误值
	 * 
	 * @return
	 */
	public int getErrorValue();

	/***
	 * 得到错误编码
	 * 
	 * @return
	 */
	public String getErrorCode();

	/***
	 * 得到错误的国际化提示信息可以提供给客户查看
	 * 
	 * @param errBean
	 *            错误信息参数
	 * @return
	 */
	public String getErrMsg(Object errBean);


	/****
	 * 返回错误信息
	 * 
	 * @return
	 */
	public String getErrMsg();

}
