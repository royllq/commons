package net.wicp.tams.commons.mybatis.dialect;

public class HSQLDialect extends Dialect
{
  public boolean supportsLimit()
  {
    return true;
  }

  public boolean supportsLimitOffset()
  {
    return true;
  }

  public String getLimitString(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    //int i = (paramInt1 > 0) ? 1 : 0;
    return new StringBuffer(paramString1.length() + 10).append(paramString1).insert(paramString1.toLowerCase().indexOf("select") + 6, " top " + paramString3).toString();
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.HSQLDialect
 * JD-Core Version:    0.5.4
 */