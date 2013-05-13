package org.micheal.freeHands.bean;

public class PropertyBean {
	//属性类型   property 正常属性   association 复杂类型对象  collection  复杂类型集合
	//仅在数据库字段类型为外键时有效
	public static final Integer PROPERTY = 0;
	public static final Integer ASSOCIATION = 1;
	public static final Integer COLLECTION = 2;
	public static final Integer CONDITION = 3;
	
	private static final String[] Types = {
		"property",
		"association",
		"collection",
		"condition"
	};
	//属性名
	private String name;
	//列名
	private String column;
	//属性类型  普通属性、条件属性、复杂属性、集合属性
	private int type;
	//全限定名
	private String javaType;
	//条件逻辑
	private String logic;
	//以下为association或collection的属性
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
	
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("\t\tname: "+this.getName());
		sb.append("\n");
		sb.append("\t\tcolumn: "+this.getColumn());
		sb.append("\n");
		sb.append("\t\tjavaType: "+Types[this.getType()]);
		sb.append("\n\n");
		return sb.toString();
	}
	
	public PropertyBean(){
		
	}
	
	public PropertyBean(String column){
		this.column = column;
	}
	
	public PropertyBean(String column,String name){
		this.column = column;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isAssociation(){
		return this.type == ASSOCIATION;
	}
	
	public boolean isProperty(){
		return this.type == PROPERTY;
	}
	
	public boolean isCollection(){
		return this.type == COLLECTION;
	}

	public boolean isCondition(){
		return this.type == CONDITION;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
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

	public static String[] getTypes() {
		return Types;
	}

	public String[] getRelRefKeys() {
		return relRefKeys;
	}

	public void setRelRefKeys(String[] relRefKeys) {
		this.relRefKeys = relRefKeys;
	}

	public String getLogic() {
		return logic;
	}

	public void setLogic(String logic) {
		logic = logic.toLowerCase();
		if(logic.equals("bigger")){
			this.logic = ">";
		}else if(logic.equals("smaller")){
			this.logic = "<";
		}else if(logic.equals("equals")) {
			this.logic = "=";
		}else if(logic.equals("bigger equals")){
			this.logic = ">=";
		}else if(logic.equals("smaller equals")){
			this.logic = "<=";
		}else{
			this.logic = "=";
		}
	}

}
