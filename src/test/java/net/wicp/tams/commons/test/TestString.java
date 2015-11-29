package net.wicp.tams.commons.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.exception.ProjectException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/****
 * String类型定义
 * 
 * @author zhoujunhui
 *
 */
public class TestString extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestString", dir,
					"TestString.xml");
			//dynabean = conf.parserInputNoCI().newInstance();
			dynabean = conf.parserOutNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() {
		dynabean.set("format", "1988-01-01");
		Assert.assertEquals(dynabean.getStrValueByName("format"), "1988-01-01");

	}

	@Test
	public void testArray() {
		dynabean.set("ary", new String[] { "ary1", "ary2" });
		dynabean.set("ary", 1, "ary3");
		Assert.assertArrayEquals(new String[] { "ary1", "ary3" },
				(String[]) dynabean.get("ary"));
	}

	@Test
	public void testList() {
		List<String> inputlist = new ArrayList<>();
		inputlist.add("list1");
		inputlist.add("list2");
		inputlist.add("list3");
		dynabean.set("list", inputlist);
		dynabean.set("list", 2, "list4");
		String str = (String) dynabean.get("list", 2);
		Assert.assertEquals("list4", str);
	}

	@Test
	public void testMap() {
		Map<String, String> inputmap = new HashMap<String, String>();
		inputmap.put("key1", "value1");
		inputmap.put("key2", "value2");
		inputmap.put("key3", "value3");
		dynabean.set("map", inputmap);
		dynabean.set("map", "key4", "value4");
		// dynabean.set("map", "value5");
		String str = (String) dynabean.get("map", "key4");
		Assert.assertEquals("value4", str);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNotNull() {
		dynabean.set("isnotnull", null);
	}

	@Test
	public void testDefaultValue() {
		String defaultValue = (String) dynabean.get("defaultValue");
		Assert.assertEquals("平安健康", defaultValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLength() {
		dynabean.set("len", "123456");
	}

}
