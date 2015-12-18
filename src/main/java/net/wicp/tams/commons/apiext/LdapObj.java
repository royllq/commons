package net.wicp.tams.commons.apiext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

import net.wicp.tams.commons.Conf;
import net.wicp.tams.commons.LogHelp;
import net.wicp.tams.commons.Result;

public class LdapObj {
	private static final Logger logger = LogHelp.getLogger(LdapObj.class);
	private static Object lockobj = new Object();
	private LdapContext ctx = null;
	private static final Control[] connCtls = null;

	private static volatile LdapObj INSTANCE;

	private LdapObj(LdapContext ctx) {
		this.ctx = ctx;
	}

	/***
	 * 得到单例
	 * 
	 * @return
	 */
	public static final LdapObj getInstance() {
		if (INSTANCE == null) {
			synchronized (lockobj) {
				if (INSTANCE == null) {
					Hashtable<String, String> env = new Hashtable<String, String>();
					env.put(Context.INITIAL_CONTEXT_FACTORY, Conf.get("ldap.server.factory"));
					env.put(Context.PROVIDER_URL, Conf.get("ldap.server.url") + Conf.get("ldap.server.basedn"));
					env.put(Context.SECURITY_AUTHENTICATION, "simple");
					env.put(Context.SECURITY_PRINCIPAL, Conf.get("ldap.server.admin"));
					env.put(Context.SECURITY_CREDENTIALS, Conf.get("ldap.server.password"));
					env.put("com.sun.jndi.ldap.connect.timeout", "3000");
					try {
						LdapContext ctx = new InitialLdapContext(env, connCtls);
						INSTANCE = new LdapObj(ctx);
						logger.info("connected.");
					} catch (NamingException e) {
						logger.error("LDAP初始化连接失败：LDAP网络异常或用户名密码错误", e);
						throw new RuntimeException("LDAP初始化连接失败：LDAP网络异常或用户名密码错误");
					} catch (Exception e) {
						logger.error("connect()方法意外异常", e);
						throw new RuntimeException("LDAP 未知异常");
					}
				}
			}
		}
		return INSTANCE;
	}

	/****
	 * 得到OU
	 * 
	 * @param ou
	 *            要查询的ou
	 * @return 找到的ou如果异常或没有返回为null
	 */
	public Attributes getOu(String ou) {
		try {
			Attributes attrs = ctx.getAttributes(ou);
			return attrs;
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * 添加单个OU
	 * 
	 * @param ou
	 *            要添加的OU，如：ou=dddd,ou=aaaaaa
	 * @return
	 */
	public Result addOUSingle(String ou) {
		if (StringUtil.isNull(ou) || getOu(ou) != null) {
			return Result.getError("参数不能为空或已存在此OU");
		}
		Attributes attrs = new BasicAttributes();
		Attribute objclass = new BasicAttribute("objectclass");
		// 设置OU属性
		objclass.add("top");
		objclass.add("organizationalunit");
		attrs.put(objclass);
		try {
			ctx.bind(ou, null, attrs);
			return Result.getSuc();
		} catch (NamingException e) {
			logger.error("添加OU失败", e);
			return Result.getError(e.getMessage());
		}
	}

	/***
	 * 通过uid得到用户路径
	 * 
	 * @param uid
	 *            要查询的uid
	 * @return 返回的用户路径
	 */
	public String getUserDN(String uid) {
		if (StringUtil.isNull(uid)) {
			return null;
		}
		List<SearchResult> serchlist = getSearchResult("", String.format("uid=%s", uid));
		if (CollectionUtils.isEmpty(serchlist)) {
			return null;
		}
		return serchlist.get(0).getName();
	}

	private List<SearchResult> getSearchResult(String searchBase, String filter) {
		SearchResult si = null;
		List<SearchResult> list = new ArrayList<SearchResult>();
		try {
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> en = ctx.search(searchBase, filter, searchCtls);
			while (en != null && en.hasMoreElements()) {
				Object obj = en.nextElement();
				if (obj instanceof SearchResult) {
					si = (SearchResult) obj;
					if (!"".equals(si.getName())) {// 排除根节点
						list.add(si);
					}
				} else {
					logger.info("{}不是SearchResult类型", obj.toString());
				}
			}
		} catch (NamingException ex) {
			logger.info("没有查询结果");
		}
		return list;
	}

	/***
	 * 添加用户
	 * 
	 * @param uid
	 *            用户uid
	 * @param ou
	 *            用户组织
	 * @param usermap
	 *            用户属性
	 * @return 添加用户的结果
	 */
	public Result addUser(String uid, String ou, Map<String, String> usermap) {
		if (StringUtil.isNull(uid)) {
			return Result.getError("uid不能为空");
		}
		String dn = StringUtil.isNull(ou) ? String.format("uid=%s", uid) : String.format("uid=%s,%s", uid, ou);

		String oldDN = getUserDN(uid);
		if (oldDN != null) {
			return Result.getError(String.format("已存在此用户[%s]", oldDN));
		}
		if (getOu(ou) == null && !addOUSingle(ou).isSuc()) {
			return Result.getError("没有此OU且添回此OU时失败");
		}
		Attributes attrs = new BasicAttributes(true);
		usermap = usermap == null ? new HashMap<String, String>() : usermap;

		Attribute objclass = new BasicAttribute("objectclass");
		objclass.add("top");
		objclass.add("person");
		objclass.add("organizationalPerson");
		objclass.add("inetorgperson");
		attrs.put(objclass);
		attrs.put("uid", uid);
		attrs.put("sn", uid);
		attrs.put("cn", uid);
		for (String ele : usermap.keySet()) {
			attrs.put(ele, usermap.get(ele));
		}
		try {
			this.ctx.createSubcontext(dn, attrs);
			return Result.getSuc();
		} catch (NamingException e) {
			return Result.getError("添加用户时异常," + e.getMessage());
		}
	}
}
