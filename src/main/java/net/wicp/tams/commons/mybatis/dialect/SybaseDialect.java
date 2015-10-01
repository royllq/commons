package net.wicp.tams.commons.mybatis.dialect;

public class SybaseDialect extends Dialect
{
  public boolean supportsLimit()
  {
    return false;
  }

  public boolean supportsLimitOffset()
  {
    return false;
  }

  public String getLimitString(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    throw new UnsupportedOperationException("paged queries not supported");
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.SybaseDialect
 * JD-Core Version:    0.5.4
 */