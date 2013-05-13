package org.micheal.freeHands.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

public class NameUtils {
	
	
	
	/**
	 * 
	 * @Title	getNestedSelectName 
	 * @Description	返回嵌套查询的名字(id值)
	 * @param alias
	 * @return String
	 */
	public static String getNestedSelectName(String alias){
		return "nestedSelect"+alias;
	}
	
	/**
	 * 
	 * @Title	getTableAlias 
	 * @Description	返回表别名,首字母加上'_'后的首字母组成的字符串
	 * 				例如: user_info 返回 ui
	 * @param tableName
	 * @return String
	 */
	public static String getTableAlias(String tableName){
		char[] array = tableName.toCharArray();
		StringBuffer alias = new StringBuffer();
		if(array.length >0){
			alias.append(array[0]);
			for(int i=1; i<array.length; ++i){
				if(array[i] == '_'){
					if(i<array.length-1){
						alias.append(array[i+1]);
					}
				}
			}
		}
		return alias.toString();
	}
	/**
	 * 
	 * @Title	getNestedResultMapName 
	 * @Description	返回嵌套ResultMap的名字
	 * @param alias
	 * @return String
	 */
	public static String getNestedResultMapName(String alias){
		return lowerCaseStart(alias)+"NestedResultMap";
	}

	/**
	 * 
	 * @Title	getMapperName 
	 * @Description	传入alias返回Mapper的类名
	 * @param alias
	 * @return String
	 */
	public static String getMapperName(String alias){
		return alias+"Mapper";
	}
	
	/**
	 * 
	 * @Title	getSqlMapFileName 
	 * @Description	传入alias返回sqlMap文件的名字
	 * @param alias
	 * @return String
	 */
	public static String getSqlMapFileName(String alias){
		return alias+"Mapper.xml";
	}
	
	public static String getSubSelectName(String alias){
		return lowerCaseStart(alias)+"SubSelect";
	}
	
	/**
	 * 
	 * @Title	getPackage 
	 * @Description	从全限定名中获取包的位置
	 * @param fullName
	 * @return String
	 */
	public static String getPackage(String fullName){
		if(fullName == null && fullName.lastIndexOf('.') == -1)
			return null;
		return fullName.substring(0,fullName.lastIndexOf('.')-1);
	}
	
	/**
	 * 
	 * @Title	getShortName 
	 * @Description	从权限定名中获取类名
	 * @param fullName
	 * @return String
	 */
	public static String getShortName(String fullName){
		if(fullName == null)
			return null;
		return fullName.substring(fullName.lastIndexOf('.')+1,fullName.length());
	}
	
