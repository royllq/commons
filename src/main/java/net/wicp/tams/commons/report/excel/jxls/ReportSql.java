package net.wicp.tams.commons.report.excel.jxls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;

import org.apache.log4j.Logger;
import org.jxls.common.Context;
import org.jxls.jdbc.JdbcHelper;
import org.jxls.util.JxlsHelper;

import net.wicp.tams.commons.report.excel.ReportAbstract;

public class ReportSql extends ReportAbstract {
	private Logger logger = Logger.getLogger(getClass());
	private Context context = null;

	public ReportSql(String tempName, Connection conn, Context context) {
		super(tempName);
		this.context = context == null ? new Context() : context;
		this.context.putVar("conn", conn);
		JdbcHelper jdbcHelper = new JdbcHelper(conn);
		this.context.putVar("jdbc", jdbcHelper);
	}

	public ReportSql(String tempName, Connection conn) {
		super(tempName);
		this.context = new Context();
		this.context.putVar("conn", conn);
		JdbcHelper jdbcHelper = new JdbcHelper(conn);
		this.context.putVar("jdbc", jdbcHelper);
	}

	@Override
	public void export(InputStream is, OutputStream os) {
		try {
			JxlsHelper.getInstance().processTemplate(is, os, context);
		} catch (IOException e) {
			logger.error("导出SQL模板Excel出借", e);
		}

	}

}
