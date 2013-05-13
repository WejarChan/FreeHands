package org.micheal.freeHands.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.micheal.freeHands.util.DateUtils;
import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.StringUtils;

/**
 * 
 * @ClassName: ClassModel 
 * @Description: 这个类可以用于生成简单的java类的代码,设置好属性后调用
 * 				toCode方法,即可生成类的代码,以String的形式返回
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-12 下午11:05:50 
 *
 */
public class ClassModel {
	
	//类型，class 、interface 、 enum
	private ClassType classType;
	//访问权限 public protected private default(不写)
	private AccessPermission accessPermission;
	//是否抽象
	private boolean Abstract = false;
	//是否终结状态
	private boolean Final = false;
	//所在的包
	private String packet;
	//类别名
	private String alias;
	//父类javaType
	private String Super;
	//实现接口
	private List<String> Implements;
	//包含属性
	private List<PropertyModel> properties;
	//包含方法
	private List<MethodModel> methods;
	//描述
	private List<String> descriptions;
	//作者
	private String author;
	//将要引用的
	private List<String> imports;
	
	public ClassModel(){
		imports = new ArrayList<String>();
		Implements = new ArrayList<String>();
		methods = new ArrayList<MethodModel>();
		properties = new ArrayList<PropertyModel>();
		descriptions = new ArrayList<String>();
	}
	
