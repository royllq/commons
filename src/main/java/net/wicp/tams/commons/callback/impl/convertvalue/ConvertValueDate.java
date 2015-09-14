package net.wicp.tams.commons.callback.impl.convertvalue;

import java.util.Date;

import net.wicp.tams.commons.callback.IConvertValue;
import net.wicp.tams.commons.constant.DateFormatCase;

/****
 * 把时间转为String
 * @author zhoujunhui
 *
 */
public class ConvertValueDate implements IConvertValue<Date> {
	private DateFormatCase formate;

	public ConvertValueDate(DateFormatCase formate) {
		this.formate = formate;
	}

	/***
	 * 默认的格式是 “yyyy-MM-DD”
	 */
	public ConvertValueDate() {
		this.formate = DateFormatCase.YYYY_MM_DD;
	}

	@Override
	public String getStr(Date keyObj) {
		return this.formate.getInstanc().format(keyObj);
	}

}
