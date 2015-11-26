package net.wicp.tams.commons;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONObject;

import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.IExcept;
import net.wicp.tams.commons.exception.ProjectException;
import net.wicp.tams.commons.exception.bean.ParamInfoBean;

/**
 * 操作返回的对象，exceptAll是必须要有的对象
 * */
public class Result implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private boolean result;// true:正确,false:业务出错
	private String message;
	private IExcept except;// 异常编码
	private Object[] retObjs;// 操作成功后，如果想带一些返回值在此设置

	// ////////////临时用变量不对外公开//////////////////////////
	private static Result suc = null;

	/**
	 * 由异常来构建返回结果
	 * */
	public Result(ProjectException opeExcept) {
		this.result = false;
		if (opeExcept == null) {
			throw new IllegalArgumentException();
		}
		this.except = opeExcept.getExcept();
	}

	public Result(IExcept except) {
		if (except == ExceptAll.no)
			this.result = true;
		else
			this.result = false;
		this.except = except;
	}

	private Result(boolean result) {
		this.result = result;
	}

	private Result(String message) {
		this.result = false;
		this.except = ExceptAll.project_undefined;
		this.message = message;
	}

	/****
	 * 得到成功的返回结果，单例
	 * 
	 * @return
	 */
	public static Result getSuc() {
		if (suc == null) {
			suc = new Result(true);
			suc.except = ExceptAll.no;
		}
		return suc;
	}

	/***
	 * 得到错误的结果
	 * 
	 * @param errmsg
	 * @return
	 */
	public static Result getError(String errmsg) {
		return new Result(errmsg);
	}

	/**
	 * 得到错误编码，如果正确则为“no”
	 * 
	 * @return
	 */
	public IExcept getExcept() {
		return this.except;
	}

	public boolean isSuc() {
		return result;
	}

	/***
	 * 返回的对象，注意读与取的先后顺序
	 * 
	 * @return
	 */
	@java.beans.Transient
	public Object[] getRetObjs() {
		return retObjs;
	}

	public Object getRetObj(int index) {
		if (retObjs == null || retObjs.length <= index) {
			return null;
		}
		return retObjs[index];
	}

	/***
	 * 设置返回值
	 * 
	 * @param retObjs
	 */
	public void setRetObjs(Object... retObjs) {
		this.retObjs = retObjs;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String getMessage(ParamInfoBean errBean) {
		if (StringUtils.isNotBlank(this.message)) {
			return this.message;
		}
		return this.except.getErrMsg(errBean);
	}

	public String getMessage() {
		if (StringUtils.isNotBlank(this.message)) {
			return this.message;
		}
		return this.except.getErrMsg();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public JSONObject getJsonObj(ParamInfoBean errBean) {
		String message = getMessage(errBean);
		return new JSONObject("result", result ? 1 : 0,"value",this.except.getErrorValue(), "code",
				this.except.getErrorCode(), "msg", message);
	}

	public JSONObject getJsonObj() {
		String message = getMessage();
		return new JSONObject("result", result ? 1 : 0, "value",this.except.getErrorValue(),"code",
				this.except.getErrorCode(), "msg", message);
	}

}
