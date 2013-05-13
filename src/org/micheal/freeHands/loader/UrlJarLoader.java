package org.micheal.freeHands.loader;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.micheal.freeHands.exception.FileFormatException;
import org.micheal.freeHands.exception.FileNotFoundException;

public class UrlJarLoader {  
    private URLClassLoader urlClassLoader;  
    
    public UrlJarLoader() {  
        this.urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();  
    }  
      
    private void loadJar(URL url) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {  
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);  
        addURL.setAccessible(true);  
        addURL.invoke(urlClassLoader, url);
    }  
    
    public void load(String path) throws MalformedURLException, FileFormatException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException {
		if(path.indexOf("\\") != -1){
			path.replaceAll("\\\\", "/");
		}
    	File file = new File(path);
    	if(file != null && file.exists()){
    		if(file.isDirectory()){
    			loadDir(file);
    		}else{
    			if(file.getName().trim().toLowerCase().endsWith(".jar")){
    				loadJar(file.toURL());
    			}else{
    				String msg = file.getAbsolutePath()+" is not a jar file!";
    				throw new FileFormatException(msg);
    			}
    		}
    	}else{
    		throw new FileNotFoundException(file);
    	}
	}

	private void loadDir(File libdir) throws SecurityException, IllegalArgumentException, MalformedURLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if (libdir != null && libdir.isDirectory()) {
			//只查找.jar结尾的文件
			File[] listFiles = libdir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.exists() && file.isFile()
							&& file.getName().toLowerCase().endsWith(".jar");
				}
			});

			for (File file : listFiles) {
				loadJar(file.toURL());
			}

		} else {
			System.out.println("[Console Message] Directory [" + libdir.getAbsolutePath()
					+ "] does not exsit, please check it");
			System.exit(0);
		}
	}
}  
