package net.wicp.tams.commons.test.other;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.wicp.tams.commons.report.excel.ReportAbstract;
import net.wicp.tams.commons.report.excel.jxls.ReportListBean;
import net.wicp.tams.commons.test.beans.AddrInfo;

public class TestExcel {

	@Test
	public void exportList(){
		List<AddrInfo> addrs=new ArrayList<>();
		addrs.add(new AddrInfo("aaa", "344345"));
		ReportAbstract abs=new ReportListBean(addrs, "addr");
		abs.exportExcel("aaa.xls");
	}
}
