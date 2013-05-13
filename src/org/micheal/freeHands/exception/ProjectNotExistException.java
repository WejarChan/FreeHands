package org.micheal.freeHands.exception;

public class ProjectNotExistException extends Exception {
	
	public ProjectNotExistException(){
		
	}
	
	public ProjectNotExistException(String project){
		super(project +" is not exist!");
	}
}
