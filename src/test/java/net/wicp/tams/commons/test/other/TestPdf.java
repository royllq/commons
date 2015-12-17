package net.wicp.tams.commons.test.other;

import org.junit.Test;

import net.wicp.tams.commons.constant.FilterPattern;
import net.wicp.tams.commons.report.pdf.PdfReader;



public class TestPdf {

	@Test
	public void redpdf() {
		PdfReader r = new PdfReader("ikang.pdf");
		r.putFilter("许海涛", FilterPattern.left);
		r.parse(null, null);
	}
}
