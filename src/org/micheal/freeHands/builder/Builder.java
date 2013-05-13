package org.micheal.freeHands.builder;

import java.io.IOException;
import java.util.List;

import org.dom4j.DocumentException;
import org.micheal.freeHands.bean.ConfigurationBean;
import org.micheal.freeHands.exception.ProjectNotExistException;
import org.micheal.freeHands.model.TableModel;
import org.micheal.freeHands.util.PathUtils;

/**
 * 
* @ClassName: Builder 
* @Description: Builder接口。所有Builder都实现这接口
* @author Micheal_Chan 553806198@qq.com 
* @date 2013-4-19 下午5:18:21 
*
 */
public abstract class Builder {
	
	private String charset = "UTF-8";
	private ConfigurationBean config;
	private List<TableModel> tableModelList;
	
	protected abstract void buildSomething() throws Exception;

	public void build() throws Exception{
		checkTargetProject();
		buildSomething();
	}
	
	private void checkTargetProject() throws ProjectNotExistException{
		if(config != null && !PathUtils.checkProject(config.getTargetProject())){
			throw new ProjectNotExistException(config.getTargetProject());
		}
	}
	
	public Builder(){

	}
	
	public Builder(String charSet){
		this.charset = charSet;
	}
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public List<TableModel> getTableModelList() {
		return tableModelList;
	}

	public void setTableModelList(List<TableModel> tableModelList) {
		this.tableModelList = tableModelList;
	}

	public ConfigurationBean getConfig() {
		return config;
	}

	public void setConfig(ConfigurationBean config) {
		this.config = config;
	}

}
