package net.wicp.tams.commons.apiext;

import net.wicp.tams.commons.constant.MathConvertType;

/***
 * 时间处理常用方法
 * 
 * @author Administrator
 * 
 */
public abstract class TimeUtil {

	/***
	 * 把秒数转为分钟数
	 * @param min			秒数
	 * @param conType		转换类型，分为trunc("取整"),round("四舍五入"),ceil("有值进1")
	 * @return				分钟数
	 */
	public static int convertMinuteToSecond(int min, MathConvertType conType) {
		int retValue = 0;
		switch (conType) {
		case trunc:
			retValue = (int) Math.floor((float) min / 60);
			break;
		case round:
			retValue = (int) Math.rint((float) min / 60);
			break;
		case ceil:
			retValue = (int) Math.ceil((float) min / 60);
			break;
		default:
			break;
		}
		return retValue;
	}
}
