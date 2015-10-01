package net.wicp.tams.commons.mybatis.dialect;

public class OracleDialect extends Dialect
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
    int i = 0;
    if (paramString1.toLowerCase().endsWith(" for update"))
    {
      paramString1 = paramString1.substring(0, paramString1.length() - 11);
      i = 1;
    }
    StringBuffer localStringBuffer = new StringBuffer(paramString1.length() + 100);
    if (paramInt1 > 0)
      localStringBuffer.append("select * from ( select row_.*, rownum rownum_ from ( ");
    else
      localStringBuffer.append("select * from ( ");
    localStringBuffer.append(paramString1);
    if (paramInt1 > 0)
    {
      String str = paramString2 + "+" + paramString3;
      localStringBuffer.append(" ) row_ ) where rownum_ <= " + str + " and rownum_ > " + paramString2);
    }
    else
    {
      localStringBuffer.append(" ) where rownum <= " + paramString3);
    }
    if (i != 0)
      localStringBuffer.append(" for update");
    return localStringBuffer.toString();
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.OracleDialect
 * JD-Core Version:    0.5.4
 */