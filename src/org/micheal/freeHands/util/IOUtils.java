package org.micheal.freeHands.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class IOUtils {

	/**
	 * 
	 * @Title	write2File 
	 * @Description	往一个文件里边写入一些内容,默认采用UTF-8编码
	 * @param file
	 * @param content
	 * @param charset
	 * @return
	 * @throws IOException int
	 */
	public static int write2File(File file,String content,String charset) throws IOException{
		if(StringUtils.isBlank(charset)){
			charset = "UTF-8";
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rws");
		int pos = 0;
		byte[] bytes = content.getBytes(charset);
		raf.seek(pos);
		raf.write(bytes);
		return bytes.length;
	}
	
	/**
	 * 
	 * @Title	write2File 
	 * @Description	往一个文件里边写入一些内容,默认采用UTF-8编码
	 * @param path
	 * @param content
	 * @param charset
	 * @return
	 * @throws IOException int
	 */
	public static int write2File(String path,String content,String charset) throws IOException{
		return write2File(new File(path),content,charset);
	}
}
