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

/***
 * 整型类型定义
 * 
 * @author zhoujunhui
 *
 */
public class TestInteger extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestString", dir,
					"TestInteger.xml");
			dynabean = conf.parserInputNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() {
		dynabean.set("integer", 1);
		Assert.assertEquals(dynabean.getStrValueByName("integer"), "1");
	}

	@Test
	public void testArray() {
		dynabean.set("ary", new Integer[] { 1, 2 });
		dynabean.set("ary", 1, 3);
		Assert.assertArrayEquals(new Integer[] { 1, 3 },
				(Integer[]) dynabean.get("ary"));
	}

	@Test
	public void testList() {
		List<Integer> inputlist = new ArrayList<>();
		inputlist.add(1);
		inputlist.add(2);
		inputlist.add(3);
		dynabean.set("list", inputlist);
		dynabean.set("list", 2, 4);
		Integer intobj = (Integer) dynabean.get("list", 2);
		Assert.assertTrue(4 == intobj);
	}

	@Test
	public void testMap() {
		Map<String, Integer> inputmap = new HashMap<String, Integer>();
		inputmap.put("key1", 1);
		inputmap.put("key2", 2);
		inputmap.put("key3", 3);
		dynabean.set("map", inputmap);
		dynabean.set("map", "key4", 4);
		Integer intobj = (Integer) dynabean.get("map", "key4");
		Assert.assertTrue(4 == intobj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tesLimit() {
		dynabean.set("limit", 31);
	}

}
