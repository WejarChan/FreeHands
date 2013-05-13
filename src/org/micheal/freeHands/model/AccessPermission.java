package org.micheal.freeHands.model;

public enum AccessPermission {
	PUBLIC,PROTECTED,PRIVATE,DEFAULT;
	
	public String toString(){
		if(this == DEFAULT){
			return "";
		}else if(this == PUBLIC){
			return "public";
		}else if(this == PROTECTED){
			return "protected";
		}else if(this == PRIVATE){
			return "private";
		}
		
		return null;
	}
}
