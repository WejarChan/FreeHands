package org.micheal.freeHands.model;

public enum ClassType {
	CLASS,INTERFACE,ENUM;
	
	public String toString(){
		if(this == CLASS){
			return "class";
		}else if(this == INTERFACE){
			return "interface";
		}else if(this == ENUM){
			return "enum";
		}
		return null;
	}
}
