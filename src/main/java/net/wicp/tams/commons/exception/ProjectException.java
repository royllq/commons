package net.wicp.tams.commons.exception;

/**
 * @ClassName: ProjectException
 * @Description: 整个项目的异常基类，errorCode不允许修改且必需是ExceptAll所枚举的异常编码。<br>
 *               errorMessage可以修改，如果不修改且是ExceptAll枚举的desc字段。
 * @author 周俊辉
 * @date 2010-10-29 下午03:45:52
 */
@SuppressWarnings("serial")
public class ProjectException extends Exception {
	private IExcept except;

	public ProjectException(IExcept except) {
		super(except.getDesc());
		this.except = except;
	}

	public ProjectException(IExcept except, Throwable cause) {
		super(except.getDesc(), cause);
		this.except = except;
	}

	/**
	 * 当有自定义的错误原因时可用此构造函数
	 * */
	public ProjectException(IExcept except, String errMsg) {
		super(errMsg);
		this.except = except;
	}

	public String getMessage(Object errBean) {
		String errmsg = null;
		if (errBean == null) {
			errmsg = except.getErrMsg();
		} else {
			errmsg = except.getErrMsg(errBean);
		}
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] es = this.getStackTrace();
		for (int i = 0; i < es.length; i++) {
			sb.append(es[i] + "\n");
		}
		return String.format(
				"错误代码: [code=%s],[value=%s],[message=%s],原始异常产生原因:\n%s",
				except.getErrorCode(), except.getErrorValue(), errmsg,
				sb.toString());
	}

	@Override
	public String getMessage() {
		return getMessage(null);
	}

	public IExcept getExcept() {
		return this.except;
	}
}
