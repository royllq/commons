package net.wicp.tams.commons.test.beans;

public class AddrInfo {
	private String addr;
	private String post;

	public AddrInfo(String addr, String post) {
		super();
		this.addr = addr;
		this.post = post;
	}

	public AddrInfo() {

	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}
}
