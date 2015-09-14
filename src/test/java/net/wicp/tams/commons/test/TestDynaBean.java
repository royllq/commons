package net.wicp.tams.commons.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.exception.ProjectException;
import net.wicp.tams.commons.test.beans.AddrInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestDynaBean extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean = null;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestDynaBean", dir,
					"TestDynaBean.xml");
			dynabean = conf.parserInputNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() {
		CusDynaBean curbean = dynabean.newCusDynaBean("single");
		curbean.set("addr", "娄山关路1号");
		curbean.set("post", "244100");
		dynabean.set("single", curbean);
		String addr = ((CusDynaBean) dynabean.get("single"))
				.getStrValueByName("addr");
		Assert.assertEquals(addr, "娄山关路1号");
	}

	@Test
	public void testToJson() {
		CusDynaBean curbean = dynabean.newCusDynaBean("single");
		curbean.set("addr", "娄山关路1号");
		curbean.set("post", "244100");
		JSONObject jsonobj = curbean.getJsonObj();
		Assert.assertEquals(jsonobj.getString("addr"), "娄山关路1号");
	}

	@Test
	public void testSetValueByJson() {
		CusDynaBean curbean = dynabean.newCusDynaBean("single");
		curbean.setByJson(new JSONObject("addr", "娄山关路1号", "post", "244100"));
		dynabean.set("single", curbean);
		String addr = ((CusDynaBean) dynabean.get("single"))
				.getStrValueByName("addr");
		Assert.assertEquals(addr, "娄山关路1号");
	}

	@Test
	public void testDefaultValue() {
		CusDynaBean curbean = dynabean.newCusDynaBean("single");
		dynabean.set("single", curbean);
		String addr = ((CusDynaBean) dynabean.get("single"))
				.getStrValueByName("addr");
		Assert.assertEquals(addr, "娄山关路2号");
		CusDynaBean newbean = dynabean.newCusDynaBean("single", false);
		Assert.assertTrue(StringUtils.isBlank(newbean.getStrValueByName("addr")));
	}

	@Test
	public void testArray() {
		CusDynaBean defaultBean = dynabean.newCusDynaBean("array");
		CusDynaBean curbean = dynabean.newCusDynaBean("array", false);
		curbean.set("addr", "娄山关路1号");
		curbean.set("post", "244100");

		dynabean.set("array", new CusDynaBean[] { curbean, defaultBean });
		// dynabean.set("array", 1, new AddrInfo("娄山关路3号", "244100"));

		Assert.assertEquals("娄山关路9号",
				((CusDynaBean[]) dynabean.get("array"))[1].get("addr"));
	}

	@Test
	public void testList() {
		List<CusDynaBean> inputlist = new ArrayList<>();
		CusDynaBean cusbean1 = dynabean.newCusDynaBean("list");
		cusbean1.set("addr", "娄山关路1号");
		cusbean1.set("post", "244100");
		CusDynaBean cusbean2 = dynabean.newCusDynaBean("list");
		cusbean2.set("addr", "娄山关路2号");
		cusbean2.set("post", "244100");
		CusDynaBean cusbean3 = dynabean.newCusDynaBean("list");
		cusbean3.set("addr", "娄山关路3号");
		cusbean3.set("post", "244100");
		inputlist.add(cusbean1);
		inputlist.add(cusbean2);
		inputlist.add(cusbean3);
		dynabean.set("list", inputlist);
		CusDynaBean cusbean4 = dynabean.newCusDynaBean("list");
		cusbean4.set("addr", "娄山关路4号");
		cusbean4.set("post", "244100");
		dynabean.set("list", 2, cusbean4);
		CusDynaBean intobj = (CusDynaBean) dynabean.get("list", 2);
		Assert.assertEquals("娄山关路4号", intobj.get("addr"));
	}

	@Test
	public void testMap() {
		CusDynaBean cusbean1 = dynabean.newCusDynaBean("map");
		cusbean1.set("addr", "娄山关路1号");
		cusbean1.set("post", "244100");
		CusDynaBean cusbean2 = dynabean.newCusDynaBean("map");
		cusbean2.set("addr", "娄山关路2号");
		cusbean2.set("post", "244100");
		CusDynaBean cusbean3 = dynabean.newCusDynaBean("map");
		cusbean3.set("addr", "娄山关路3号");
		cusbean3.set("post", "244100");

		Map<String, CusDynaBean> inputmap = new HashMap<String, CusDynaBean>();
		inputmap.put("key1", cusbean1);
		inputmap.put("key2", cusbean2);
		inputmap.put("key3", cusbean3);
		dynabean.set("map", inputmap);
		CusDynaBean cusbean4 = dynabean.newCusDynaBean("map");
		cusbean4.set("addr", "娄山关路4号");
		cusbean4.set("post", "244100");
		dynabean.set("map", "key4", cusbean4);
		CusDynaBean intobj = (CusDynaBean) dynabean.get("map", "key4");
		Assert.assertEquals("娄山关路4号", intobj.get("addr"));
	}

}
