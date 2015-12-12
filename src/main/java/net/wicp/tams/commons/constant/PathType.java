package net.wicp.tams.commons.constant;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.ArrayUtils;
import net.wicp.tams.commons.apiext.IOUtil;
import net.wicp.tams.commons.apiext.StringUtil;

/***
 * 文件路径寻找规则
 * 
 * @author andy.zhou
 *
 */
public enum PathType {
	web("web根目录"), clp("classpath根目录"), rel("相对于class的路径"), abs("绝对路径");

	private final String desc;

	private PathType(String desc) {
		this.desc = desc;
	}

	/***
	 * 解析PathType类型的路径得到实际路径，如 web:/aaaa/bbb
	 * 
	 * @param inputpath
	 *            PathType类型的路径
	 * @param splitchar
	 *            类型分隔符
	 * @return
	 */
	public static String getPath(String inputpath, String splitchar) {
		if (StringUtil.isNull(inputpath)) {
			return "";
		}
		splitchar = StringUtil.hasNull(splitchar, ":");
		String[] inputpathary = inputpath.split(splitchar);
		if (ArrayUtils.isEmpty(inputpathary)) {
			return "";
		}
		String PathTypeStr = inputpathary.length > 1 ? inputpathary[0] : "";
		String inputpathStr = inputpathary.length > 1 ? inputpathary[1] : inputpathary[0];
		PathType pathType = getByName(PathTypeStr);
		String retstr = inputpathStr;
		switch (pathType) {
		case web:
			URL classpathurl = Thread.currentThread().getContextClassLoader().getResource("/");
			File file = new File(classpathurl.getPath());
			String webrootstr = file.getParentFile().getParentFile().getPath();
			retstr = IOUtil.mergeFolderAndFilePath(webrootstr, inputpathStr);
			break;

		case clp:
			URL classpath = Thread.currentThread().getContextClassLoader().getResource("/");
			retstr = IOUtil.mergeFolderAndFilePath(classpath.getPath(), inputpathStr);
			break;
		case rel:
			URL classpathrel = Thread.currentThread().getContextClassLoader().getResource(".");
			retstr = IOUtil.mergeFolderAndFilePath(classpathrel.getPath(), inputpathStr);
			break;
		case abs:
			retstr = inputpathStr;
			break;
		default:
			break;
		}
		return retstr;
	}

	public static String getPath(String inputpath) {
		return getPath(inputpath, ":");
	}

	/***
	 * 通过名字得到路径寻找类型
	 * 
	 * @param pathpath
	 *            路径寻找类型字符串
	 * @return
	 */
	public static PathType getByName(String pathpath) {
		if (StringUtil.isNull(pathpath)) {// 默认是classpath根目录
			return clp;
		}
		for (PathType ele : PathType.values()) {
			if (pathpath.equalsIgnoreCase(ele.name())) {
				return ele;
			}
		}
		return clp;
	}

	public String getDesc() {
		return desc;
	}
}
