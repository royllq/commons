package net.wicp.tams.commons.mybatis.dialect;

public class SQLServer2012Dialect extends Dialect
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
    paramString1 = paramString1.trim();
    StringBuffer localStringBuffer = new StringBuffer(paramString1.length() + 100);
    localStringBuffer.append(paramString1);
    localStringBuffer.append(" OFFSET ").append(paramInt1).append(" ROW FETCH NEXT ").append(paramInt2).append(" ROWS ONLY ");
    return localStringBuffer.toString();
  }

  static String getOrderByPart(String paramString)
  {
    String str = paramString.toLowerCase();
    int i = str.indexOf("order by");
    if (i != -1)
      return paramString.substring(i);
    return "";
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.SQLServer2012Dialect
 * JD-Core Version:    0.5.4
 */