package net.wicp.tams.commons.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.exception.ProjectException;
import net.wicp.tams.commons.test.beans.SexEnum;

/****
 * enum类型定义
 * 
 * @author zhoujunhui
 *
 */
public class TestEnum extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestEnum", dir,
					"TestEnum.xml");
			dynabean = conf.parserInputNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() {
		dynabean.set("single", SexEnum.M);
		Assert.assertSame(dynabean.getStrValueByName("single"), "M");

	}

	@Test
	public void testDefault() {
		Assert.assertSame(dynabean.getStrValueByName("single"), "F");

	}

	@Test
	public void testArray() {
		dynabean.set("ary", new SexEnum[] { SexEnum.F, SexEnum.M });
		dynabean.set("ary", 1, SexEnum.U);
		Assert.assertArrayEquals(new SexEnum[] { SexEnum.F, SexEnum.U },
				(SexEnum[]) dynabean.get("ary"));
	}

	@Test
	public void testList() {
		List<SexEnum> inputlist = new ArrayList<>();
		inputlist.add(SexEnum.F);
		inputlist.add(SexEnum.M);
		dynabean.set("list", inputlist);
		dynabean.set("list", 1, SexEnum.U);
		SexEnum str = (SexEnum) dynabean.get("list", 1);
		Assert.assertEquals(SexEnum.U, str);
	}

	@Test
	public void testMap() {
		Map<String, SexEnum> inputmap = new HashMap<String, SexEnum>();
		inputmap.put("key1", SexEnum.F);
		inputmap.put("key2", SexEnum.M);
		dynabean.set("map", inputmap);
		dynabean.set("map", "key3", SexEnum.U);
		SexEnum str = (SexEnum) dynabean.get("map", "key3");
		Assert.assertEquals(SexEnum.U, str);
	}

}
