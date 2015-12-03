package net.wicp.tams.commons.test.other;

import java.util.ArrayList;
import java.util.List;

import net.wicp.tams.commons.test.beans.UserInfo;

/***
 * 构建一棵树
 * 
 * @author andy.zhou
 *
 */
public class TestTree {
	private static List<UserInfo> userlist = new ArrayList<>();

	static {
		UserInfo ui1 = new UserInfo("r1", 1);
		userlist.add(ui1);
		UserInfo ui2 = new UserInfo("r2", 2);
		userlist.add(ui2);
		UserInfo ui3 = new UserInfo("r3", 3);
		userlist.add(ui3);
		UserInfo ui4 = new UserInfo("r4", 4);
		userlist.add(ui4);
	}
}
