package net.wicp.tams.commons.test.other;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.jxls.common.Context;
import org.jxls.jdbc.JdbcHelper;
import org.jxls.util.JxlsHelper;

import net.wicp.tams.commons.report.excel.ReportAbstract;
import net.wicp.tams.commons.report.excel.jxls.ReportListBean;
import net.wicp.tams.commons.report.excel.jxls.ReportSql;
import net.wicp.tams.commons.test.beans.Employee;

public class TestExcel {

	private final static List<Employee> employees = generateSampleEmployeeData();

	@Test
	public void exportListNoTemp() throws FileNotFoundException, ParseException {
		List<String> headers = Arrays.asList("Name", "Birthday", "Payment");
		ReportAbstract abs = new ReportListBean(employees, "name, birthDate, payment");
		abs.setHeaders(headers);
		abs.exportExcel("exportListNoTemp.xls");
	}

	@Test
	public void exportListTemp() throws IOException {
		List<String> headers = Arrays.asList("Name", "Birthday", "Payment");
		ReportAbstract abs = new ReportListBean("simple_export_template.xlsx", employees, "name, birthDate, payment");
		abs.setHeaders(headers);
		abs.exportExcel("exportListTemp.xls");
	}

	@Test
	public void exportSql() throws IOException, ClassNotFoundException, SQLException, ParseException {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		try (Connection conn = DriverManager.getConnection("jdbc:derby:memory:testDB;create=true")) {
			initData(conn);
			ReportAbstract abs = new ReportSql("sql_demo_template.xls", conn);
			abs.exportExcel("exportSql.xls");
		}
	}

	private static List<Employee> generateSampleEmployeeData() {
		List<Employee> employees = new ArrayList<Employee>();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd", Locale.US);
			employees.add(new Employee("Elsa", dateFormat.parse("1970-Jul-10"), 1500, 0.15));
			employees.add(new Employee("Oleg", dateFormat.parse("1973-Apr-30"), 2300, 0.25));
			employees.add(new Employee("Neil", dateFormat.parse("1975-Oct-05"), 2500, 0.00));
			employees.add(new Employee("Maria", dateFormat.parse("1978-Jan-07"), 1700, 0.15));
			employees.add(new Employee("John", dateFormat.parse("1969-May-30"), 2800, 0.20));
		} catch (Exception e) {
			// TODO: handle exception
		}
		return employees;
	}

	private static void initData(Connection conn) throws SQLException, ParseException {
		String createTableSlq = "CREATE TABLE employee (" + "id INT NOT NULL, " + "name VARCHAR(20) NOT NULL, "
				+ "birthdate DATE, " + "payment DECIMAL, " + "PRIMARY KEY (id))";
		String insertSql = "INSERT INTO employee VALUES (?,?,?,?)";
		List<Employee> employees = generateSampleEmployeeData();
		try (Statement stmt = conn.createStatement()) {
			stmt.executeUpdate(createTableSlq);
			int k = 1;
			try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
				for (Employee employee : employees) {
					insertStmt.setInt(1, k++);
					insertStmt.setString(2, employee.getName());
					insertStmt.setDate(3, new Date(employee.getBirthDate().getTime()));
					insertStmt.setBigDecimal(4, employee.getPayment());
					insertStmt.executeUpdate();
				}
			}
		}
	}
}
