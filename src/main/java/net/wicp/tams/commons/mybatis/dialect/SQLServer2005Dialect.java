package net.wicp.tams.commons.mybatis.dialect;

public class SQLServer2005Dialect extends Dialect
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
    StringBuffer localStringBuffer1 = new StringBuffer();
    String str1 = getOrderByPart(paramString1);
    String str2 = "";
    String str3 = paramString1.toLowerCase();
    String str4 = paramString1;
    if (str3.trim().startsWith("select"))
    {
      int i = 6;
      if (str3.startsWith("select distinct"))
      {
        str2 = "DISTINCT ";
        i = 15;
      }
      str4 = str4.substring(i);
    }
    localStringBuffer1.append(str4);
    if ((str1 == null) || (str1.length() == 0))
      str1 = "ORDER BY CURRENT_TIMESTAMP";
    StringBuffer localStringBuffer2 = new StringBuffer();
    localStringBuffer2.append("WITH query AS (SELECT ").append(str2).append("TOP 100 PERCENT ").append(" ROW_NUMBER() OVER (").append(str1).append(") as __row_number__, ").append(localStringBuffer1).append(") SELECT * FROM query WHERE __row_number__ BETWEEN ").append(paramInt1 + 1).append(" AND ").append(paramInt1 + paramInt2).append(" ORDER BY __row_number__");
    return localStringBuffer2.toString();
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
 * Qualified Name:     com.vxichina.core.jdbc.dialect.SQLServer2005Dialect
 * JD-Core Version:    0.5.4
 */