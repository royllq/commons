package net.wicp.tams.commons.report.excel;

import org.apache.log4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.constant.PathType;

public abstract class ReportAbstract {
	private Logger logger = Logger.getLogger(getClass());
	protected final static String tempdir = PathType.getPath(Conf.get("jxls.dir.temp"));
	protected final static String exportDefault = PathType.getPath(Conf.get("jxls.dir.export"));

	protected final String tempName;

	public abstract void export(boolean issave);

	/****
	 * 没有模板文件直接导出文件
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
