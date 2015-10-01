package net.wicp.tams.commons.mybatis.dialect;

public class DB2Dialect extends Dialect
{
  public boolean supportsLimit()
  {
    return true;
  }

  public boolean supportsLimitOffset()
  {
    return true;
  }

  private static String getRowNumber(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(50).append("rownumber() over(");
    int i = paramString.toLowerCase().indexOf("order by");
    if ((i > 0) && (!hasDistinct(paramString)))
      localStringBuffer.append(paramString.substring(i));
    localStringBuffer.append(") as rownumber_,");
    return localStringBuffer.toString();
  }

  private static boolean hasDistinct(String paramString)
  {
    return paramString.toLowerCase().indexOf("select distinct") >= 0;
  }

  public String getLimitString(String paramString1, int paramInt1, String paramString2, int paramInt2, String paramString3)
  {
    int i = paramString1.toLowerCase().indexOf("select");
    StringBuffer localStringBuffer = new StringBuffer(paramString1.length() + 100).append(paramString1.substring(0, i)).append("select * from ( select ").append(getRowNumber(paramString1));
    if (hasDistinct(paramString1))
      localStringBuffer.append(" row_.* from ( ").append(paramString1.substring(i)).append(" ) as row_");
    else
      localStringBuffer.append(paramString1.substring(i + 6));
    localStringBuffer.append(" ) as temp_ where rownumber_ ");
    if (paramInt1 > 0)
    {
      String str = paramString2 + "+" + paramString3;
      localStringBuffer.append("between " + paramString2 + "+1 and " + str);
    }
    else
    {
      localStringBuffer.append("<= " + paramString3);
    }
    return localStringBuffer.toString();
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.DB2Dialect
 * JD-Core Version:    0.5.4
 */