package net.wicp.tams.commons.mybatis.dialect;

public class MySQLDialect extends Dialect
{
  public boolean supportsLimitOffset()
  {
    return true;
  }

  public boolean supportsLimit()
  {
    return true;
  }

  public String getLimitString(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    if (paramInt1 > 0)
      return paramString1 + " limit " + paramString2 + "," + paramString3;
    return paramString1 + " limit " + paramString3;
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.MySQLDialect
 * JD-Core Version:    0.5.4
 */