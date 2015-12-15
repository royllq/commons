package net.wicp.tams.commons.report.excel.jxls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.jxls.template.SimpleExporter;
import net.wicp.tams.commons.apiext.CollectionUtil;

public class ReportListBean extends net.wicp.tams.commons.report.excel.ReportAbstract {

	private final List<?> datalist;// 要导出的数据
	private final String[] cols;// 需要导出的列名

	public ReportListBean(List<?> datalist, String... cols) {
		super();
		this.datalist = CollectionUtils.isEmpty(datalist) ? new ArrayList<>() : datalist;
		this.cols = cols;
	}

	public ReportListBean(String tempName, List<?> datalist, String... cols) {
		super(tempName);
		this.datalist = CollectionUtils.isEmpty(datalist) ? new ArrayList<>() : datalist;
		this.cols = cols;
	}

	@Override
	public void export(InputStream is, OutputStream os) {
		SimpleExporter exporter = new SimpleExporter();
		if (is != null) {
			try {
				exporter.registerGridTemplate(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (headers == null) {
			headers = new ArrayList<>();
		}

		exporter.gridExport(headers, datalist, CollectionUtil.arrayJoin(cols, ","), os);
	}

}
