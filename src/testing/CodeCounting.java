package testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.micheal.freeHands.util.PathUtils;
import org.micheal.freeHands.util.StringUtils;

public class CodeCounting {

	public static void main(String[] args) throws IOException{
		File project = PathUtils.getTargetProjectFile("FreeHands");
		if(null != project){
			System.out.println("总共写了代码:"+rowCount(project,false)+"行");
			System.out.println("其中注释有:"+(rowCount(project,false)-rowCount(project,true))+"行");
		}
	}
	
	public static int rowCount(File file,boolean flag) throws IOException{
		int count = 0;
		if(file.isDirectory()){
			for(File f : file.listFiles()){
				count+=rowCount(f,flag);
			}
		}else{
			//只数java代码
			if(file.getName().toLowerCase().endsWith(".java")){
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = null;
				while((line=br.readLine())!=null){
					if(StringUtils.isNotBlank(line)){
						line = line.trim();
						if(flag){
							if(!line.startsWith("//")){
								if(!line.startsWith("/*")){
									if(!line.startsWith("*")){
										++count;
									}
								}
							}
						}else{
							++count;
						}
					}
				}
			
			}
		}
		
		return count;
	}
}
