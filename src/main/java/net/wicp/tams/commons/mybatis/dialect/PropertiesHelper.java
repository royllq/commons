package net.wicp.tams.commons.mybatis.dialect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesHelper
{
  public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;
  public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;
  public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;
  Properties p;
  private int systemPropertiesMode = 0;

  public PropertiesHelper(Properties paramProperties)
  {
    setProperties(paramProperties);
  }

  public PropertiesHelper(Properties paramProperties, int paramInt)
  {
    setProperties(paramProperties);
    if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2))
      throw new IllegalArgumentException("error systemPropertiesMode mode:" + paramInt);
    this.systemPropertiesMode = paramInt;
  }

  public Properties getProperties()
  {
    return this.p;
  }

  public void setProperties(Properties paramProperties)
  {
    if (paramProperties == null)
      throw new IllegalArgumentException("properties must be not null");
    this.p = paramProperties;
  }

  public String getRequiredString(String paramString)
  {
    String str = getProperty(paramString);
    if (isBlankString(str))
      throw new IllegalStateException("required property is blank by key=" + paramString);
    return str;
  }

  public String getNullIfBlank(String paramString)
  {
    String str = getProperty(paramString);
    if (isBlankString(str))
      return null;
    return str;
  }

  public String getNullIfEmpty(String paramString)
  {
    String str = getProperty(paramString);
    if ((str == null) || ("".equals(str)))
      return null;
    return str;
  }

  public String getAndTryFromSystem(String paramString)
  {
    String str = getProperty(paramString);
    if (isBlankString(str))
      str = getSystemProperty(paramString);
    return str;
  }

  private String getSystemProperty(String paramString)
  {
    String str = System.getProperty(paramString);
    if (isBlankString(str))
      str = System.getenv(paramString);
    return str;
  }

  public Integer getInteger(String paramString)
  {
    String str = getProperty(paramString);
    if (str == null)
      return null;
    return Integer.valueOf(Integer.parseInt(str));
  }

  public int getInt(String paramString, int paramInt)
  {
    if (getProperty(paramString) == null)
      return paramInt;
    return Integer.parseInt(getRequiredString(paramString));
  }

  public int getRequiredInt(String paramString)
  {
    return Integer.parseInt(getRequiredString(paramString));
  }

  public Long getLong(String paramString)
  {
    if (getProperty(paramString) == null)
      return null;
    return Long.valueOf(Long.parseLong(getRequiredString(paramString)));
  }

  public long getLong(String paramString, long paramLong)
  {
    if (getProperty(paramString) == null)
      return paramLong;
    return Long.parseLong(getRequiredString(paramString));
  }

  public Long getRequiredLong(String paramString)
  {
    return Long.valueOf(Long.parseLong(getRequiredString(paramString)));
  }

  public Boolean getBoolean(String paramString)
  {
    if (getProperty(paramString) == null)
      return null;
    return Boolean.valueOf(Boolean.parseBoolean(getRequiredString(paramString)));
  }

  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    if (getProperty(paramString) == null)
      return paramBoolean;
    return Boolean.parseBoolean(getRequiredString(paramString));
  }

  public boolean getRequiredBoolean(String paramString)
  {
    return Boolean.parseBoolean(getRequiredString(paramString));
  }

  public Float getFloat(String paramString)
  {
    if (getProperty(paramString) == null)
      return null;
    return Float.valueOf(Float.parseFloat(getRequiredString(paramString)));
  }

  public float getFloat(String paramString, float paramFloat)
  {
    if (getProperty(paramString) == null)
      return paramFloat;
    return Float.parseFloat(getRequiredString(paramString));
  }

  public Float getRequiredFloat(String paramString)
  {
    return Float.valueOf(Float.parseFloat(getRequiredString(paramString)));
  }

  public Double getDouble(String paramString)
  {
    if (getProperty(paramString) == null)
      return null;
    return Double.valueOf(Double.parseDouble(getRequiredString(paramString)));
  }

  public double getDouble(String paramString, double paramDouble)
  {
    if (getProperty(paramString) == null)
      return paramDouble;
    return Double.parseDouble(getRequiredString(paramString));
  }

  public Double getRequiredDouble(String paramString)
  {
    return Double.valueOf(Double.parseDouble(getRequiredString(paramString)));
  }

  public Object setProperty(String paramString, int paramInt)
  {
    return setProperty(paramString, String.valueOf(paramInt));
  }

  public Object setProperty(String paramString, long paramLong)
  {
    return setProperty(paramString, String.valueOf(paramLong));
  }

  public Object setProperty(String paramString, float paramFloat)
  {
    return setProperty(paramString, String.valueOf(paramFloat));
  }

  public Object setProperty(String paramString, double paramDouble)
  {
    return setProperty(paramString, String.valueOf(paramDouble));
  }

  public Object setProperty(String paramString, boolean paramBoolean)
  {
    return setProperty(paramString, String.valueOf(paramBoolean));
  }

  public String getProperty(String paramString1, String paramString2)
  {
    return this.p.getProperty(paramString1, paramString2);
  }

  public String getProperty(String paramString)
  {
    String str = null;
    if (this.systemPropertiesMode == 2)
      str = getSystemProperty(paramString);
    if (str == null)
      str = this.p.getProperty(paramString);
    if ((str == null) && (this.systemPropertiesMode == 1))
      str = getSystemProperty(paramString);
    return str;
  }

  public Object setProperty(String paramString1, String paramString2)
  {
    return this.p.setProperty(paramString1, paramString2);
  }

  public void clear()
  {
    this.p.clear();
  }

  public Set<Map.Entry<Object, Object>> entrySet()
  {
    return this.p.entrySet();
  }

  public Enumeration<?> propertyNames()
  {
    return this.p.propertyNames();
  }

  public boolean contains(Object paramObject)
  {
    return this.p.contains(paramObject);
  }

  public boolean containsKey(Object paramObject)
  {
    return this.p.containsKey(paramObject);
  }

  public boolean containsValue(Object paramObject)
  {
    return this.p.containsValue(paramObject);
  }

  public Enumeration<Object> elements()
  {
    return this.p.elements();
  }

  public Object get(Object paramObject)
  {
    return this.p.get(paramObject);
  }

  public boolean isEmpty()
  {
    return this.p.isEmpty();
  }

  public Enumeration<Object> keys()
  {
    return this.p.keys();
  }

  public Set<Object> keySet()
  {
    return this.p.keySet();
  }

  public void list(PrintStream paramPrintStream)
  {
    this.p.list(paramPrintStream);
  }

  public void list(PrintWriter paramPrintWriter)
  {
    this.p.list(paramPrintWriter);
  }

  public void load(InputStream paramInputStream)
    throws IOException
  {
    this.p.load(paramInputStream);
  }

  public void loadFromXML(InputStream paramInputStream)
    throws IOException, InvalidPropertiesFormatException
  {
    this.p.loadFromXML(paramInputStream);
  }

  public Object put(Object paramObject1, Object paramObject2)
  {
    return this.p.put(paramObject1, paramObject2);
  }

  public void putAll(Map<? extends Object, ? extends Object> paramMap)
  {
    this.p.putAll(paramMap);
  }

  public Object remove(Object paramObject)
  {
    return this.p.remove(paramObject);
  }

  /** @deprecated */
  public void save(OutputStream paramOutputStream, String paramString)
  {
    this.p.save(paramOutputStream, paramString);
  }

  public int size()
  {
    return this.p.size();
  }

  public void store(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    this.p.store(paramOutputStream, paramString);
  }

  public void storeToXML(OutputStream paramOutputStream, String paramString1, String paramString2)
    throws IOException
  {
    this.p.storeToXML(paramOutputStream, paramString1, paramString2);
  }

  public void storeToXML(OutputStream paramOutputStream, String paramString)
    throws IOException
  {
    this.p.storeToXML(paramOutputStream, paramString);
  }

  public Collection<Object> values()
  {
    return this.p.values();
  }

  public String toString()
  {
    return this.p.toString();
  }

  private static boolean isBlankString(String paramString)
  {
    return (paramString == null) || ("".equals(paramString.trim()));
  }
}

/* Location:           D:\workspace_all\bershka\WebRoot\WEB-INF\lib\vxi-core-2.3.2.jar
 * Qualified Name:     com.vxichina.core.jdbc.dialect.PropertiesHelper
 * JD-Core Version:    0.5.4
 */