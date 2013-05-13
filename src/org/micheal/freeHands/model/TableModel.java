package org.micheal.freeHands.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: TableModel 
 * @Description: 要生产配置的表模型。里边包含相关表各种信息
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-21 下午10:56:49 
 *
 */
public class TableModel{
	//表名
	private String tableName;
	//要生成的类的全限定名
	private String javaType;
	//类别名
	private String alias;
	//pojo类存放的包
	private String pojoTargetPacket;
	//pojo类的父类全限定名
	private String pojoSuperClass;
	//sqlMap存放的包
	private String sqlMapTargetPacket;
	//dao存放的包
	private String daoTargetPacket;
	//dao父类全限定名
	private String daoSuperClass;
	//普通属性
	List<PropertyModel> properties;
	//复杂对象属性
	List<PropertyModel> Associations;
	//集合型复杂对象属性
	List<PropertyModel> Collections;

	public TableModel(){
		this.properties = new ArrayList<PropertyModel>();
		this.Associations = new ArrayList<PropertyModel>();
		this.Collections = new ArrayList<PropertyModel>();
	}

	/**
	 * 
	 * @Title	getSimpleProperties 
	 * @Description	获取和数据表对应的字段
	 * @return List<PropertyModel>
	 */
	public List<PropertyModel> getSimpleProperties(){
		List<PropertyModel> list = new ArrayList<PropertyModel>();
		for(PropertyModel property : this.properties){
			if(!property.isCondition()){
				list.add(property);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @Title	getConditions 
	 * @Description	获取条件字段
	 * @return List<PropertyModel>
	 */
	public List<PropertyModel> getConditions(){
		List<PropertyModel> list = new ArrayList<PropertyModel>();
		for(PropertyModel property : this.properties){
			if(property.isCondition()){
				list.add(property);
			}
		}
		return list;
	}
	
	public PropertyModel getPrimaryKey(){
		for(PropertyModel property : properties){
			if(property.getType().equals(PropertyModel.PRIMARY_KEY)){
				return property;
			}
		}
		return null;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getPojoTargetPacket() {
		return pojoTargetPacket;
	}

	public void setPojoTargetPacket(String pojoTargetPacket) {
		this.pojoTargetPacket = pojoTargetPacket;
	}

	public String getPojoSuperClass() {
		return pojoSuperClass;
	}

	public void setPojoSuperClass(String pojoSuperClass) {
		this.pojoSuperClass = pojoSuperClass;
	}

	public String getSqlMapTargetPacket() {
		return sqlMapTargetPacket;
	}

	public void setSqlMapTargetPacket(String sqlMapTargetPacket) {
		this.sqlMapTargetPacket = sqlMapTargetPacket;
	}

	public String getDaoTargetPacket() {
		return daoTargetPacket;
	}

	public void setDaoTargetPacket(String daoTargetPacket) {
		this.daoTargetPacket = daoTargetPacket;
	}

	public String getDaoSuperClass() {
		return daoSuperClass;
	}

	public void setDaoSuperClass(String daoSuperClass) {
		this.daoSuperClass = daoSuperClass;
	}

	public List<PropertyModel> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyModel> properties) {
		this.properties = properties;
	}

	public List<PropertyModel> getAssociations() {
		return Associations;
	}

	public void setAssociations(List<PropertyModel> associations) {
		Associations = associations;
	}

	public List<PropertyModel> getCollections() {
		return Collections;
	}

	public void setCollections(List<PropertyModel> collections) {
		Collections = collections;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
}
