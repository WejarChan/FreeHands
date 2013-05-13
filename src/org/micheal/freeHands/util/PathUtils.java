package org.micheal.freeHands.util;

import java.io.File;

public class PathUtils {
	
	/**
	 * 
	 * @Title	getPacketPath 
	 * @Description	将 com.micheal.user.pojo 这样的包名转换成
	 *  com/micheal/user/pojo 这样的相对路径并返回
	 * @param packet
	 * @return String
	 */
	public static String getPacketPath(String packet){
		return packet.replaceAll("\\.", "\\/");
	}
	
	/**
	 * 
	 * @Title	getFile 
	 * @Description	若项目存在,返回file的引用
	 * @param project
	 * @param packet
	 * @param file
	 * @return File
	 */
	public static File getFile(String project,String packet,String file){
		if(checkProject(project)){
			String path = getWorkSpacePath()+"/"+project+"/src/";
			
			if(StringUtils.isNotBlank(packet)){
				path += packet.replaceAll("\\.", "/")+"/";
			}
			
			if(StringUtils.isNotBlank(file)){
				path += file;
			}
			if(path.indexOf("\\") != -1){
				path.replaceAll("\\\\", "/");
			}
			return new File(path);
		}
		return null;
	}
	
	/**
	 * 
	 * @Title	getPakcetFile
	 * @Description	项目存在，返回targetPacket的File对象,否则返回null
	 * @param project
	 * @param targetPacket
	 * @return File
	 */
	public static File getPakcetFile(String project,String targetPacket){
		return getFile(project,targetPacket,null);
	}
	
	/**
	 * 
	 * @Title	getTargetProjectFile 
	 * @Description	若项目存在，则返回File对象，否则返回null
	 * @param project
	 * @return File
	 */
	public static File getTargetProjectFile(String project){
		return getFile(project,null,null);
	}
	
	/**
	 * 
	 * @Title	getWorkSpacePath 
	 * @Description	返回当前workSpace的path 
	 * @return String
	 */
	public static String getWorkSpacePath(){
		String path = ClassLoader.getSystemResource("").getPath();
		File f = new File(path);
		path = f.getParentFile().getParent();
		return path.replaceAll("\\\\", "/");
	}
	
	/**
	 * 
	 * @Title	checkProject 
	 * @Description	项目存在返回 true,否则返回false
	 * @param projectName
	 * @return boolean
	 */
	public static boolean checkProject(String projectName){
		return checkFileExist(getWorkSpacePath()+"/"+projectName);
	}
	
	/**
	 * 
	 * @Title	checkFileExist 
	 * @Description	文件存在返回true,不存在返回false
	 * @param file
	 * @return boolean
	 */
	public static boolean checkFileExist(File file){
		if(file.isDirectory() || file.isFile()){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @Title	checkFileExist 
	 * @Description	文件存在返回true,不存在返回false
	 * @param path
	 * @return boolean
	 */
	public static boolean checkFileExist(String path){
		File file = new File(path);
		return checkFileExist(file);
	}
	
}
