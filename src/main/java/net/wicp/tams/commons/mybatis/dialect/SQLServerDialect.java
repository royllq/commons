package net.wicp.tams.commons.mybatis.dialect;

public class SQLServerDialect extends Dialect
{
  public boolean supportsLimitOffset()
  {
    return false;
  }

  public boolean supportsLimit()
  {
    return true;
  }

  static int getAfterSelectInsertPoint(String paramString)
  {
    int i = paramString.toLowerCase().indexOf("select");
    int j = paramString.toLowerCase().indexOf("select distinct");
    return i + ((j == i) ? 15 : 6);
  }

  public String getLimitString(String paramString, int paramInt1, int paramInt2)
  {
    return getLimitString(paramString, paramInt1, null, paramInt2, null);
  }

  public String getLimitString(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    if (paramInt1 > 0)
      throw new UnsupportedOperationException("sql server has no offset");
    return new StringBuffer(paramString1.length() + 8).append(paramString1).insert(getAfterSelectInsertPoint(paramString1), " top " + paramInt2).toString();
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.SQLServerDialect
 * JD-Core Version:    0.5.4
 */