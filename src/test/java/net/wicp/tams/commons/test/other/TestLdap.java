package net.wicp.tams.commons.test.other;

import javax.naming.directory.Attributes;

import org.junit.Test;

import net.wicp.tams.commons.apiext.LdapObj;

public class TestLdap {
	@Test
	public void addOUSingle() {
		LdapObj.getInstance().addOUSingle("ou=dddd,ou=aaaaaa");
	}

	@Test
	public void addUser() {
		// LdapObj.getInstance().addUser("zjh", "", null);
		LdapObj.getInstance().addUser("zjh1", "ou=dddd,ou=aaaaaa", null);
	}

	@Test
	public void getUserDN() {
		String name = LdapObj.getInstance().getUserDN("zjh1");
		System.out.println(name);
	}

	@Test
	public void getOu() {
		Attributes attr = LdapObj.getInstance().getOu("ou=dddd,ou=aaaaaa");
		System.out.println(attr);
	}
}
