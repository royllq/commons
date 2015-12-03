package net.wicp.tams.commons.test.beans;

import java.util.Date;

public class UserInfo {
	private String userName;
	private Integer age;
	private Date birth;
	private AddrInfo addr;

	public UserInfo(String userName, Integer age, Date birth) {
		this.userName = userName;
		this.age = age;
		this.birth = birth;
	}

	public UserInfo(String userName, Integer age) {
		this.userName = userName;
		this.age = age;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public AddrInfo getAddr() {
		return addr;
	}

	public void setAddr(AddrInfo addr) {
		this.addr = addr;
	}
}
