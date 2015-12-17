package net.wicp.tams.commons.report.pdf.parse;

import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;

public class PdfReaderTools {
	public static String parsePDF(File file, Integer start, Integer end) {
		if (file == null) {
			return null;
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			PDFParser p = new PDFParser(fis);
			p.parse();

			PDFTextStripper ts = new PDFTextStripper();
			ts.setSortByPosition(true);
			if (start != null) {
				ts.setStartPage(start);
			}
			if (end != null) {
				ts.setEndPage(end);
			}

			String s = ts.getText(p.getPDDocument());
			fis.close();
			return s;
		} catch (Exception e) {
			return null;
		}
	}
}
