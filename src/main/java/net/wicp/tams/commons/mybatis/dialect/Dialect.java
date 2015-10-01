package net.wicp.tams.commons.mybatis.dialect;

public class Dialect {
	public boolean supportsLimit() {
		return false;
	}

	public boolean supportsLimitOffset() {
		return supportsLimit();
	}

	public String getLimitString(String paramString, int paramInt1,
			int paramInt2) {
		return getLimitString(paramString, paramInt1,
				Integer.toString(paramInt1), paramInt2,
				Integer.toString(paramInt2));
	}

	public String getLimitString(String paramString1, int paramInt1,
			String paramString2, int paramInt2, String paramString3) {
		throw new UnsupportedOperationException("paged queries not supported");
	}
}