	/**
	 * 
	 * @Title	addImports 
	 * @Description	添加类引入
	 * @param list void
	 */
	public void addImports(List<String> list){
		if(list != null && list.size()>0){
			for(String fullName : list){
				addImport(fullName);
			}
		}
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
						//同包下不引入
						if(!javaType.startsWith(this.packet)){
							//不重复引入
							if(!this.imports.contains(javaType)){
								this.imports.add(javaType);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title	addProperty 
	 * @Description	添加一个属性 
	 * @param property void
	 */
	public void addProperty(PropertyModel property){
		this.properties.add(property);
	}
	
	/**
	 * 
	 * @Title	addImplement 
	 * @Description	添加一个实现的接口
	 * @param fullName void
	 */
	public void addImplement(String fullName){
		this.Implements.add(fullName);
	}
	
	/**
	 * 
	 * @Title	addMethod 
	 * @Description	添加一个方法模型
	 * @param method void
	 */
	public void addMethod(MethodModel method){
		this.methods.add(method);
	}
	
	/**
	 * 
	 * @Title	toCode 
	 * @Description	返回这个Class的代码。用String返回
	 * @return String
	 */
	public String toCode(){
		
		//把所有可能要引入的地方都给添加引入
		if(StringUtils.isNotBlank(Super)){
			addImport(Super);
		}
		if(this.Implements.size() >0){
			for(String implement : this.Implements){
				addImport(implement);
			}
		}
		if(classType == ClassType.CLASS){
			if(this.properties.size() >0){
				for(PropertyModel property : this.properties){
					addImport(property.getJavaType());
					addImports(property.getImports());
					addGetterSetter(property);
				}
			}
		}
		if(this.methods.size() >0){
			for(MethodModel method : this.methods){
				addImport(method.getResultJavaType());
				for(String paramType : method.getParamJavaType()){
					addImport(paramType);
				}
				addImports(method.getImports());
			}
		}
		
		
		//类的所有内容
		StringBuffer classContent = new StringBuffer();
		String packet = null;
		//package
		packet = "package "+this.packet+";";
		
		classContent.append(packet);
		classContent.append("\n");
		classContent.append("\n");
		//imports
		if(imports.size() >0){
			for(String javaType : imports){
				classContent.append("import "+javaType+";");
				classContent.append("\n");
			}
		}
		classContent.append("\n");
		//注释
		classContent.append(getCommentCode());
		classContent.append("\n");
		classContent.append("\n");
		//类头
		classContent.append(this.accessPermission+" ");
		if(this.Abstract){
			classContent.append("abstract ");
		}
		if(this.Final){
			classContent.append("final ");
		}
		classContent.append(this.classType+" ");
		classContent.append(alias);
		if(StringUtils.isNotBlank(this.Super)){
			classContent.append(" extends "+Super+" ");
		}
		if(this.Implements.size() >0){
			for(int i=0; i<Implements.size(); ++i){
				String implement = this.Implements.get(i);
				implement = NameUtils.getShortName(implement);
				if(i == 0){
					classContent.append(" implements ");
					classContent.append(implement);
				}else{
					classContent.append(",");
					classContent.append(implement);
				}
			}
		}
		classContent.append("{");
		classContent.append("\n");
		//属性
		if(classType == ClassType.CLASS){
			if(this.properties.size() >0){
				for(PropertyModel property : properties){
					classContent.append(property.toCode());
				}
			}
		}else if(classType == ClassType.ENUM){
			//按枚举类的格式  prop1,prop2;
			classContent.append("\t");
			if(this.properties.size() >0){
				for(int i=0; i<properties.size(); ++i){
					PropertyModel property = properties.get(i);
					if(i == 0){
						classContent.append(property.getPropertyName());
					}else{
						classContent.append(","+property.getPropertyName());
					}
					if(i == properties.size() -1){
						classContent.append(";\n");
					}
				}
			}
		}
		
		classContent.append("\n");
		//方法
		if(this.methods.size() >0){
			for(MethodModel method : methods){
				if((this.Abstract && method.isAbstract()) || classType == ClassType.INTERFACE){
					method.setPrintContents(false);
				}else{
					method.setPrintContents(true);
				}
				classContent.append(method.toCode());
			}
		}
		//类结尾
		classContent.append("}");
		return classContent.toString();
	}

	/**
	 * 
	 * @Title	getCommentCode 
	 * @Description	获取类注释代码
	 * @return StringBuffer
	 */
	private StringBuffer getCommentCode() {

		StringBuffer sb = new StringBuffer();
		sb.append("/**\n");
		sb.append(" *\n");
		
		sb.append(" * @ClassName: "+this.alias+"\n");
		if(this.descriptions != null && this.descriptions.size() >0){
			for(int i=0; i<this.descriptions.size(); ++i){
				String description = this.descriptions.get(i);
				if(i == 0){
					sb.append(" * @Description: "+ StringUtils.nvl(description)+"\n");
				}else{
					sb.append(" * "+StringUtils.nvl(description));
				}
			}
		}
		sb.append(" * @author "+StringUtils.nvl(this.author)+"\n");
		sb.append(" * @date "+DateUtils.format(new Date())+"\n");
		
		sb.append(" *\n");
		sb.append(" */");
		
		return sb;
	}

	/**
	 * 
	 * @Title	addGetterSetter 
	 * @Description	根据propertyModel的内容 添加Getter Setter方法到methods中
	 * @param methods
	 * @param property void
	 */
	private void addGetterSetter(PropertyModel property) {
		String alias = NameUtils.getPropertyType(property.getJavaType());
		String propertyName = property.getPropertyName();
		String upperName = NameUtils.upperCaseStart(propertyName);
		String javaType = property.getJavaType();
		
		//Getter方法
		MethodModel getter = new MethodModel();
		getter.setAccessPermission(AccessPermission.PUBLIC);
		if(property.isCollection()){
			getter.setResultJavaType(List.class.getName()+"<"+alias+">");
		}else{
			getter.setResultJavaType(javaType);
		}
		getter.setIndentionLevel(1);
		//布尔类型方法名要用isXXX()
		if(alias.toLowerCase().equals("boolean")){
			getter.setMethodName("is"+upperName);
		}else{
			getter.setMethodName("get"+upperName);
		}
		//方法内容
		getter.addContent("return this."+propertyName+";",2);
		getter.addDescription("返回"+propertyName+"的值");
		
		
		//Setter方法
		MethodModel setter = new MethodModel();
		setter.setAccessPermission(AccessPermission.PUBLIC);
		setter.setResultJavaType("void");
		setter.setMethodName("set"+upperName);
		//添加参数
		if(property.isCollection()){
			setter.addParam("java.util.List<"+alias+">",propertyName);
		}else{
			setter.addParam(javaType, propertyName);
		}
		//方法内容
		setter.addContent("this."+propertyName+" = "+propertyName+";",2);
		setter.addDescription("设置"+propertyName+"的值");
		setter.setIndentionLevel(1);
		this.methods.add(getter);
		this.methods.add(setter);
		
	}


	public ClassType getClassType() {
		return classType;
	}

	public void setClassType(ClassType classType) {
		this.classType = classType;
	}

	public AccessPermission getAccessPermission() {
		return accessPermission;
	}

	public void setAccessPermission(AccessPermission accessPermission) {
		this.accessPermission = accessPermission;
	}

	public boolean isAbstract() {
		return Abstract;
	}

	public void setAbstract(boolean abstract1) {
		Abstract = abstract1;
	}

	public boolean isFinal() {
		return Final;
	}

	public void setFinal(boolean final1) {
		Final = final1;
	}

	public String getPacket() {
		return packet;
	}

	public void setPacket(String packet) {
		this.packet = packet;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSuper() {
		return Super;
	}

	public void setSuper(String super1) {
		Super = super1;
	}

	public List<String> getImplements() {
		return Implements;
	}

	public void setImplements(List<String> implements1) {
		Implements = implements1;
	}

	public List<PropertyModel> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyModel> properties) {
		this.properties = properties;
	}

	public List<MethodModel> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodModel> methods) {
		this.methods = methods;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}


	
}
