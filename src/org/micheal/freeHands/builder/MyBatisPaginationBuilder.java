package org.micheal.freeHands.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.micheal.freeHands.bean.ConfigurationBean;
import org.micheal.freeHands.util.IOUtils;
import org.micheal.freeHands.util.PathUtils;

public class MyBatisPaginationBuilder extends Builder {

	String packet = null;
	
	@Override
	protected void buildSomething() throws Exception {
		String charset = this.getCharset();
		ConfigurationBean config = this.getConfig();
		//存放于com.projectName.base.pagination包下
		packet = "com."+config.getTargetProject().toLowerCase()+".base.pagination";
		
		File packetFile = PathUtils.getPakcetFile(config.getTargetProject(), packet);
		if(!packetFile.exists()){
			packetFile.mkdirs();
			//数据库方言
			buildDialect();
			//paginationContext  传参数用的ThreadLocal
			buildPaginationContext();
			//paginationInfo	分页信息
			buildPaginationInfo();
			//paginationResult	分页查询结果
			buildPaginationResult();
			//paginationInterceptor	物理分页拦截器。
			buildPaginationInterceptor();
		}else{
			//TODO 已经存在。是否记录
		}
		
	}

	private void buildPaginationInterceptor() throws IOException {
		String fileName = "PaginationInterceptor.java";
		File file = PathUtils.getFile(getConfig().getTargetProject(), packet, fileName);
		if(file != null){
			if(!file.exists()){
				StringBuffer sb = new StringBuffer();
				sb.append("package "+packet+";\n\n");
				
				String temPath = System.getProperty("user.dir")+"/src/org/micheal/freeHands/template/PaginationInterceptor.temp";
				if(temPath.indexOf("\\") != -1){
					temPath.replaceAll("\\\\", "/");
				}
				BufferedReader br = new BufferedReader(new FileReader(temPath));
				String line = null;
				while((line = br.readLine())!= null){
					sb.append(line);
					sb.append("\n");
				}
				IOUtils.write2File(file, sb.toString(), getCharset());
			}else{
				//TODO 已经存在
			}
		}
	}

	private void buildPaginationResult() throws IOException {
		String fileName = "PaginationResult.java";
		File file = PathUtils.getFile(getConfig().getTargetProject(), packet, fileName);
		if(file != null){
			if(!file.exists()){
				StringBuffer sb = new StringBuffer();
				sb.append("package "+packet+";\n\n");
				
				String temPath = System.getProperty("user.dir")+"/src/org/micheal/freeHands/template/PaginationResult.temp";
				if(temPath.indexOf("\\") != -1){
					temPath.replaceAll("\\\\", "/");
				}
				BufferedReader br = new BufferedReader(new FileReader(temPath));
				String line = null;
				while((line = br.readLine())!= null){
					sb.append(line);
					sb.append("\n");
				}
				IOUtils.write2File(file, sb.toString(), getCharset());
			}else{
				//TODO 已经存在
			}
		}
	}

	private void buildPaginationInfo() throws IOException {
		String fileName = "PaginationInfo.java";
		File file = PathUtils.getFile(getConfig().getTargetProject(), packet, fileName);
		if(file != null){
			if(!file.exists()){
				StringBuffer sb = new StringBuffer();
				sb.append("package "+packet+";\n\n");
				
				String temPath = System.getProperty("user.dir")+"/src/org/micheal/freeHands/template/PaginationInfo.temp";
				if(temPath.indexOf("\\") != -1){
					temPath.replaceAll("\\\\", "/");
				}
				BufferedReader br = new BufferedReader(new FileReader(temPath));
				String line = null;
				while((line = br.readLine())!= null){
					sb.append(line);
					sb.append("\n");
				}
				IOUtils.write2File(file, sb.toString(), getCharset());
			}else{
				//TODO 已经存在
			}
		}
	}

	private void buildPaginationContext() throws IOException {
		String fileName = "PaginationContext.java";
		File file = PathUtils.getFile(getConfig().getTargetProject(), packet, fileName);
		if(file != null){
			if(!file.exists()){
				StringBuffer sb = new StringBuffer();
				sb.append("package "+packet+";\n\n");
				
				String temPath = System.getProperty("user.dir")+"/src/org/micheal/freeHands/template/PaginationContext.temp";
				if(temPath.indexOf("\\") != -1){
					temPath.replaceAll("\\\\", "/");
				}
				BufferedReader br = new BufferedReader(new FileReader(temPath));
				String line = null;
				while((line = br.readLine())!= null){
					sb.append(line);
					sb.append("\n");
				}
				IOUtils.write2File(file, sb.toString(), getCharset());
			}else{
				//TODO 已经存在
			}
		}
	}

	private void buildDialect() throws IOException {
		String fileName = "Dialect.java";
		File file = PathUtils.getFile(getConfig().getTargetProject(), packet, fileName);
		if(file != null){
			if(!file.exists()){
				StringBuffer sb = new StringBuffer();
				sb.append("package "+packet+";\n\n");
				sb.append("import "+packet+".PaginationInfo.SortOrder;\n\n");
				
				String temPath = System.getProperty("user.dir")+"/src/org/micheal/freeHands/template/Dialect.temp";
				if(temPath.indexOf("\\") != -1){
					temPath.replaceAll("\\\\", "/");
				}
				BufferedReader br = new BufferedReader(new FileReader(temPath));
				String line = null;
				while((line = br.readLine())!= null){
					sb.append(line);
					sb.append("\n");
				}
				IOUtils.write2File(file, sb.toString(), getCharset());
			}else{
				//TODO 已经存在
			}
		}
	}

}
