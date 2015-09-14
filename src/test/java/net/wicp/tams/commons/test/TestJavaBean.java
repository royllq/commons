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

import org.apache.tapestry5.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJavaBean extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean = null;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestDynaBean", dir,
					"TestJavaBean.xml");
			dynabean = conf.parserInputNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() {
		AddrInfo addbean = new AddrInfo("娄山关路1号", "244100");
		dynabean.set("single", addbean);
		String addr = ((AddrInfo) dynabean.get("single")).getAddr();
		Assert.assertEquals(addr, "娄山关路1号");
	}

	@Test
	public void testToJson() {
		AddrInfo addbean = new AddrInfo("娄山关路1号", "244100");
		dynabean.set("single", addbean);
		JSONObject jsonobj = dynabean.getJsonObj();
		Assert.assertEquals(jsonobj.getJSONObject("single").getString("addr"),
				"娄山关路1号");
	}

	@Test
	public void testSetValueByJson() {
		JSONObject json = new JSONObject("addr", "娄山关路1号", "post", "244100");
		dynabean.setByJson(new JSONObject("single", json));
		String addr = ((AddrInfo) dynabean.get("single")).getAddr();
		Assert.assertEquals(addr, "娄山关路1号");
	}

	@Test
	public void testDefaultValue() {
		AddrInfo curbean = (AddrInfo) dynabean.newDefaultObj("single");
		Assert.assertEquals(curbean.getAddr(), "娄山关路3号");
	}

	@Test
	public void testArray() {
		dynabean.set("array", new AddrInfo[] {
				new AddrInfo("娄山关路1号", "244100"),
				new AddrInfo("娄山关路2号", "244100") });
		dynabean.set("array", 1, new AddrInfo("娄山关路3号", "244100"));

		Assert.assertEquals("娄山关路3号",
				((AddrInfo[]) dynabean.get("array"))[1].getAddr());
	}

	@Test
	public void testList() {
		List<AddrInfo> inputlist = new ArrayList<>();
		inputlist.add(new AddrInfo("娄山关路1号", "244100"));
		inputlist.add(new AddrInfo("娄山关路2号", "244100"));
		inputlist.add(new AddrInfo("娄山关路3号", "244100"));
		dynabean.set("list", inputlist);
		dynabean.set("list", 2, new AddrInfo("娄山关路4号", "244100"));
		AddrInfo intobj = (AddrInfo) dynabean.get("list", 2);
		Assert.assertEquals("娄山关路4号", intobj.getAddr());
	}

	@Test
	public void testMap() {
		Map<String, AddrInfo> inputmap = new HashMap<String, AddrInfo>();
		inputmap.put("key1", new AddrInfo("娄山关路1号", "244100"));
		inputmap.put("key2", new AddrInfo("娄山关路2号", "244100"));
		inputmap.put("key3", new AddrInfo("娄山关路3号", "244100"));
		dynabean.set("map", inputmap);
		dynabean.set("map", "key4", new AddrInfo("娄山关路4号", "244100"));
		AddrInfo intobj = (AddrInfo) dynabean.get("map", "key4");
		Assert.assertEquals("娄山关路4号", intobj.getAddr());
	}

}
