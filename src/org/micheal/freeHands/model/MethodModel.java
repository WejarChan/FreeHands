package org.micheal.freeHands.model;

import java.util.ArrayList;
import java.util.List;

import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.StringUtils;

/**
 * 
 * @ClassName: MethodModel 
 * @Description: 用于生产java类用的。方法的模型
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-21 下午11:19:51 
 *
 */
public class MethodModel {
	
	//访问权限 public protected private default(不写)
	private AccessPermission accessPermission = AccessPermission.PUBLIC;
	//是否静态
	private boolean Static = false;
	//是否final
	private boolean Final = false;
	//是否抽象
	private boolean Abstract = false;
	//返回值类型
	private String resultJavaType;
	//方法名
	private String methodName;
	//参数类型 全限定名
	private List<String> paramJavaType;
	//参数名
	private List<String> paramName;
	//方法描述
	private List<String> descriptions;
	//缩进等级
	private int indentionLevel = 1;
	//方法内容
	private List<String> contents;
	//内容的缩进等级
	private List<Integer> contentIndentionLevel;
	//是否输出方法体
	private boolean printContents = true;
	//要引用的类
	private List<String> imports ;
	
	/**
	 * 
	 * @Title	addDescription 
	 * @Description	添加一行描述
	 * @param description void
	 */
	public void addDescription(String description){
		this.descriptions.add(description);
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
	 * <p>Title: MethodModel</p> 
	 * <p>Description: 默认构造方法。实例化3个复杂属性</p>
	 */
	public MethodModel(){
		this.paramJavaType = new ArrayList<String>();
		this.paramName = new ArrayList<String>();
		this.contents = new ArrayList<String>();
		this.contentIndentionLevel = new ArrayList<Integer>();
		this.imports = new ArrayList<String>();
		this.descriptions = new ArrayList<String>();
	}
	
	
	/**
	 * 
	 * @Title	addContent 
	 * @Description	添加一行方法内容
	 * @param content
	 * @param level void
	 */
	public void addContent(String content,int level){
		this.contents.add(content);
		this.contentIndentionLevel.add(level);
	}
	
	/**
	 * 
	 * @Title	addContent 
	 * @Description	添加一行内容
	 * @param content void
	 */
	public void addContent(String content){
		addContent(content,this.indentionLevel);
	}
	
	/**
	 * 
	 * @Title	addParam 
	 * @Description	添加一个参数 
	 * @param javaType
	 * @param name void
	 */
	public void addParam(String javaType,String name){
		this.paramJavaType.add(javaType);
		this.paramName.add(name);
	}
	
	
	/**
	 * 
	 * @Title	toCode
	 * @Description	返回这个方法的代码表现形式
	 * @return String
	 */
	public String toCode(){
		StringBuffer sb = new StringBuffer();
		//注释
		indention(sb, indentionLevel);
		sb.append("/**");
		sb.append("\n");
		indention(sb, indentionLevel);
		sb.append(" *");
		sb.append("\n");
		indention(sb, indentionLevel);
		sb.append(" * @Title "+this.methodName);
		sb.append("\n");
		for(int i=0; i<this.descriptions.size(); ++i){
			String description = this.descriptions.get(i);
			if(i == 0){
				indention(sb, indentionLevel);
				sb.append(" * @Description ");
			}else{
				indention(sb, indentionLevel);
				sb.append(" * ");
			}
			sb.append(description);
			sb.append("\n");
		}
		if(paramName!=null && paramName.size()>0){
			for(String name : paramName){
				indention(sb, indentionLevel);
				sb.append(" * @param "+name);
				sb.append("\n");
			}
		}
		indention(sb, indentionLevel);
		sb.append(" * @return "+NameUtils.getShortName(this.resultJavaType));
		sb.append("\n");
		indention(sb, indentionLevel);
		sb.append(" */");
		sb.append("\n");
		indention(sb, indentionLevel);
		//访问修饰符
		if(null != this.accessPermission){
			sb.append(this.accessPermission+" ");
		}
		//修饰符
		if(this.Abstract){
			sb.append("abstract ");
		}
		if(this.Static){
			sb.append("static ");
		}
		if(this.Final){
			sb.append("final ");
		}
		sb.append(NameUtils.getShortName(resultJavaType)+" ");
		//特殊情况----构造方法没有方法名
		if(StringUtils.isNotBlank(this.methodName)){
			sb.append(this.methodName+"(");
		}
		//参数列表
		if(paramName != null && paramName.size()>0){
			for(int i=0; i<paramName.size(); ++i){
				String alias = NameUtils.getShortName(paramJavaType.get(i));
				sb.append(alias+" "+paramName.get(i));
				if(i<paramName.size()-1){
					sb.append(",");
				}
			}
		}
		sb.append(")");
		if(this.printContents){
			sb.append("{");
			sb.append("\n");
			sb.append("\n");
			if(contents != null && contents.size()>0){
				for(int i=0;i<contents.size();++i){
					String content = contents.get(i);
					int level = contentIndentionLevel.get(i);
					indention(sb, level);
					sb.append(content);
					sb.append("\n");
				}
			}
				
			sb.append("\n");
			indention(sb, indentionLevel);
			sb.append("}");
		}else{
			sb.append(";");
			sb.append("\n");
		}
		//空一行
		sb.append("\n");
		return sb.toString();
	}
	
	/**
	 * 
	 * @Title	indention 
	 * @Description	行首缩进
	 * @param sb
	 * @param level void
	 */
	private void indention(StringBuffer sb,int level){
		while(level-- >0){
			sb.append("\t");
		}
	}
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<String> getParamJavaType() {
		return paramJavaType;
	}
	public void setParamJavaType(List<String> paramJavaType) {
		this.paramJavaType = paramJavaType;
	}
	public List<String> getParamName() {
		return paramName;
	}
	public void setParamName(List<String> paramName) {
		this.paramName = paramName;
	}

	public List<String> getContents() {
		return contents;
	}

	public void setContents(List<String> contents) {
		this.contents = contents;
	}

	public String getResultJavaType() {
		return resultJavaType;
	}

	public void setResultJavaType(String resultJavaType) {
		this.resultJavaType = resultJavaType;
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


	public boolean isAbstract() {
		return Abstract;
	}


	public void setAbstract(boolean abstract1) {
		Abstract = abstract1;
	}


	public List<String> getDescriptions() {
		return descriptions;
	}


	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}


	public int getIndentionLevel() {
		return indentionLevel;
	}


	public void setIndentionLevel(int indentionLevel) {
		this.indentionLevel = indentionLevel;
	}


	public List<Integer> getContentIndentionLevel() {
		return contentIndentionLevel;
	}


	public void setContentIndentionLevel(List<Integer> contentIndentionLevel) {
		this.contentIndentionLevel = contentIndentionLevel;
	}


	public boolean isPrintContents() {
		return printContents;
	}


	public void setPrintContents(boolean printContents) {
		this.printContents = printContents;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

}
