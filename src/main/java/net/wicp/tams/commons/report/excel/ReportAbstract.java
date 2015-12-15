package net.wicp.tams.commons.report.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
	protected List<String> headers;// excel的标题

	public void exportExcel(OutputStream os) {
		InputStream is = null;
		try {
			is = StringUtil.isNull(tempName) ? null
					: new FileInputStream(IOUtil.mergeFolderAndFilePath(tempdir, tempName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("模板文件不存在");
		}
		export(is, os);
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				logger.info("关闭输入流出错");
			}
		}
	}

	public void exportExcel(HttpServletResponse response) {
		try {
			exportExcel(response.getOutputStream());
		} catch (IOException e) {
			logger.error("关闭输入流出错", e);
			throw new RuntimeException("导出文件出错");
		}
	}

	public String exportExcel(String fileName) {
		OutputStream os;
		try {
			File outdir = new File(exportDefault);
			if (!outdir.exists()) {
				outdir.mkdir();
			}
			String outpath = IOUtil.mergeFolderAndFilePath(exportDefault, fileName);
			os = new FileOutputStream(outpath);
			exportExcel(os);
			return outpath;
		} catch (FileNotFoundException e) {
			logger.error("打开输出流出错", e);
			throw new RuntimeException("导出文件出错");
		}
	}

	public abstract void export(InputStream is, OutputStream os);

	////////////////////////////////////////////////////// getset方法///////////////////////////////////////////////////////////////
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

	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

}
