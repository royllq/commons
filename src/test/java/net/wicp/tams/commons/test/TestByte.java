package net.wicp.tams.commons.test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.constant.ColType;
import net.wicp.tams.commons.exception.ProjectException;
import net.wicp.tams.commons.test.beans.SexEnum;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/****
 * enum类型定义
 * 
 * @author zhoujunhui
 *
 */
public class TestByte extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestByte", dir,
					"TestByte.xml");
			dynabean = conf.parserInputNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() throws UnsupportedEncodingException {
		byte[] rjzjh = "周俊辉".getBytes("UTF-8");
		dynabean.set("single", rjzjh);
		Assert.assertEquals(
				new String((byte[]) dynabean.get("single"), "UTF-8"), "周俊辉");
	}

	@Test
	public void testDefault() {
		Assert.assertEquals(dynabean.getStrValueByName("single"), "rjzjh");
	}

	@Test
	public void testArray() {
		dynabean.set("ary",
				new byte[][] { "rjzjh1".getBytes(), "rjzjh2".getBytes() });
		dynabean.set("ary", 1, "rjzjh3".getBytes());
		Assert.assertArrayEquals(
				new byte[][] { "rjzjh1".getBytes(), "rjzjh3".getBytes() },
				(byte[][]) dynabean.get("ary"));
	}

	@Test
	public void testList() {
		List<byte[]> inputlist = new ArrayList<byte[]>();
		inputlist.add("rjzjh1".getBytes());
		inputlist.add("rjzjh2".getBytes());
		dynabean.set("list", inputlist);
		dynabean.set("list", 1, "rjzjh3".getBytes());
		byte[] str = (byte[]) dynabean.get("list", 1);
		Assert.assertEquals("rjzjh3", new String(str));
	}

	@Test
	public void testMap() {
		Map<String, byte[]> inputmap = new HashMap<String, byte[]>();
		inputmap.put("key1", "rjzjh1".getBytes());
		inputmap.put("key2", "rjzjh2".getBytes());
		dynabean.set("map", inputmap);
		dynabean.set("map", "key3", "rjzjh3".getBytes());
		byte[] str = (byte[]) dynabean.get("map", "key3");
		Assert.assertEquals("rjzjh3", new String(str));
	}

}
