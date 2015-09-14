package net.wicp.tams.commons.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.connector.beans.CusDynaBean;
import net.wicp.tams.commons.connector.config.AbstractConfigClass;
import net.wicp.tams.commons.connector.config.xmlParser.ConfigClassXml;
import net.wicp.tams.commons.constant.DateFormatCase;
import net.wicp.tams.commons.exception.ProjectException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TestDatetime extends AbsToDynaBean {
	private static AbstractConfigClass conf = null;
	private static CusDynaBean dynabean;

	@BeforeClass
	public static void initCalss() {
		try {
			conf = ConfigClassXml.createConfigClassXml("TestDate", dir,
					"TestDate.xml");
			dynabean = conf.parserInputNoCI().newInstance();
		} catch (ProjectException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSingle() {
		Date curtime = new Date();
		dynabean.set("datetime", curtime);
		Assert.assertSame(dynabean.get("datetime"), curtime);

	}

	@Test
	public void testArray() throws ParseException {
		SimpleDateFormat format = DateFormatCase.YYYY_MM_DD.getInstanc();

		dynabean.set(
				"ary",
				new Date[] { format.parse("2005-03-01"),
						format.parse("2005-03-02") });
		dynabean.set("ary", 1, format.parse("2005-03-03"));
		Assert.assertArrayEquals(new Date[] { format.parse("2005-03-01"),
				format.parse("2005-03-03") }, (Date[]) dynabean.get("ary"));
	}

	@Test
	public void testList() throws ParseException {
		SimpleDateFormat format = DateFormatCase.YYYY_MM_DD.getInstanc();
		List<Date> inputlist = new ArrayList<>();
		inputlist.add(format.parse("2005-03-01"));
		inputlist.add(format.parse("2005-03-02"));
		inputlist.add(format.parse("2005-03-03"));
		dynabean.set("list", inputlist);
		dynabean.set("list", 2, format.parse("2005-03-04"));
		Date intobj = (Date) dynabean.get("list", 2);
		Assert.assertTrue(intobj.getTime() == format.parse("2005-03-04")
				.getTime());
	}

	@Test
	public void testMap() throws ParseException {
		SimpleDateFormat format = DateFormatCase.YYYY_MM_DD.getInstanc();
		Map<String, Date> inputmap = new HashMap<String, Date>();
		inputmap.put("key1", format.parse("2005-03-01"));
		inputmap.put("key2", format.parse("2005-03-02"));
		inputmap.put("key3", format.parse("2005-03-03"));
		dynabean.set("map", inputmap);
		dynabean.set("map", "key4", format.parse("2005-03-04"));
		Date intobj = (Date) dynabean.get("map", "key4");
		Assert.assertTrue(intobj.getTime() == format.parse("2005-03-04")
				.getTime());
	}

	@Test(expected = IllegalArgumentException.class)
	public void tesLimit() {
		dynabean.set("limit", new Date(2013, 1, 1));
	}

	@Test
	public void tesFormatAndDefault() throws ParseException  {
		SimpleDateFormat format = DateFormatCase.YYYY_MM_DD.getInstanc();
		Assert.assertEquals("2013-01-01 10:10:10",
				dynabean.getStrValueByName("format"));
		dynabean.set("format", format.parse("2013-01-01"));
	}

}
