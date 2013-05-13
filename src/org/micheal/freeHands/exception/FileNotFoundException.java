package org.micheal.freeHands.exception;

import java.io.File;

public class FileNotFoundException extends Exception {

	public FileNotFoundException(File f){
		super(f.getAbsolutePath()+" is not found! please check the url!");
	}
	
}
