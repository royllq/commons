package net.wicp.tams.commons.report.excel.jxl;

import java.io.InputStream;
import java.io.OutputStream;

import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.report.excel.ReportAbstract;

/****
 * 导出Excel文件,速度较快文件内容格式如下: 标题1,标题2\r\n aaa,bbb\r\n ccc,ccc
 * 
 * @author andy.zhou
 *
 */
public class ReportFast extends ReportAbstract {

	private final String context;
	private final String sheetTitle;

	public ReportFast(String context, String sheetTitle) {
		this.context = context;
		this.sheetTitle = sheetTitle;
	}

	public ReportFast(String context) {
		this.context = context;
		this.sheetTitle = null;
	}

	@Override
	public void export(InputStream is, OutputStream os) {
		try {
			String sheetTitleTrue = StringUtil.isNull(this.sheetTitle) ? "sheet1" : this.sheetTitle;
			WritableWorkbook wbook = jxl.Workbook.createWorkbook(os); // 建立excel文件
			WritableSheet wsheet = wbook.createSheet(sheetTitleTrue, 0); // sheet名称
			WritableFont wfont = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			WritableCellFormat wcfFC = new WritableCellFormat(wfont);
			wcfFC.setBackground(Colour.AQUA);
			wsheet.addCell(new Label(1, 0, sheetTitle, wcfFC));
			wfont = new jxl.write.WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
			wcfFC = new WritableCellFormat(wfont);
			// 开始生成主体内容
			String[] rows = context.split("\r\n");
			for (int i = 0; i < rows.length; i++) {
				String rowString = rows[i];
				String[] rowAry = rowString.split(",");
				for (int j = 0; j < rowAry.length; j++) {
					wsheet.addCell(new Label(j, i, rowAry[j]));
				}
			}
			// 主体内容生成结束
			wbook.write(); // 写入文件
			wbook.close();
		} catch (Exception e) {
		}

	}

}
