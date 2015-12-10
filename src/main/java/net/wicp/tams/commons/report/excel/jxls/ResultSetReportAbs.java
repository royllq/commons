package net.wicp.tams.commons.report.excel.jxls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.RowSetDynaClass;

import net.wicp.tams.commons.exception.ProjectException;

/****
 * 通过ResultSet或SQL导出Excel SQL放到Java中
 * 
 * @author 周俊辉
 *
 */
public abstract class ResultSetReportAbs extends ReportAbstract {
	protected String rsName = "rs";// Result在Excell里的默认别名

	protected boolean lower = true;// 默认全部转为小写true

	protected ResultSet rs;

	public ResultSetReportAbs(ResultSet rs) {
		this.rs = rs;
	}

	public static ResultSetReportAbs getResultSetReportByRs(ResultSet rs) {
		return new ResultSetReportAbs(rs) {
			@Override
			public void initParam() throws ProjectException {
				// 不设置任何参数
			}
		};
	}

	public static ResultSetReportAbs getResultSetReportBySql(Connection conn, String sql) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			return getResultSetReportByRs(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String exportExcel(boolean isSaveFile) throws Exception {
		try {
			String retStr = super.exportExcel(isSaveFile);
			return retStr;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (this.rs != null) {
				this.rs.close();
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getInputMap() {
		Map beans = new HashMap();
		try {
			RowSetDynaClass rsdc = new RowSetDynaClass(rs, this.lower);
			beans.put("rs", rsdc.getRows());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return beans;
	}

	public String getRsName() {
		return rsName;
	}

	public void setRsName(String rsName) {
		this.rsName = rsName;
	}

	public void setLower(boolean lower) {
		this.lower = lower;
	}
}
