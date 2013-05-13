package org.micheal.freeHands.bean;

public class JdbcConfigurationBean {
	private String jdbcClassPath;
	private String driver;
	private String url;
	private String user;
	private String password;
	
	public String getJdbcClassPath() {
		return jdbcClassPath;
	}
	public void setJdbcClassPath(String jdbcClassPath) {
		this.jdbcClassPath = jdbcClassPath;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("\tjdbcClassPath: "+this.getJdbcClassPath());
		sb.append("\n");
		sb.append("\tdriver: "+this.getDriver());
		sb.append("\n");
		sb.append("\turl: "+this.getUrl());
		sb.append("\n");
		sb.append("\tuser: "+this.getUser());
		sb.append("\n");
		sb.append("\tpassword: "+this.getPassword());
		
		return sb.toString();
	}
	
}