	/**
	 * 
	 * @Title	isBaseType 
	 * @Description	判断是否基本类型。若为基本类型则返回true，否则false
	 * @param type
	 * @return boolean
	 */
	public static boolean isBaseType(String type){
		type = type.trim();
		if(type.equals("byte") || type.equals("short") || type.equals("int")
				||type.equals("long") || type.equals("float") || type.equals("double")
				|| type.equals("boolean") || type.equals("char")){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @Title	isBoxBaseType 
	 * @Description	若type是基本类型包装类,返回true.否则false
	 * @param type
	 * @return boolean
	 */
	public static boolean isBoxBaseType(String type){
		if(type.equals("Byte") || type.equals("Short") || type.equals("Integer")
				|| type.equals("Long") || type.equals("Float") || type.equals("Double")
				|| type.equals("Boolean") || type.equals("Character")){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @Title	getPropertyType 
	 * @Description	传入一个javaType,若是基本类型的包装类,则返回基本类型名。否则返回类名
	 * @param javaType
	 * @return boolean
	 */
	public static String getPropertyType(String javaType){
		String alias = getShortName(javaType);
		
		if(alias.equals("Byte")){
			alias = "byte";
		}else if(alias.equals("Short")){
			alias = "short";
		}else if(alias.equals("Integer")){
			alias = "int";
		}else if(alias.equals("Long")){
			alias = "long";
		}else if(alias.equals("Float")){
			alias = "float";
		}else if(alias.equals("Double")){
			alias = "double";
		}else if(alias.equals("Boolean")){
			alias = "boolean";
		}else if(alias.equals("Character")){
			alias = "char";
		}
		
		return alias;
	}
	
	/**
	 * 
	 * @Title	getFullName 
	 * @Description	获取常用的类型的全限定名
	 * @param type 类名,Integer float …… 不区分大小写
	 * @return String
	 */
	public static String getFullName(String type){
		if(isBaseType(type)){
			return type;
		}
		type = type.trim().toLowerCase();
		if(type.equals("integer")){
			return Integer.class.getName();
		}else if(type.equals("float")){
			return Float.class.getName();
		}else if(type.equals("long")){
			return Long.class.getName();
		}else if(type.equals("double")){
			return Double.class.getName();
		}else if(type.equals("bigdecimal")){
			return BigDecimal.class.getName();
		}else if(type.equals("string")){
			return String.class.getName();
		}else if(type.equals("date")){
			return Date.class.getName();
		}else if(type.equals("timestamp")){
			return Timestamp.class.getName();
		}else if(type.equals("boolean")){
			return Boolean.class.getName();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @Title	toCamelCase 
	 * @Description	将参数nameBefore转换成驼峰命名格式返回('_',' '后边的首字母转换成大写,并去掉'_',' ')
	 * @param nameBefore
	 * @return String
	 */
	public static String toCamelCase(String nameBefore){
		if(nameBefore.isEmpty()){
			return nameBefore;
		}
		String nameAfter = null;
		if(nameBefore.indexOf('_') != -1){
			nameBefore = nameBefore.toLowerCase();
		}
		char[] array = nameBefore.toLowerCase().toCharArray();
		for(int i=0; i<array.length; ++i){
			if(array[i] == '_' || array[i] == ' '){
				int j=1;
				while(array[i+j] == '_' || array[i+j] == ' '){
					++j;
				}
				if(array[i+j] >= 97 && array[i+j] <= 122){
					array[i+j] -= (97-65);
				}
				i+=j;
			}
		}
		nameAfter = new String(array).replaceAll("_","").replaceAll(" ", "");
		return nameAfter;
	}
	
	/**
	 * 
	 * @Title	lowerCaseStart
	 * @Description	转成小写开头
	 * @param nameBefore
	 * @return String
	 */
	public static String lowerCaseStart(String nameBefore){
		char[] array = nameBefore.toCharArray();
		if(array[0] >= 65 && array[0] <= 91){
			array[0] += (97-65);
		}
		return new String(array);
	}
	
	/**
	 * 
	 * @Title	upperCaseStart 
	 * @Description	将名字转成大写字母开头
	 * @param nameBefore
	 * @return String
	 */
	public static String upperCaseStart(String nameBefore){
		char[] array = nameBefore.toCharArray();
		if(array[0] >= 97 && array[0] <= 122){
			array[0] -= (97-65);
		}
		return new String(array);
	}
	
	/**
	 * 
	 * @Title	toCamelCaseClassName 
	 * @Description	去掉'_'并转换成驼峰标识,且首字母大写,可用于当类名
	 * @param nameBefore
	 * @return String
	 */
	public static String toCamelCaseClassName(String nameBefore){
		if(nameBefore.isEmpty()){
			return nameBefore;
		}
		String nameAfter = null;
		char[] array = nameBefore.toLowerCase().toCharArray();
		for(int i=0; i<array.length; ++i){
			if(array[i] == '_' || array[i] == ' '){
				int j=1;
				while(array[i+j] == '_' || array[i+j] == ' '){
					++j;
				}
				if(array[i+j] >= 97 && array[i+j] <= 122){
					array[i+j] -= (97-65);
				}
				i+=j;
			}
		}
		if(array[0] >= 97 && array[0] <= 122){
			array[0] -= (97-65);
		}
		nameAfter = new String(array).replaceAll("_","").replaceAll(" ", "");
		return nameAfter;
	}

	/**
	 * 
	 * @Title	getSubResultMap 
	 * @Description	返回嵌套结果集的子结果集名字(id) 
	 * @param refAlias
	 * @return String
	 */
	public static String getSubResultMap(String refAlias) {
		return lowerCaseStart(refAlias)+"SubResultMap";
	}

	/**
	 * 
	 * @Title	getJoinSelectName 
	 * @Description	返回连表查询select语句的名字(id)
	 * @param alias
	 * @return String
	 */
	public static String getJoinSelectName(String alias) {
		return "joinSelect"+alias;
	}

	/**
	 * 
	 * @Title	getJoinResultMapName 
	 * @Description	返回连表查询的结果集名字(id值) 
	 * @param alias
	 * @return String
	 */
	public static String getJoinResultMapName(String alias){
		return lowerCaseStart(alias)+"JoinResultMap";
	}

	/**
	 * 
	 * @Title	getDaoInterfaceName 
	 * @Description	获取dao接口的名字 
	 * @param alias
	 * @return String
	 */
	public static String getDaoInterfaceName(String alias) {
		return alias+"Dao";
	}

	/**
	 * 
	 * @Title	getdaoImplName 
	 * @Description	获取dao的实现类名
	 * @param alias
	 * @return String
	 */
	public static String getdaoImplName(String alias) {
		return alias+"DaoImpl";
	}

}
