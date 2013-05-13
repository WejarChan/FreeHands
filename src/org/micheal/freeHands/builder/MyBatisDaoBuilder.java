package org.micheal.freeHands.builder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.micheal.freeHands.model.AccessPermission;
import org.micheal.freeHands.model.ClassModel;
import org.micheal.freeHands.model.ClassType;
import org.micheal.freeHands.model.MethodModel;
import org.micheal.freeHands.model.PropertyModel;
import org.micheal.freeHands.model.TableModel;
import org.micheal.freeHands.util.IOUtils;
import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.PathUtils;

/**
 * 
* @ClassName: MyBatisDaoBuilder 
* @Description: 用于生成mybatis的dao文件
* @author Micheal_Chan 553806198@qq.com 
* @date 2013-4-19 下午5:14:41 
*
 */
public class MyBatisDaoBuilder extends Builder {

	@Override
	protected void buildSomething() throws Exception {
		for (TableModel table : this.getTableModelList()) {
			buildDao(table);
		}
	}

	private void buildDao(TableModel table) throws IOException {
		String alias = table.getAlias();
		String interfacePacket = table.getDaoTargetPacket();
		String interfaceAlias = NameUtils.getDaoInterfaceName(alias);
		String implPacket = interfacePacket+".impl";
		String implAlias = NameUtils.getdaoImplName(alias);
		
		File interfacePacketFile = PathUtils.getPakcetFile(this.getConfig().getTargetProject(), interfacePacket);
		File implPacketFile = PathUtils.getPakcetFile(this.getConfig().getTargetProject(), implPacket);
		//不存在的包,创建
		if (!implPacketFile.exists()) {
			implPacketFile.mkdirs();
		}
		if (!interfacePacketFile.exists()) {
			interfacePacketFile.mkdirs();
		}

		File interfaceFile = new File(interfacePacketFile.getAbsolutePath() + "/" + interfaceAlias + ".java");
		File implFile = new File(implPacketFile.getAbsolutePath() + "/" + implAlias + ".java");
		
		ClassModel implModel = null;
		ClassModel interfaceModel = null;
		
		if(!interfaceFile.exists()){
			//引用传递。改变量名而已
			interfaceModel = getNewDaoModel(table);
			//清空imported和gettersetter
			
			interfaceModel.setAccessPermission(AccessPermission.PUBLIC);
			interfaceModel.setClassType(ClassType.INTERFACE);
			interfaceModel.setAlias(interfaceAlias);
			interfaceModel.setPacket(interfacePacket);
			interfaceModel.setSuper(null);//无接口父接口的配置
			
			IOUtils.write2File(interfaceFile, interfaceModel.toCode(), getCharset());
			System.out.println(interfaceFile.getAbsolutePath()+" build !");
		}else{
			System.out.println("File "+interfaceFile.getAbsolutePath()+" is exists!Please check up!");
			// TODO 文件已经存在。需要记录？
		}
		
		if (!implFile.exists()) {
			implModel = getNewDaoModel(table);
			implModel.addImplement(interfacePacket+"."+interfaceAlias);
			IOUtils.write2File(implFile, implModel.toCode(), getCharset());
			System.out.println(implFile.getAbsolutePath()+" build !");
			
		} else {
			System.out.println("File "+implFile.getAbsolutePath()+" is exists!Please check up!");
			// TODO 文件已经存在。需要记录？
		}
		
	}

