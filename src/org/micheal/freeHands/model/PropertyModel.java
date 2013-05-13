package org.micheal.freeHands.model;

import java.util.ArrayList;
import java.util.List;

import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.StringUtils;

/**
 * 
 * @ClassName: PropertyModel 
 * @Description: 表中的属性模型。包含一个属性的各种信息
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-21 下午10:57:36 
 *
 */
public class PropertyModel {
	//标识是否主键
	public static final Integer NORMAL = 0;
	public static final Integer PRIMARY_KEY = 1;
	public static final Integer FOREIGN_KEY = 2;
	public static final Integer ASSOCIATION = 3;
	public static final Integer COLLECTION = 4;
	//查询条件字段，数据库没有此字段而pojo类中有,查询中不用查此字段。只用于当范围查询的查询条件
	public static final Integer CONDITION = 5;
	
	//变量名
	private String propertyName;
	//字段名（当属性为普通属性时）
	private String columnName;
	//注释
	private String comment;
	//java类型(全限定名)
	private String javaType;
	//数据库字段类型（当属性为普通属性时）
	private String dataType;
	//此属性的类型 0:普通属性 1:主键 2:外键 3:复杂对象(association) 4:集合对象(collection)
	private int type;
	//和数据库字段的逻辑关系,默认是等于
	private String logic = "=";
	
	//以下为type为3,4时候用的属性
	
	//本数据表用于关联的Key
	private String[] keys;
	//关系表关联的字段
	private String[] relKeys;
	//关系表名
	private String relTableName;
	//关系表和引用表的关联字段
	private String[] relRefKeys;
	//外部数据表用于关联的Key
	private String[] refKeys;
	//引用外部数据表名
	private String refTableName;
	//引用数据表字段
	private List<PropertyModel> refProperties;
	//引用的sqlMap的包位置,用于生成sqlMap时候引用外部命名空间的ResultMap
	private String refSqlMapPacket;
	
	//以下属性作为生产java文件用的属性
	
	//要生产class时候的值
	private String value;
	private AccessPermission accessPermission = AccessPermission.PRIVATE;
	private boolean Static = false;
	private boolean Final = false;
	//缩进等级
	private int indentionLevel = 1;
	//需要引用的类
	private List<String> imports = new ArrayList<String>();
	
	/**
	 * 
	 * @Title	getSimpleRefProperties 
	 * @Description	获取引用的简单属性
	 * @return List<PropertyModel>
	 */
	public List<PropertyModel> getSimpleRefProperties(){
		List<PropertyModel> list = new ArrayList<PropertyModel>();
		for(PropertyModel property : this.refProperties){
			if(!property.isCondition()){
				list.add(property);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @Title	addImport 
	 * @Description	添加一给引入
	 * @param javaType void
	 */
	public void addImport(String javaType){
		if(StringUtils.isNotBlank(javaType)){
			//比如java.util.List<String> 去掉结尾的泛型
			javaType = javaType.replaceAll("<.*>$", "");
			//void不引入
			if(!javaType.equals("void")){
				//基本类型不引入
				if(!NameUtils.isBaseType(javaType)){
					//lang包不用引入
					if(!javaType.startsWith("java.lang")){
						//不重复引入
						if(!this.imports.contains(javaType)){
							this.imports.add(javaType);
						}
					}
				}
			}
		}
		this.imports.add(javaType);
	}
	
	/**
	 * 
	 * @Title	indention 
	 * @Description	缩进
	 * @return String
	 */
	public String indention(){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<this.indentionLevel; ++i){
			sb.append("\t");
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @Title	toCode 
	 * @Description	返回此对象的代码表现形式(属性定义)
	 * @return String
	 */
	public String toCode(){
		StringBuffer sb = new StringBuffer();
		if(StringUtils.isNotBlank(this.comment)){
			sb.append(indention());
			sb.append("/**\n");
			sb.append(indention());
			sb.append("*\t"+this.getComment()+"\n");
			sb.append(indention());
			sb.append("*/\n");
		}
		
		sb.append(indention());
		sb.append(this.accessPermission);
		sb.append(" ");
		if(Static){
			sb.append("static ");
		}
		if(Final){
			sb.append("final ");
		}
		if(isCollection()){
			sb.append("List<"+NameUtils.getShortName(javaType)+"> "+propertyName);
		}else{
			sb.append(NameUtils.getShortName(javaType)+" "+propertyName);
		}
		if(StringUtils.isNotBlank(value)){
			sb.append(" = "+value);
		}
		sb.append(";\n");
		return sb.toString();
	}
	
	/**
	 * 
	 * @Title	isCollection 
	 * @Description	如果类型为Collection返回true,否则返回false
	 * @return boolean
	 */
	public boolean isCollection(){
		return this.type == PropertyModel.COLLECTION;
	}
	
	public boolean isCondition(){
		return this.type == CONDITION;
	}
	
	public void setPrimaryKey(){
		this.type = PropertyModel.PRIMARY_KEY;
	}
	
	public void setNormal(){
		this.type = PropertyModel.NORMAL;
	}
	
	public void setCondition(){
		this.type = PropertyModel.CONDITION;
	}
	
	public void setForeignKey(){
		this.type = PropertyModel.FOREIGN_KEY;
	}
	
	public void setAssociation(){
		this.type = PropertyModel.ASSOCIATION;
	}
	
	public void setCollection(){
		this.type = PropertyModel.COLLECTION;
	}
	
	public boolean isPrimaryKey(){
		return this.type == PropertyModel.PRIMARY_KEY;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Integer getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getRelKeys() {
		return relKeys;
	}

	public void setRelKeys(String[] relKeys) {
		this.relKeys = relKeys;
	}

	public String getRelTableName() {
		return relTableName;
	}

	public void setRelTableName(String relTableName) {
		this.relTableName = relTableName;
	}

	public String[] getRefKeys() {
		return refKeys;
	}

	public void setRefKeys(String[] refKeys) {
		this.refKeys = refKeys;
	}

	public String getRefTableName() {
		return refTableName;
	}

	public void setRefTableName(String refTableName) {
		this.refTableName = refTableName;
	}

	public String[] getRelRefKeys() {
		return relRefKeys;
	}

	public void setRelRefKeys(String[] relRefKeys) {
		this.relRefKeys = relRefKeys;
	}

	public List<PropertyModel> getRefProperties() {
		return refProperties;
	}

	public void setRefProperties(List<PropertyModel> refProperties) {
		this.refProperties = refProperties;
	}

	public String getRefSqlMapPacket() {
		return refSqlMapPacket;
	}

	public void setRefSqlMapPacket(String refSqlMapPacket) {
		this.refSqlMapPacket = refSqlMapPacket;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		this.logic = logic;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getIndentionLevel() {
		return indentionLevel;
	}

	public void setIndentionLevel(int indentionLevel) {
		this.indentionLevel = indentionLevel;
	}

	public AccessPermission getAccessPermission() {
		return accessPermission;
	}

	public void setAccessPermission(AccessPermission accessPermission) {
		this.accessPermission = accessPermission;
	}

	public boolean isStatic() {
		return Static;
	}

	public void setStatic(boolean static1) {
		Static = static1;
	}

	public boolean isFinal() {
		return Final;
	}

	public void setFinal(boolean final1) {
		Final = final1;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

}
