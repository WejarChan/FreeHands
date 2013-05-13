package org.micheal.freeHands.util;

public class StringUtils {
	
	
	/**
	 * 
	* @Title isEmpty 
	* @Description null 和长度为0的字符串返回true。其余返回false 
	* @param str
	* @return boolean    返回类型 
	 */
	public static boolean isEmpty(String str){
		if(str == null || str.length() <=0){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @Title	isNotEmpty 
	 * @Description	与isEmpty相反
	 * @param str
	 * @return boolean
	 */
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}
	
	/**
	 * 
	* @Title	isBlank 
	* @Description	返回字符串是否为空白字符串或null。
	* @param str
	* @return boolean
	 */
	public static boolean isBlank(String str){
		if(str != null){
			str = str.replaceAll("\\s", "");
			return str.length() <= 0;
		}
		return true;
	}
	
	/**
	 * 
	 * @Title	isNotBlank 
	 * @Description	与isBlank相反 
	 * @param str
	 * @return boolean
	 */
	public static boolean isNotBlank(String str){
		return !isBlank(str);
	}
	
	/**
	 * 
	 * @Title	nvl 
	 * @Description	若参数为空,则返回空字符串(一些情况可以避免空指针异常)
	 * @param str
	 * @return String
	 */
	public static String nvl(String str){
		if(str == null){
			return "";
		}
		return str;
	}
	
}
