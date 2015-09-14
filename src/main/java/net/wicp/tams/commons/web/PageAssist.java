package net.wicp.tams.commons.web;

import java.util.List;

import net.wicp.tams.commons.apiext.StringUtil;

public class PageAssist {
	private int pageSize;
	private int pageNo;
	private long allNum;
	private List<?> result;

	public PageAssist(int pageSize, int pageNo, int allNum) {
		super();
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.allNum = allNum;
	}

	public PageAssist(int pageSize, int pageNo) {
		super();
		this.pageSize = pageSize;
		this.pageNo = pageNo;
		this.allNum = -1;
	}

	public int getPageSize() {
		return pageSize;
	}

	public List<?> getResult() {
		return result;
	}

	public void setResult(List<?> result) {
		this.result = result;
	}

	/***
	 * 通过JPQL　得到要count()的字段
	 * 
	 * @param queryStr
	 * @return
	 */
	public static String getCountCol(String queryStr) {
		int firstIndex = queryStr.toLowerCase().indexOf(" from");
		int firstDoc = queryStr.indexOf(".");
		String countStr = "";
		if (firstDoc < firstIndex) {
			int pre = firstDoc;
			boolean hasChart = true;
			do {
				pre--;
				if ((queryStr.charAt(pre) >= 'a' && queryStr.charAt(pre) <= 'z')
						|| (queryStr.charAt(pre) >= 'A' && queryStr.charAt(pre) <= 'Z')
						&& hasChart) {
					hasChart = false;
				}
			} while (pre >= 0
					&& ((queryStr.charAt(pre) >= 'a' && queryStr.charAt(pre) <= 'z') || (queryStr
							.charAt(pre) >= 'A' && queryStr.charAt(pre) <= 'Z'))
					|| hasChart);

			int pos = firstDoc;
			hasChart = true;
			do {
				pos++;
				if ((queryStr.charAt(pos) >= 'a' && queryStr.charAt(pos) <= 'z')
						|| (queryStr.charAt(pos) >= 'A' && queryStr.charAt(pos) <= 'Z')
						&& hasChart) {
					hasChart = false;
				}
			} while (pos < firstIndex
					&& ((queryStr.charAt(pos) >= 'a' && queryStr.charAt(pos) <= 'z') || (queryStr
							.charAt(pos) >= 'A' && queryStr.charAt(pos) <= 'Z'))
					|| hasChart);
			countStr = queryStr.substring(pre + 1, pos);
		} else {
			countStr = queryStr.substring(0, firstIndex);
			countStr = StringUtil.trimSpace(countStr.replace("select", ""));
		}

		countStr = countStr.replace(" ", "");
		return StringUtil.hasNull(countStr);
	}

	public int getPageNo() {
		return pageNo;
	}

	public long getAllNum() {
		return allNum;
	}

	public void setAllNum(long allNum) {
		this.allNum = allNum;
	}
}
