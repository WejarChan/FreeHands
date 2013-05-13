package org.micheal.freeHands.exception;

public class ConfigurationFormatException extends Exception {
	
	public static final Integer MISSING_ATTRIBUTE = 0;
	public static final Integer MISSING_SUBELEMENT = 1;
	public static final Integer TOO_MANY_ELEMENT = 2;
	public static final Integer MISSING_PROPERTY = 3;
	public static final Integer UNKNOW_SUBELEMENT = 4;
	
	
	private static final String[] messages = {
		"The element \"$1$\" missing attribute \"$2$\".",
		"There is no subElement named \"$1$\" in the element \"$2$\".",
		"There is too many element named \"$1$\" in element \"$2$\".",
		"There is no subElement or attribute named \"$1$\" in element \"$2$\".",
		"There is an unknow subElement \"$1$\" in the element \"$2$\"."
	};
	
	
	public ConfigurationFormatException(){
		super("The Configuration Format is wrong! Please check up!");
	}
	
	
	public ConfigurationFormatException(String msg){
		super(msg);
	}
	
	public ConfigurationFormatException(Integer type){
		super(messages[type]);
	}
	
	public ConfigurationFormatException(Integer type,String one){
		super(messages[type].replace("$1$", one));
	}
	
	public ConfigurationFormatException(Integer type,String one,String two){
		super(messages[type].replace("$1$", one).replace("$2$", two));
	}
	
}
