package net.wicp.tams.commons.report.excel.jxls;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import net.sf.jxls.transformer.XLSTransformer;
import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.constant.DateFormatCase;
import net.wicp.tams.commons.exception.ExceptAll;
import net.wicp.tams.commons.exception.ProjectException;

/**
 * @ClassName: ReportAbstract
 * @Description: jxls辅助抽象类
 * @author 周俊辉
 * @date 2010-10-29 下午05:18:19
 * 
 */
public abstract class ReportAbstract {
	private  Logger logger = Logger.getLogger(getClass());

	private String srcFilePath = "";
	private String srcFileName = "";
	private String destFilePath = "";
	private String destFileName = "tempFileName";
	private InputStream inputStream;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	private Workbook resultWorkBook;// 如果不在服务器生成文件，导出的结果会放到这个结果里。

	public String exportExcel(boolean isSaveFile) throws Exception {
		initParam();
		if (StringUtil.isNull(srcFilePath)) {
			srcFilePath = Conf.get("jxls.temp.dir");
		}

		if (inputStream == null && (StringUtil.isNull(srcFilePath) || StringUtil.isNull(srcFileName))) {
			throw new ProjectException(ExceptAll.param_error);
		}

		if (StringUtil.isNull(destFilePath)) {
			destFilePath = srcFilePath = Conf.get("jxls.export.dir");
		}
		File tempFile = new File(IOUtil.mergeFolderAndFilePath(srcFilePath, srcFileName));
		if (!tempFile.exists() && inputStream == null)// 没有设置输入流或模板文件不存在
			throw new ProjectException(ExceptAll.param_error);

		File exportDir = new File(destFilePath);
		if (!exportDir.exists()) {
			exportDir.mkdir();
		}
		String exportFileNameRandom = Integer.toString(Math.abs(new Random().nextInt()));
		XLSTransformer transformer = new XLSTransformer();
		Map<String, Object> inputMap = getInputMap();
		String srcFileUrl = IOUtil.mergeFolderAndFilePath(srcFilePath, srcFileName);
		int lastDotIndex= destFileName.lastIndexOf(".") ;
		String fileNameNotFix= lastDotIndex>0?destFileName.substring(0, lastDotIndex):destFileName;
		String destFileUrl = exportDir.getPath() + File.separator +   fileNameNotFix
				+ DateFormatCase.YYYYMMDD.getInstanc().format(new Date()) + exportFileNameRandom + ".xls";
		logger.debug("inputMap=" + inputMap + "  srcFileUrl = " + srcFileUrl + " destFileUrl=" + destFileUrl);
		InputStream is = inputStream != null ? inputStream : new BufferedInputStream(new FileInputStream(srcFileUrl));
		System.setProperty("javax.xml.parsers.SAXParserFactory",
				"com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");// 设置解析Excel的类，它会首先找到
		Workbook resultWorkBook = transformer.transformXLS(is, inputMap);
		is.close();
		this.resultWorkBook = resultWorkBook;
		if (isSaveFile) {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(destFileUrl));
			resultWorkBook.write(os);
			os.flush();
			os.close();
			return destFileUrl;
		}
		return "";
	}

	public abstract Map<String, Object> getInputMap();

	public abstract void initParam() throws ProjectException;

	public String getSrcFilePath() {
		return srcFilePath;
	}

	public void setSrcFilePath(String srcFilePath) {
		this.srcFilePath = srcFilePath;
	}

	public String getSrcFileName() {
		return srcFileName;
	}

	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	public String getDestFilePath() {
		return destFilePath;
	}

	public void setDestFilePath(String destFilePath) {
		this.destFilePath = destFilePath;
	}

	public String getDestFileName() {
		return destFileName;
	}

	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}

	public Workbook getResultWorkBook() {
		return resultWorkBook;
	}
}
