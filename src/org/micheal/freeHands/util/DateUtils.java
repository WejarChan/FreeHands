package org.micheal.freeHands.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	/**
	 * 
	 * @Title	format 
	 * @Description	根据format格式化时间
	 * @param d
	 * @param format
	 * @return String
	 */
	public static String format(Date d,String format){
		SimpleDateFormat smf = new SimpleDateFormat(format);
		return smf.format(d);
	}
	
	/**
	 * 
	 * @Title	parse 
	 * @Description	根据格式解析时间
	 * @param dateStr
	 * @param format
	 * @return
	 * @throws ParseException Date
	 */
	public static Date parse(String dateStr,String format) throws ParseException{
		SimpleDateFormat smf = new SimpleDateFormat(format);
		return smf.parse(dateStr);
	}
	
	/**
	 * 
	 * @Title	format 
	 * @Description	将时间格式化成  yyyy-MM-dd hh:mm:ss 返回
	 * @param d
	 * @return String
	 */
	public static String format(Date d){
		return format(d,"yyyy-MM-dd hh:mm:ss");
	}
	
	/**
	 * 
	 * @Title	parse 
	 * @Description	解析yyyy-MM-dd hh:mm:ss格式的字符串返回Date 
	 * @param dateStr
	 * @return
	 * @throws ParseException Date
	 */
	public static Date parse(String dateStr) throws ParseException{
		return parse(dateStr,"yyyy-MM-dd hh:mm:ss");
	}
}
