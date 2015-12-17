package net.wicp.tams.commons.report.pdf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.util.PDFTextStripper;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.StringUtil;
import net.wicp.tams.commons.constant.FilterPattern;
import net.wicp.tams.commons.constant.PathType;

/****
 * pdf文件解析类
 * 
 * @author andy.zhou
 *
 */
public class PdfReader {
	protected final static String tempdir = PathType.getPath(Conf.get("pdf.dir.temp"));
	private final Map<String, FilterPattern> filterMap = new HashMap<>();
	protected PDFTextStripper textStripper;
	private final String spiltFormat = "\n>%s\n";

	private final String fileName;
	private String pageStart;// 页面开始标签
	private String pageEnd;// 页面结束标签
	private String articleStart;// 文章开始标签
	private String articleEnd;// 文章结束标签
	private String lineSeparator = " ";// 行分隔符
	private boolean filternull = true;// 是否要去除空值

	public PdfReader(String fileName) {
		this.fileName = fileName;
	}

	public void putFilter(String key, FilterPattern filterPattern) {
		filterMap.put(key, filterPattern);
	}

	/***
	 * 默认为包括就删除
	 * 
	 * @param key
	 */
	protected void putFilter(String key) {
		filterMap.put(key, FilterPattern.contains);
	}

	public void setTextStripper(PDFTextStripper textStripper) {
		this.textStripper = textStripper;
	}

	public List<String> parse(Integer beginpage, Integer endpage) {
		List<String> retlist = new ArrayList<>();
		FileInputStream fis = null;
		try {
			fis = StringUtil.isNull(fileName) ? null
					: new FileInputStream(IOUtil.mergeFolderAndFilePath(tempdir, fileName));
			PDFParser p = new PDFParser(fis);
			p.parse();
			PDFTextStripper ts = this.textStripper == null ? new PDFTextStripper() : this.textStripper;
			if (beginpage != null && endpage != null && beginpage.intValue() > endpage.intValue()) {
				throw new IllegalArgumentException("参数错误，起始页小于结束页");
			}
			if (beginpage != null) {
				ts.setStartPage(beginpage.intValue());
			}
			if (endpage != null) {
				ts.setEndPage(endpage.intValue());
			}
			if (StringUtil.isNotNull(pageStart)) {
				ts.setPageStart(String.format(spiltFormat, pageStart));
			}
			if (StringUtil.isNotNull(pageEnd)) {
				ts.setPageStart(String.format(spiltFormat, pageEnd));
			}
			if (StringUtil.isNotNull(articleStart)) {
				ts.setPageStart(String.format(spiltFormat, articleStart));
			}
			if (StringUtil.isNotNull(articleEnd)) {
				ts.setPageStart(String.format(spiltFormat, articleEnd));
			}
			ts.setLineSeparator(lineSeparator);
			ts.setParagraphStart("\n");
			// ts.setParagraphEnd("\n>PAR_END");
			// ts.setParagraphEnd("\n");
			ts.setSortByPosition(true);
			String[] ret = ts.getText(p.getPDDocument()).split("\n");
			retlist = Arrays.asList(ret);
			CollectionUtils.filter(retlist, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					String context = (String) object;
					if (filternull && StringUtil.isNull(context)) {// 是否要去除空值
						return false;
					}
					boolean has = false;
					for (String filterkey : filterMap.keySet()) {
						if (filterMap.get(filterkey).check(context, filterkey)) {
							has = true;
							break;
						}
					}
					return !has;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retlist;
	}

	public String parseStr(Integer beginpage, Integer endpage) {
		List<String> retlist = parse(beginpage, endpage);
		StringBuffer buffer = new StringBuffer();
		for (String line : retlist) {
			buffer.append(line);
			buffer.append("\n");
		}
		return buffer.toString();
	}

	public String getPageStart() {
		return pageStart;
	}

	public void setPageStart(String pageStart) {
		this.pageStart = pageStart;
	}

	public String getPageEnd() {
		return pageEnd;
	}

	public void setPageEnd(String pageEnd) {
		this.pageEnd = pageEnd;
	}

	public String getArticleStart() {
		return articleStart;
	}

	public void setArticleStart(String articleStart) {
		this.articleStart = articleStart;
	}

	public String getArticleEnd() {
		return articleEnd;
	}

	public void setArticleEnd(String articleEnd) {
		this.articleEnd = articleEnd;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public boolean isFilternull() {
		return filternull;
	}

	public void setFilternull(boolean filternull) {
		this.filternull = filternull;
	}
}
