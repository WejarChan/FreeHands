package org.micheal.freeHands.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableBean {
	public static final String CAMEL_CASE = "camelCase";
	public static final String COLUMN_NAME = "columnName";
	
	private String schema;
	private String tableName;
	private String javaType;
	private String propertyNameRule;
	private String columnPrefix;
	private String columnSuffix;
	private String alias;
	private String targetPacket;
	private String packetAlias;
	
	private List<PropertyBean> properties;
	
	public TableBean(){
		this.propertyNameRule = TableBean.CAMEL_CASE;
		this.properties = new ArrayList<PropertyBean>();
	}
	
	/**
	 * 
	 * @Title	getConditionProperties 
	 * @Description	获取所有新添加的逻辑条件属性
	 * @return List<PropertyBean>
	 */
	public List<PropertyBean> getConditionProperties(){
		List<PropertyBean> result = null;
		if(properties != null){
			result = new ArrayList<PropertyBean>();
			for(PropertyBean bean : properties){
				if(bean.isCondition()){
					result.add(bean);
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @Title	getComplexProperties 
	 * @Description	获取propertis里边association和collection的集合
	 * @return List<PropertyBean>
	 */
	public List<PropertyBean> getComplexProperties(){
		List<PropertyBean> result = null;
		if(properties != null){
			result = new ArrayList<PropertyBean>();
			for(PropertyBean bean : properties){
				if(bean.isAssociation() || bean.isCollection()){
					result.add(bean);
				}
			}
		}
		return result;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("\tschema: "+this.getSchema());
		sb.append("\n");
		sb.append("\ttableName: "+this.getTableName());
		sb.append("\n");
		sb.append("\tjavaType: "+this.getJavaType());
		sb.append("\n");
		sb.append("\tpropertyNameRule: "+this.getPropertyNameRule());
		sb.append("\n");
		sb.append("\tproperties: ");
		sb.append("\n");
		Iterator<PropertyBean> it = properties.iterator();
		while(it.hasNext()){
			sb.append(it.next().toString());
		}
		return sb.toString();
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPropertyNameRule() {
		return propertyNameRule;
	}

	public void setPropertyNameRule(String propertyNameRule) {
		this.propertyNameRule = propertyNameRule;
	}

	public List<PropertyBean> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyBean> properties) {
		this.properties = properties;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getColumnPrefix() {
		return columnPrefix;
	}

	public void setColumnPrefix(String columnPrefix) {
		this.columnPrefix = columnPrefix;
	}

	public String getColumnSuffix() {
		return columnSuffix;
	}

	public void setColumnSuffix(String columnSuffix) {
		this.columnSuffix = columnSuffix;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTargetPacket() {
		return targetPacket;
	}

	public void setTargetPacket(String targetPacket) {
		this.targetPacket = targetPacket;
	}

	public String getPacketAlias() {
		return packetAlias;
	}

	public void setPacketAlias(String packetAlias) {
		this.packetAlias = packetAlias;
	}
}

