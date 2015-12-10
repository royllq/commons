package net.wicp.tams.commons.report.excel.jxls;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument.Config;

import net.sf.jxls.report.ReportManager;
import net.sf.jxls.report.ReportManagerImpl;
import net.wicp.tams.commons.exception.ProjectException;

/****
 * 通过SQL导出记录，在Excel里定义SQL,支持参数。
 * 如果有较为复杂的信息组装进Map，可以继承此类，此时不要用：getSqlReportDefault
 * 
 * @author 周俊辉
 */
// SqlReportAbs sr = SqlReportAbs.getSqlReportDefault();
// sr.setSrcFilePath("D:/workspace_all/commons-util/src/main/resources/template/excel");
// sr.setSrcFileName("report.xls");
// sr.setDestFilePath("D:/workspace_all/commons-util/src/main/resources/template/excel");
// sr.setDestFileName("test.xls");
// Map<String, Object> inputParam = new HashMap<String, Object>();
// inputParam.put("channel", 538);
// sr.setSqlPraram(inputParam);
public abstract class SqlReportAbs extends ReportAbstract {
	private Map<String, Object> sqlPraram;

	protected Connection connection;

	public Map<String, Object> getSqlPraram() {
		return sqlPraram;
	}

	/***
	 * 要传入的参数，如果子类有参数一定要覆盖此方法进行设置。"rm"为固定的执行ＳＱＬ的参数
	 * 
	 * @return
	 */
	public void setSqlPraram(Map<String, Object> sqlPraram) {
		this.sqlPraram = sqlPraram;
	}

	/****
	 * 如果没有查询的参数，可以使用此方法返回要导出的对象 模板名称和导出目录通过set方法进行设置
	 * 
	 * @return
	 */
	public static SqlReportAbs getSqlReportDefault() {
		return new SqlReportAbs() {
			@Override
			public void initParam() throws ProjectException {
				// 不设置任何参数
			}
		};
	}
	
	private SqlReportAbs(Connection connection){
		this.connection = connection;
	}
	private SqlReportAbs(){
		
	}
	

	/****
	 * 传入connection
	 * 
	 * @param connection
	 * @return
	 */
	public static SqlReportAbs getSqlReportDefault(final Connection connection) {
		return new SqlReportAbs(connection) {
			@Override
			public void initParam() throws ProjectException {
				// 不设置任何参数
			}
		};
	}

	@Override
	public Map<String, Object> getInputMap() {
		Map beans = new HashMap();
		try {
			ReportManager reportManager = new ReportManagerImpl(
					this.connection == null ? JDBCUtil.getConnection(Config.getConnectorProperties())
							: this.connection, beans);
			beans.put("rm", reportManager);
			if (sqlPraram != null
					&& CollectionUtils.isNotEmpty(sqlPraram.keySet())) {
				beans.putAll(sqlPraram);
			}
		} catch (ProjectException e) {
			e.printStackTrace();
		}
		return beans;
	}
}
