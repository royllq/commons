package net.wicp.tams.commons.mybatis.dialect;

public class H2Dialect extends Dialect
{
  public boolean supportsLimit()
  {
    return true;
  }

  public String getLimitString(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    return new StringBuffer(paramString1.length() + 40).append(paramString1).append(" limit " + paramString3).toString();
  }

  public boolean supportsLimitOffset()
  {
    return true;
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.H2Dialect
 * JD-Core Version:    0.5.4
 */