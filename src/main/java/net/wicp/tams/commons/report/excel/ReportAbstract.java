package net.wicp.tams.commons.report.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.constant.PathType;

public abstract class ReportAbstract {
	private Logger logger = Logger.getLogger(getClass());
	protected final static String tempdir = PathType.getPath(Conf.get("jxls.dir.temp"));
	protected final static String exportDefault = PathType.getPath(Conf.get("jxls.dir.export"));

	protected final String tempName;

	public String exportExcel(boolean isSaveFile) {
		InputStream is = null;
		try {
			is = StringUtil.isNull(tempName) ? null
					: new FileInputStream(IOUtil.mergeFolderAndFilePath(tempdir, tempName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("模板文件不存在");
		}
		OutputStream os = null;
		export(is, os);
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				logger.info("关闭输入流出错");
			}
		}
		return "";
	}

	public abstract void export(InputStream is, OutputStream os);

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
