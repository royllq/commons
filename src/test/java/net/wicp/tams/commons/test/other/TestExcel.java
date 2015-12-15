package net.wicp.tams.commons.test.other;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.jxls.template.SimpleExporter;

import net.wicp.tams.commons.report.excel.ReportAbstract;
import net.wicp.tams.commons.report.excel.jxls.ReportListBean;
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
		/*try (InputStream is = TestExcel.class.getResourceAsStream("/template/excel/simple_export_template.xlsx")) {
            try  {
            	OutputStream os2 = new FileOutputStream("simple_expor2.xlsx");
            	SimpleExporter exporter = new SimpleExporter();
                exporter.registerGridTemplate(is);
                List<String>  headers = Arrays.asList("Name", "Payment", "Birth Date");
                exporter.gridExport(headers, employees, "name,payment, birthDate", os2);
            }catch(Exception e){
            	e.printStackTrace();
            }
        }*/
		
		List<String> headers = Arrays.asList("Name", "Birthday", "Payment");
		ReportAbstract abs = new ReportListBean("simple_export_template.xlsx", employees, "name, birthDate, payment");
		abs.setHeaders(headers);
		abs.exportExcel("exportListTemp.xls");
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
}