	/**
	 * 
	 * @Title	getNewDaoModel 
	 * @Description	返回一个dao实现类的ClassModel
	 * @param table
	 * @return ClassModel
	 */
	private ClassModel getNewDaoModel(TableModel table) {
		String alias = table.getAlias();
		String interfacePacket = table.getDaoTargetPacket();
		String implPacket = interfacePacket+".impl";
		String daoSuperClass = table.getDaoSuperClass();
		String mapperName = NameUtils.getMapperName(alias);
		String sqlMapPacket = table.getSqlMapTargetPacket();
		String mapperPropertyName = NameUtils.lowerCaseStart(mapperName);
		String implAlias = NameUtils.getdaoImplName(alias);
		
		ClassModel implModel = new ClassModel();
		implModel.setAuthor(getConfig().getAuthor());
		implModel.setAccessPermission(AccessPermission.PUBLIC);
		implModel.setClassType(ClassType.CLASS);
		implModel.setAlias(implAlias);
		implModel.setPacket(implPacket);
		implModel.setSuper(daoSuperClass);
		//mapper 的属性
		PropertyModel property = new PropertyModel();
		property.setJavaType(sqlMapPacket+"."+mapperName);
		property.setPropertyName(mapperPropertyName);
		property.setComment(alias+"映射的mapper");
		implModel.addProperty(property);

		//方法
		String paramName = NameUtils.lowerCaseStart(alias);
		MethodModel add = new MethodModel();
		add.setAccessPermission(AccessPermission.PUBLIC);
		add.setResultJavaType("int");
		add.setMethodName("add");
		add.addParam(table.getJavaType(), paramName);
		add.addContent("return "+mapperPropertyName+"."+"insert"+alias+"("+paramName+");",2);
		add.addDescription("根据"+alias+"的属性。往数据表"+table.getTableName()+"插入一条新记录");
		implModel.addMethod(add);
		
		MethodModel delete = new MethodModel();
		delete.setAccessPermission(AccessPermission.PUBLIC);
		delete.setResultJavaType("int");
		delete.setMethodName("delete");
		delete.addParam(table.getJavaType(), paramName);
		delete.addContent("return "+mapperPropertyName+"."+"delete"+alias+"("+paramName+");",2);
		delete.addDescription("根据"+alias+"的属性,删除数据表"+table.getTableName()+"一条记录");
		implModel.addMethod(delete);
		
		MethodModel update = new MethodModel();
		update.setAccessPermission(AccessPermission.PUBLIC);
		update.setResultJavaType("int");
		update.setMethodName("update");
		update.addParam(table.getJavaType(), paramName);
		update.addContent("return "+mapperPropertyName+"."+"update"+alias+"("+paramName+");",2);
		update.addDescription("根据"+alias+"的主键,更新数据表"+table.getTableName()+"一条记录");
		implModel.addMethod(update);
		
		if(table.getAssociations().size() > 0 || table.getCollections().size() > 0){
			MethodModel query = new MethodModel();
			query.setAccessPermission(AccessPermission.PUBLIC);
			query.setResultJavaType(List.class.getName()+"<"+alias+">");
			query.setMethodName("query");
			query.addParam(table.getJavaType(), paramName);
			//第二个参数用于判断使用那种查询方式
			query.addParam("boolean", "join");
			//方法体
			query.addContent("if(join){",2);
			query.addContent("return "+mapperPropertyName+"."+NameUtils.getJoinSelectName(alias)+"("+paramName+");",3);
			query.addContent("}else{",2);
			query.addContent("return "+mapperPropertyName+"."+NameUtils.getNestedSelectName(alias)+"("+paramName+");",3);
			query.addContent("}",2);
			String description = "根据参数"+paramName+"的属性做条件,查询数据表"+table.getTableName()+"的记录。";
			query.addDescription(description);
			description = "若join为true则采用表连接查询复杂属性.否则采用子查询方式查询";
			query.addDescription(description);
			implModel.addMethod(query);
			
			//分页查询方法
			String pagePacket = "com."+getConfig().getTargetProject().toLowerCase()+".base.pagination";
			String paginationContextJavaType = pagePacket +".PaginationContext";
			String paginationResultJavaType = pagePacket +".PaginationResult";
			String paginationInfoJavaType = pagePacket+".PaginationInfo";
			String pageInfoParamName = "pageInfo";
			String pageResultParamName = "pageResult";
			
			MethodModel queryPage = new MethodModel();
			queryPage.setAccessPermission(AccessPermission.PUBLIC);
			queryPage.setResultJavaType(paginationResultJavaType+"<"+alias+">");
			queryPage.setMethodName("query");
			queryPage.addParam(table.getJavaType(), paramName);
			//第二参数是分页信息
			queryPage.addParam(paginationInfoJavaType, pageInfoParamName);
			//第三个参数用于判断使用那种查询方式
			queryPage.addParam("boolean", "join");
			//方法体内容
			queryPage.addImport(paginationContextJavaType);
			queryPage.addContent("PaginationContext.set("+pageInfoParamName+");", 2);
			queryPage.addContent("PaginationResult<"+alias+"> "+pageResultParamName+" = new PaginationResult<"+alias+">();",2);
			queryPage.addContent(pageResultParamName+".setData(query("+paramName+",join));", 2);
			queryPage.addContent(pageResultParamName+".setPaginationInfo("+pageInfoParamName+");",2);
			queryPage.addContent("return "+pageResultParamName+";", 2);
			queryPage.addDescription("此方法是分页查询方法。根据"+pageInfoParamName+"中的参数!查询数据表");
			implModel.addMethod(queryPage);
		}
		
		MethodModel query = new MethodModel();
		query.setAccessPermission(AccessPermission.PUBLIC);
		query.setResultJavaType(List.class.getName()+"<"+alias+">");
		query.setMethodName("query");
		query.addParam(table.getJavaType(), paramName);
		String description = null;
		//方法体
		if(table.getAssociations().size() > 0 || table.getCollections().size() > 0){
			query.addContent("return "+"query"+"("+paramName+","+"false"+");",2);
			description =  "根据参数"+paramName+"的属性做条件,查询数据表"+table.getTableName()+"的记录。采用嵌套查询方式查询复杂属性";
		}else{
			query.addContent("return "+mapperPropertyName+"."+NameUtils.getNestedSelectName(alias)+"("+paramName+");",2);
			description = "根据参数"+paramName+"的属性做条件,查询数据表"+table.getTableName()+"的记录。";
		}
		query.addDescription(description);
		implModel.addMethod(query);
		
		//分页查询方法
		String pagePacket = "com."+getConfig().getTargetProject().toLowerCase()+".base.pagination";
		String paginationContextJavaType = pagePacket +".PaginationContext";
		String paginationResultJavaType = pagePacket +".PaginationResult";
		String paginationInfoJavaType = pagePacket+".PaginationInfo";
		String pageInfoParamName = "pageInfo";
		String pageResultParamName = "pageResult";
		
		MethodModel queryPage = new MethodModel();
		queryPage.setAccessPermission(AccessPermission.PUBLIC);
		queryPage.setResultJavaType(paginationResultJavaType+"<"+alias+">");
		queryPage.setMethodName("query");
		queryPage.addParam(table.getJavaType(), paramName);
		//第二参数是分页信息
		queryPage.addParam(paginationInfoJavaType, pageInfoParamName);
		//第三个参数用于判断使用那种查询方式
		//方法体内容
		queryPage.addImport(paginationContextJavaType);
		queryPage.addContent("PaginationContext.set("+pageInfoParamName+");", 2);
		queryPage.addContent("PaginationResult<"+alias+"> "+pageResultParamName+" = new PaginationResult<"+alias+">();",2);
		queryPage.addContent(pageResultParamName+".setData(query("+paramName+"));", 2);
		queryPage.addContent(pageResultParamName+".setPaginationInfo("+pageInfoParamName+");",2);
		queryPage.addContent("return "+pageResultParamName+";", 2);
		queryPage.addDescription("此方法是分页查询方法。根据"+pageInfoParamName+"中的参数!查询数据表");
		implModel.addMethod(queryPage);
		
		return implModel;
	}

}
