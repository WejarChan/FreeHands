package org.micheal.freeHands.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @ClassName: ConfigurationBean 
 * @Description: 与freeHandsConfig对应的一个类。配置类
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-19 下午5:19:02 
 *
 */
public class ConfigurationBean {
	private String targetProject;
	
	private String configurationFile = "/Configuration.xml";
	
	private boolean useGenerateKeys = false;
	
	private String timeout;
	
	private String charset;
	
	private String author;
	
	private JdbcConfigurationBean jdbcConfig;
	
	private JavaModelGenerator javaModelGenerator;
	
	private SqlMapGenerator sqlMapGenerator;
	
	private DaoGenerator daoGenerator;
	
	private List<TableBean> tables;

	public ConfigurationBean(){
		this.jdbcConfig = new JdbcConfigurationBean();
		this.javaModelGenerator = new JavaModelGenerator();
		this.sqlMapGenerator = new SqlMapGenerator();
		this.daoGenerator = new DaoGenerator();
		this.tables = new ArrayList<TableBean>();
	}
	
	public String getTargetProject() {
		return targetProject;
	}

	public void setTargetProject(String targetProject) {
		this.targetProject = targetProject;
	}

	public String getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(String configurationFile) {
		this.configurationFile = configurationFile;
	}

	public JdbcConfigurationBean getJdbcConfig() {
		return jdbcConfig;
	}

	public void setJdbcConfig(JdbcConfigurationBean jdbcConfig) {
		this.jdbcConfig = jdbcConfig;
	}

	public JavaModelGenerator getJavaModelGenerator() {
		return javaModelGenerator;
	}

	public void setJavaModelGenerator(JavaModelGenerator javaModelGenerator) {
		this.javaModelGenerator = javaModelGenerator;
	}

	public SqlMapGenerator getSqlMapGenerator() {
		return sqlMapGenerator;
	}

	public void setSqlMapGenerator(SqlMapGenerator sqlMapGenerator) {
		this.sqlMapGenerator = sqlMapGenerator;
	}

	public DaoGenerator getDaoGenerator() {
		return daoGenerator;
	}

	public void setDaoGenerator(DaoGenerator daoGenerator) {
		this.daoGenerator = daoGenerator;
	}

	public List<TableBean> getTables() {
		return tables;
	}

	public void setTables(List<TableBean> tables) {
		this.tables = tables;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("===========================================\n");
		sb.append("targetProject: "+this.getTargetProject());
		sb.append("\n");
		sb.append("configurationFile: "+this.getConfigurationFile());
		sb.append("\n");
		sb.append("jdbcConfig: \n"+this.getJdbcConfig().toString());
		sb.append("\n");
		sb.append("javaModelGenerator: \n"+this.getJavaModelGenerator().toString());
		sb.append("\n");
		sb.append("sqlMapGenerator: \n"+this.getSqlMapGenerator().toString());
		sb.append("\n");
		sb.append("daoGenerator: \n"+this.getDaoGenerator().toString());
		sb.append("\n");
		sb.append("tables: \n");
		Iterator<TableBean> it = this.tables.iterator();
		while(it.hasNext()){
			sb.append(it.next().toString());
		}
		sb.append("===========================================\n");
		return sb.toString();
		
	}

	public boolean isUseGenerateKeys() {
		return useGenerateKeys;
	}

	public void setUseGenerateKeys(boolean useGenerateKeys) {
		this.useGenerateKeys = useGenerateKeys;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
