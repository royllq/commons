package net.wicp.tams.commons.report.excel;

import org.apache.log4j.Logger;

public abstract class ReportAbstract {
	private Logger logger = Logger.getLogger(getClass());
	// private final HttpServletResponse response;// 直接返回到页面
	// private final OutputStream output;// 直接传入文件的OutputStream表示要导出为文件
	/// private final String fileName;// 从默认路径 下的文件名

	// private OutputStream os = new BufferedOutputStream(new
	// FileOutputStream(destFileUrl));

	private final String tempName;

	/****
	 * 直接导出文件
	 * 
	 * @param response
	 */
	public ReportAbstract() {
		this.tempName = null;
	}

	public ReportAbstract(String tempName) {
		this.tempName = tempName;
	}

}
