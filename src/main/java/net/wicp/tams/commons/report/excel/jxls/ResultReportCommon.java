package net.wicp.tams.commons.report.excel.jxls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.collections.CollectionUtils;

import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.exception.ProjectException;

public class ResultReportCommon extends ResultSetReportAbs {

	private List<String> titleList;

	private ResultReportCommon(ResultSet rs) {
		super(rs);
	}

	public static ResultReportCommon newInstall(List<String> titleList, Connection conn, String sql) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (CollectionUtils.isEmpty(titleList)) {// 如果没有传入标题，则直接取ＳＱＬ中的列名做标题
				ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
				int columnCnt = rsmd.getColumnCount();
				if (columnCnt > 0) {
					titleList = new ArrayList<String>();
					for (int i = 1; i < columnCnt + 1; i++) {
						titleList.add(rsmd.getColumnName(i));
					}
				}
			}
			ResultReportCommon retObj = new ResultReportCommon(rs);
			retObj.setTitleList(titleList);
			return retObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> getInputMap() {
		Map beans = new HashMap();
		try {
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, super.lower);
			beans.put("rs", rsdc.getRows());
			beans.put("cols", titleList);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return beans;
	}

	@Override
	public void initParam() throws ProjectException {
		super.setInputStream(IOUtil.class.getResourceAsStream("/template/excel/commonSql.xls"));

		// super.setSrcFileName("commonSql.xls");
		// super.setSrcFilePath(IOUtil.getDirForCommonUtilFilePath("/template/excel/commonSql.xls"));
	}

	public List<String> getTitleList() {
		return titleList;
	}

	public void setTitleList(List<String> titleList) {
		this.titleList = titleList;
	}
}
