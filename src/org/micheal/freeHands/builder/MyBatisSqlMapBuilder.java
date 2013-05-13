package org.micheal.freeHands.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultDocument;
import org.micheal.freeHands.bean.ConfigurationBean;
import org.micheal.freeHands.bean.JdbcConfigurationBean;
import org.micheal.freeHands.model.AccessPermission;
import org.micheal.freeHands.model.ClassModel;
import org.micheal.freeHands.model.ClassType;
import org.micheal.freeHands.model.MethodModel;
import org.micheal.freeHands.model.PropertyModel;
import org.micheal.freeHands.model.TableModel;
import org.micheal.freeHands.util.IOUtils;
import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.PathUtils;
import org.micheal.freeHands.util.StringUtils;

/**
 * 
* @ClassName: MyBatisSqlMapBuilder 
* @Description: 用于生成mybatis的Mapper接口和sqlmap文件
* @author Micheal_Chan 553806198@qq.com 
* @date 2013-4-19 下午5:17:24 
*
 */
public class MyBatisSqlMapBuilder extends Builder {

	@Override
	protected void buildSomething() throws Exception {
		SAXReader reader = new SAXReader();
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(this.getCharset());
		XMLWriter writer = null;
		
		ConfigurationBean config = this.getConfig();
		File configurationFile = PathUtils.getFile(config.getTargetProject(), null, config.getConfigurationFile());
		Document doc = null;
		if(PathUtils.checkFileExist(configurationFile)){
			doc = reader.read(configurationFile);
		}else{
			doc = newConfigurationFile();
		}
		
		//根据TableModel 创建映射文件
		for(TableModel table : this.getTableModelList()){
			//创建sqlMap映射文件
			buildSqlMap(table,format);
			//创建Mapper接口
			buildMapper(table);
			//主配置文件中添加Alias和Mapper
			addAlias(doc,table);
			addMapper(doc,table);
		}
		
		writer = new XMLWriter(new FileWriter(configurationFile),format);
		writer.write(doc);
		writer.flush();
		writer.close();
		//添加各个元素的内容
		
	}

	/**
	 * 
	 * @Title	buildMapper 
	 * @Description	创建一个Mapper接口
	 * @param table void
	 * @throws IOException 
	 */
	private void buildMapper(TableModel table) throws IOException {
		String packet = table.getSqlMapTargetPacket();
		String alias = table.getAlias();
		String propertyType = NameUtils.lowerCaseStart(alias);
		String fileName = NameUtils.getMapperName(alias);
		String project = this.getConfig().getTargetProject();
		String charset = getCharset();
		
		File packetFile = PathUtils.getPakcetFile(project, packet);
		//不存在则创建文件夹
		if(!PathUtils.checkFileExist(packetFile)){
			packetFile.mkdirs();
		}
		
		File mapperFile = PathUtils.getFile(project, packet, fileName+".java");
		//不存在
		if(!mapperFile.exists()){
			ClassModel classModel = new ClassModel();
			classModel.setAuthor(getConfig().getAuthor());
			classModel.setAccessPermission(AccessPermission.PUBLIC);
			classModel.setClassType(ClassType.INTERFACE);
			classModel.setPacket(packet);
			classModel.setAlias(NameUtils.getMapperName(alias));
			List<MethodModel> methods = classModel.getMethods();
			
			MethodModel insert = new MethodModel();
			insert.setAccessPermission(AccessPermission.PUBLIC);
			insert.setResultJavaType("int");
			insert.setMethodName("insert"+alias);
			insert.addParam(table.getJavaType(), propertyType);
			insert.addDescription("向数据表"+table.getTableName()+"中插入一条"+alias+"记录");
			methods.add(insert);
			
			MethodModel delete = new MethodModel();
			delete.setAccessPermission(AccessPermission.PUBLIC);
			delete.setResultJavaType("int");
			delete.setMethodName("delete"+alias);
			delete.addParam(table.getJavaType(), propertyType);
			delete.addDescription("从数据表"+table.getTableName()+"中,根据传入参数做条件,删除一条"+alias+"记录");
			methods.add(delete);
			
			MethodModel update = new MethodModel();
			update.setAccessPermission(AccessPermission.PUBLIC);
			update.setResultJavaType("int");
			update.setMethodName("update"+alias);
			update.addParam(table.getJavaType(), propertyType);
			update.addDescription("从数据表"+table.getTableName()+"中,根据Id,更新一条"+alias+"记录");
			methods.add(update);
			
			MethodModel nestedSelect = new MethodModel();
			nestedSelect.setAccessPermission(AccessPermission.PUBLIC);
			nestedSelect.setResultJavaType(List.class.getName()+"<"+alias+">");
			nestedSelect.setMethodName(NameUtils.getNestedSelectName(alias));
			nestedSelect.addParam(table.getJavaType(),propertyType);
			nestedSelect.addDescription("根据传入的参数作为条件,从数据库查询出符合条件的记录,复杂对象用子查询方式查询");
			methods.add(nestedSelect);
			
			//若有复杂属性 需要做unionSelect 连表查询
			if(table.getAssociations().size() > 0 || table.getCollections().size() > 0){
				MethodModel unionSelect = new MethodModel();
				unionSelect.setAccessPermission(AccessPermission.PUBLIC);
				unionSelect.setResultJavaType(List.class.getName()+"<"+alias+">");
				unionSelect.setMethodName(NameUtils.getJoinSelectName(alias));
				unionSelect.addParam(table.getJavaType(),propertyType);
				unionSelect.addDescription("根据传入的参数作为条件,从数据库查询出符合条件的记录,采用连表查询的方式");
				methods.add(unionSelect);
			}
			
			IOUtils.write2File(mapperFile, classModel.toCode(), charset);
			System.out.println(mapperFile.getAbsolutePath()+" build !");
		}else{
			System.out.println("File "+mapperFile.getAbsolutePath()+" is exists!Please check up!");
			//TODO 已经存在，要做记录
		}
		
	}

	/**
	 * 
	 * @Title	addMapper 
	 * @Description	往配置文件中的Mappers添加一个mapper元素 
	 * @param doc
	 * @param table void
	 */
	private void addMapper(Document doc, TableModel table) {
		Element configuration = doc.getRootElement();
		Element mappers = configuration.element("mappers");
		String alias = table.getAlias();
		String packetPath = PathUtils.getPacketPath(table.getSqlMapTargetPacket());
		String resource = packetPath+"/"+NameUtils.getSqlMapFileName(alias);
		//检查是否重复
		Iterator<Element> aliasIt = mappers.elementIterator("mapper");
		while(aliasIt.hasNext()){
			Element ele = aliasIt.next();
			if(ele.attributeValue("resource").equals(resource)){
				//重复则不添加
				//TODO 是否要做记录
				return ;
			}
		}
		String[] attrs = {"resource"};
		String[] values = {resource};
		mappers.add(newElement("mapper",attrs,values));
		
	}

	/**
	 * 
	 * @Title	addAlias 
	 * @Description	往配置文件中的typeAliases添加一个typeAlias元素
	 * @param doc
	 * @param table void
	 */
	private void addAlias(Document doc, TableModel table) {
		Element configuration = doc.getRootElement();
		Element typeAliases = configuration.element("typeAliases");
		String alias = table.getAlias();
		String type = table.getJavaType();
		//检查是否重复
		Iterator<Element> aliasIt = typeAliases.elementIterator("typeAlias");
		while(aliasIt.hasNext()){
			Element ele = aliasIt.next();
			if(ele.attributeValue("type").equals(type)){
				//重复则不添加
				//TODO 是否要做记录
				return ;
			}
		}
		
		String[] attrs = {"alias","type"};
		String[] values = {alias,type};
		typeAliases.add(newElement("typeAlias",attrs,values));
	}

	/**
		 * 
		 * @Title	buildSqlMap 
		 * @Description	根据tableModel,和format（编码格式）创建一个sqlMap文件。文件以${alias}_SqlMap.xml命名
		 * @param table
		 * @param format
		 * @throws IOException void
		 */
		private void buildSqlMap(TableModel table, OutputFormat format) throws IOException {
			String project = this.getConfig().getTargetProject();
			String packet = table.getSqlMapTargetPacket();
			String alias = table.getAlias();
			String fileName = NameUtils.getSqlMapFileName(alias);
			File sqlMapFile = PathUtils.getFile(project, table.getSqlMapTargetPacket(), fileName);
			if(!PathUtils.checkFileExist(sqlMapFile)){
				if(!PathUtils.checkFileExist(sqlMapFile.getParentFile())){
					sqlMapFile.getParentFile().mkdirs();
				}
				//添加dtd格式验证
	//			<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd"> 
				DocumentFactory documentFactory = DocumentFactory.getInstance();
				String name = "mapper";
				String publicId = "-//mybatis.org//DTD Mapper 3.0//EN";
				String systemId = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
				DocumentType docType = documentFactory.createDocType(name, publicId, systemId);
				Document doc = new DefaultDocument(docType);
				//添加sqlMap文件主要内容 mapper元素
				Element mapper = DocumentHelper.createElement("mapper");
				//namespace = Mapper类的全限定名
				String namespace = packet+"."+NameUtils.getMapperName(alias);
				mapper.addAttribute("namespace", namespace);
				
				//若有复杂属性 需要做unionSelect 连表查询
				if(table.getAssociations().size() > 0 || table.getCollections().size() > 0){
					//连表查询需要用到的表别名集合
					List<String> tableAliases = getTableAliases(table);
					mapper.addComment("连表查询结果集和子结果集");
					newJoinResultMap(table,mapper,tableAliases);
					mapper.addComment("连表查询动态sql语句");
					mapper.add(newJoinSelect(table,tableAliases));
				}
				
				//添加ResultMap   连表查询结果、子查询结果
				mapper.addComment("嵌套查询(子查询)结果集和子查询");
				addNewNestedResultMap(table,mapper);
				//添加select 连表查询byId、普通查询 byId、连表条件查询、普通条件查询、分页查询
				mapper.addComment("嵌套查询动态sql语句");
				mapper.add(newNestedSelect(table));
				//添加insert
				mapper.addComment("插入动态sql语句");
				mapper.add(newInsert(table));
				//添加update
				mapper.addComment("更新动态sql语句");
				mapper.add(newUpdate(table));
				//添加delete
				mapper.addComment("删除动态sql语句");
				mapper.add(newDelete(table));
				
				doc.add(mapper);
				XMLWriter writer = new XMLWriter(new FileWriter(sqlMapFile),format);
				writer.write(doc);
				writer.flush();
				writer.close();
				System.out.println(sqlMapFile.getAbsolutePath()+" build !");
			}else{
				System.out.println("File "+sqlMapFile.getAbsolutePath()+" is exists!Please check up!");
				//TODO 文件已经存在。跳过，并做记录
			}
		}

	/**
	 * 
	 * @Title	newJoinSelect 
	 * @Description	创建一个连表查询的select语句
	 * @param table
	 * @param tableAliases 
	 * @return Element
	 */
	private Element newJoinSelect(TableModel table, List<String> tableAliases) {
		String alias = table.getAlias();
		
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		attrs.add("id");
		attrs.add("parameterType");
		attrs.add("resultMap");
		values.add(NameUtils.getJoinSelectName(alias));
		values.add(alias);
		values.add(NameUtils.getJoinResultMapName(alias));
		
		Element unionSelect = newElement("select",attrs.toArray(),values.toArray());
		
		int index = 0;
		String tableAlias = tableAliases.get(index++);
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		//普通属性
		List<PropertyModel> properties = table.getSimpleProperties();
		for(int i=0; i<properties.size(); ++i){
			PropertyModel property = properties.get(i);

			String columnName = property.getColumnName();
			// si.id as si_id 这种格式的
			sql.append(tableAlias+"."+columnName+" as "+tableAlias+"_"+columnName);
			if(i<properties.size()-1){
				sql.append(",");
			}
		}
		//复杂属性
		List<PropertyModel> complexProperties = new ArrayList<PropertyModel>();
		complexProperties.addAll(table.getAssociations());
		complexProperties.addAll(table.getCollections());
		for(int i=0; i<complexProperties.size(); ++i){
			PropertyModel complexProperty = complexProperties.get(i);
			tableAlias = tableAliases.get(index++);
			//若存在关系表,则跳过关系表别名
			if(StringUtils.isNotBlank(complexProperty.getRelTableName())){
				tableAlias = tableAliases.get(index++);
			}
			for(PropertyModel property : complexProperty.getRefProperties()){
				sql.append(",");
				String columnName = property.getColumnName();
				// si.id as si_id 这种格式的
				sql.append(tableAlias+"."+columnName+" as "+tableAlias+"_"+columnName);
			}
		}
		sql.append(" from ");
		index = 0;	//清零。重新拿到主表别名
		tableAlias = tableAliases.get(index++);
		sql.append(table.getTableName()+" "+tableAlias);
		//这里需要写join 连接
		for(PropertyModel property : complexProperties){
			sql.append(" left join ");
			String relTableAlias = null;
			String refTableAlias = null;
			String [] keys = property.getKeys();
			String [] relKeys = property.getRelKeys();
			String [] relRefKeys = property.getRelRefKeys();
			String [] refKeys = property.getRefKeys();
			if(StringUtils.isNotBlank(property.getRelTableName())){
				//主表和关系表的join
				relTableAlias = tableAliases.get(index++);
				refTableAlias = tableAliases.get(index++);
				sql.append(property.getRelTableName()+" "+relTableAlias);
				sql.append(" on ");
				for(int i=0; i<keys.length; ++i){
					sql.append(tableAlias+"."+keys[i]+" = "+relTableAlias+"."+relKeys[i]+" ");
					if(i<keys.length-1){
						sql.append("and ");
					}
				}
				//关系表和关联表的join
				sql.append("left join ");
				sql.append(property.getRefTableName()+" "+refTableAlias);
				sql.append(" on ");
				for(int i=0; i<relRefKeys.length; ++i){
					sql.append(relTableAlias+"."+relRefKeys[i]+" = "+refTableAlias+"."+refKeys[i]);
				}
				
			}else{
				//主表和关联表的join
				refTableAlias = tableAliases.get(index++);
				sql.append(property.getRefTableName()+" "+refTableAlias);
				sql.append(" on ");
				for(int i=0; i<keys.length; ++i){
					sql.append(tableAlias+"."+keys[i]+" = "+refTableAlias+"."+refKeys[i]+" ");
					if(i<keys.length-1){
						sql.append("and ");
					}
				}
			}
		}
		
		//动态查询条件trim
		attrs = new ArrayList<String>();
		values = new ArrayList<String>();
		//trim元素
		attrs.add("prefix");
		attrs.add("prefixOverrides");
		values.add("where");
		values.add("and |or");
		Element trim = newElement("trim",attrs.toArray(),values.toArray());
		String[] testAttr = {"test"};
		String[] testValue = {""};
		for(PropertyModel property : properties){
			String propertyName = property.getPropertyName();
			String columnName = property.getColumnName();
			testValue[0] = property.getPropertyName()+" != null";
			String text = "and "+tableAlias+"."+columnName+" "+property.getLogic()+" #{"+propertyName+"} ";
			Element IF = newElement("if",testAttr,testValue);
			IF.addCDATA(text);
			trim.add(IF);
		}
		
		unionSelect.addCDATA(sql.toString());
		unionSelect.add(trim);
		
		return unionSelect;
	}

	/**
	 * 
	 * @Title	newJoinResultMap 
	 * @Description	创建一个连表查询的resultMap元素
	 * 				嵌套结果ResultMap
	 * @param table
	 * @param mapper 
	 * @param tableAliases
	 * @return Element
	 */
	private void newJoinResultMap(TableModel table, Element mapper, List<String> tableAliases) {
		String alias = table.getAlias();
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		//普通属性
		List<PropertyModel> simpleProperties = table.getSimpleProperties();
		//复杂属性
		List<PropertyModel> complexProperties = new ArrayList<PropertyModel>();
		complexProperties.addAll(table.getAssociations());
		complexProperties.addAll(table.getCollections());
		
		attrs.add("id");
		values.add(NameUtils.getJoinResultMapName(alias));
		attrs.add("type");
		values.add(alias);
		
		Element joinResultMap = newElement("resultMap",attrs.toArray(),values.toArray());
		mapper.add(joinResultMap);
		
		//主表表别名
		int index=0;
		String tableAlias = tableAliases.get(index++);
		//主表普通属性映射
		addNormalPropertyResult(joinResultMap,simpleProperties,tableAlias);
		//添加复杂属性结果映射
		addComplexPropertyResult(joinResultMap,complexProperties,tableAlias);
		//添加子结果集ResultMap
		for(PropertyModel proModel : complexProperties){
			tableAlias = tableAliases.get(index++);
			//若存在关系表名则跳过关系表名
			if(StringUtils.isNotBlank(proModel.getRelTableName())){
				tableAlias = tableAliases.get(index++);
			}
			mapper.add(newSubResultMap(proModel,tableAlias));				
		}
	}
	
	/**
	 * 
	 * @Title	addNormalPropertyResult 
	 * @Description	普通属性结果映射
	 * @param unionResultMap
	 * @param properties
	 * @param tableAlias void
	 */
	private void addNormalPropertyResult(Element unionResultMap,List<PropertyModel> properties, String tableAlias) {
		for(PropertyModel proModel : properties){
			List<String> attrs = new ArrayList<String>();
			List<String> values = new ArrayList<String>();
			
			attrs.add("property");
			attrs.add("column");
			values = new ArrayList<String>();
			values.add(proModel.getPropertyName());
			values.add(tableAlias+"_"+proModel.getColumnName());
			if(proModel.isPrimaryKey()){
				Element idEle = newElement("id",attrs.toArray(),values.toArray());
				unionResultMap.add(idEle);
			}else{
				Element result = newElement("result",attrs.toArray(),values.toArray());
				unionResultMap.add(result);
			}
		}
	}

	/**
	 * 
	 * @Title	addComplexPropertyResult 
	 * @Description	添加复杂属性结果映射
	 * @param unionResultMap
	 * @param complexes
	 * @param tableAlias void
	 */
	private void addComplexPropertyResult(Element unionResultMap,List<PropertyModel> complexes,String tableAlias) {
		
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		//复杂属性的result有4个属性
		attrs.add("property");
		attrs.add("column");
		attrs.add("javaType");
		attrs.add("resultMap");
				
		for(PropertyModel proModel : complexes){
			if(proModel.isCollection()){
				attrs.set(2, "ofType");
			}else{
				attrs.set(2, "javaType");
			}
			String refAlias = NameUtils.getShortName(proModel.getJavaType());
			values = new ArrayList<String>();
			values.add(proModel.getPropertyName());
			String[] keys = proModel.getKeys();
			//column = {key1,key2}
			String column = "{";
			for(int i=0; i<keys.length; ++i){
				if(StringUtils.isNotBlank(keys[i])){
					column += tableAlias+"_"+keys[i];
					if(i < keys.length-1){
						column += ",";
					}
				}
			}
			column += "}";
			values.add(column);
			values.add(refAlias);
			values.add(NameUtils.getSubResultMap(refAlias));
			String qname = proModel.isCollection()?"collection":"association";
			Element result = newElement(qname,attrs.toArray(),values.toArray());
			unionResultMap.add(result);
		}
	}

	/**
	 * 
	 * @Title	newSubResultMap 
	 * @Description	复杂属性用的子结果集SubResultMap
	 * @param proModel
	 * @param tableAlias
	 * @return Element
	 */
	private Element newSubResultMap(PropertyModel proModel, String tableAlias) {
		String refAlias = NameUtils.getShortName(proModel.getJavaType());
		String resultMapName = NameUtils.getSubResultMap(refAlias);
		
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		attrs.add("id");
		attrs.add("type");
		
		values.add(resultMapName);
		values.add(refAlias);
		
		Element subResultMap = newElement("resultMap",attrs.toArray(),values.toArray());
		//添加普通属性结果映射
		addNormalPropertyResult(subResultMap,proModel.getRefProperties(),tableAlias);
		
		return subResultMap;
	}

	/**
	 * 
	 * @Title	getTableAliases 
	 * @Description	获取连表查询需要用到的表别名。
	 * 				先是主表表别名。然后就是复杂属性关联表表别名。再是集合属性关联表表别名
	 * @param table
	 * @return List<String>
	 */
	private List<String> getTableAliases(TableModel table) {
		//先把所有的别名生成好
		List<String>tableAliases = new ArrayList<String>();
		//先添加主表别名
		tableAliases.add(NameUtils.getTableAlias(table.getTableName()));
		
		List<PropertyModel> complexProperties = new ArrayList<PropertyModel>();
		complexProperties.addAll(table.getAssociations());
		complexProperties.addAll(table.getCollections());
		
		//添加复杂属性引用的表的别名
		for(PropertyModel property : complexProperties){
			String tableAlias = null;
			//有关系表。先加关系表别名
			if(StringUtils.isNotBlank(property.getRelTableName())){
				tableAlias = NameUtils.getTableAlias(property.getRefTableName()); 
				//若已经有重复的表别名。则后来的别名再后面添加'_1'。以区分
				int i = 1;
				if(tableAliases.contains(tableAlias)){
					tableAlias += ("_"+i);
					while(tableAliases.contains(tableAlias)){
						tableAlias.substring(0, tableAlias.length()-1);
						++i;
						tableAlias += i;
					}
				}
				tableAliases.add(tableAlias);
			}
			tableAlias = NameUtils.getTableAlias(property.getRefTableName()); 
			//若已经有重复的表别名。则后来的别名再后面添加'_1'。以区分
			int i = 1;
			if(tableAliases.contains(tableAlias)){
				tableAlias += ("_"+i);
				while(tableAliases.contains(tableAlias)){
					tableAlias.substring(0, tableAlias.length()-1);
					++i;
					tableAlias += i;
				}
			}
			tableAliases.add(tableAlias);
		}
		return tableAliases;
	}

	/**
	 * 
	 * @Title	newNestedSelect 
	 * @Description	根据tableModel对象,创建一个简单的Select元素(单表查询)。
	 * 				复杂属性用懒惰加载(子查询)获得
	 * @param table
	 * @return Element
	 */
	private Element newNestedSelect(TableModel table) {
		String alias = table.getAlias();
		String id = NameUtils.getNestedSelectName(alias);
		String resultMap = NameUtils.getNestedResultMapName(alias);
		
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		attrs.add("id");
		attrs.add("parameterType");
		attrs.add("resultMap");
		
		values.add(id);
		values.add(alias);
		values.add(resultMap);
		
		Element nestedSelect = newElement("select",attrs.toArray(),values.toArray());
		StringBuffer sql = new StringBuffer();
		String tableAlias = NameUtils.getTableAlias(table.getTableName());
		sql.append("select ");
		//simpleSelect只查询简单属性
		List<PropertyModel> properties = table.getSimpleProperties();
		for(int i=0; i<properties.size(); ++i){
			PropertyModel property = properties.get(i);
			sql.append(tableAlias+"."+property.getColumnName());
			if(i<properties.size()-1){
				sql.append(",");
			}
		}
		sql.append(" from ");
		sql.append(table.getTableName()+" "+tableAlias);
		sql.append(" ");
		
		//查询条件
		attrs = new ArrayList<String>();
		values = new ArrayList<String>();
		//trim元素
		attrs.add("prefix");
		attrs.add("prefixOverrides");
		values.add("where");
		values.add("and |or");
		Element trim = newElement("trim",attrs.toArray(),values.toArray());
		String[] testAttr = {"test"};
		String[] testValue = {""};
		for(PropertyModel property : properties){
			String propertyName = property.getPropertyName();
			String columnName = property.getColumnName();
			testValue[0] = property.getPropertyName()+" != null";
			String text = "and "+tableAlias+"."+columnName+" "+property.getLogic()+" #{"+propertyName+"} ";
			Element IF = newElement("if",testAttr,testValue);
			IF.addCDATA(text);
			trim.add(IF);
		}
		
		nestedSelect.addCDATA(sql.toString());
		nestedSelect.add(trim);
		return nestedSelect;
	}

	/**
	 * 
	 * @Title	addNewNestedResultMap 
	 * @Description	根据tableModel对象,创建一个简单的ResultMap元素
	 * 				带有复杂属性的映射,映射到一个select语句去(懒惰加载时查询)
	 * @param table
	 * @param mapper void
	 */
	private void addNewNestedResultMap(TableModel table,Element mapper) {
		String alias = table.getAlias();
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		List<PropertyModel> simpleProperties = table.getSimpleProperties();
		List<PropertyModel> associations = table.getAssociations();
		List<PropertyModel> collections = table.getCollections();
		
		attrs.add("id");
		values.add(NameUtils.getNestedResultMapName(alias));
		attrs.add("type");
		values.add(alias);
		
		Element nestedResultMap = newElement("resultMap",attrs.toArray(),values.toArray());
		mapper.add(nestedResultMap);
		attrs = new ArrayList<String>();
		values = new ArrayList<String>();
		
		attrs.add("property");
		attrs.add("column");
		//普通属性
		for(PropertyModel proModel : simpleProperties){

			values = new ArrayList<String>();
			values.add(proModel.getPropertyName());
			values.add(proModel.getColumnName());
			if(proModel.isPrimaryKey()){
				Element idEle = newElement("id",attrs.toArray(),values.toArray());
				nestedResultMap.add(idEle);
			}else{
				Element result = newElement("result",attrs.toArray(),values.toArray());
				nestedResultMap.add(result);
			}
		}
		//以下为复杂对象。多2个属性
		attrs.add("javaType");
		attrs.add("select");
		//association属性
		for(PropertyModel proModel : associations){
			String proAlias = NameUtils.getShortName(proModel.getJavaType());
			values = new ArrayList<String>();
			values.add(proModel.getPropertyName());
			//column字段的值
			StringBuffer column = new StringBuffer();
			column.append("{");
			for(int i=0; i< proModel.getKeys().length; ++i){
				String key = proModel.getKeys()[i];
				column.append(NameUtils.toCamelCase(key)+"="+key);
				if(i<proModel.getKeys().length-1){
					column.append(",");
				}
			}
			column.append("}");
			
			values.add(column.toString());
			values.add(proAlias);
			values.add(NameUtils.getSubSelectName(proAlias));
			Element association = newElement("association",attrs.toArray(),values.toArray());
			nestedResultMap.add(association);
			mapper.add(newSubSelect(proModel));
		}
		//第三个元素改成ofType
		attrs.set(2, "ofType");
		//collection属性
		for(PropertyModel proModel : collections){
			String proAlias = NameUtils.getShortName(proModel.getJavaType());
			values = new ArrayList<String>();
			values.add(proModel.getPropertyName());
			//column字段的值
			StringBuffer column = new StringBuffer();
			column.append("{");
			for(int i=0; i< proModel.getKeys().length; ++i){
				String key = proModel.getKeys()[i];
				column.append(NameUtils.toCamelCase(key)+"="+key);
				if(i<proModel.getKeys().length-1){
					column.append(",");
				}
			}
			column.append("}");
			
			values.add(column.toString());
			values.add(proAlias);
			values.add(NameUtils.getSubSelectName(proAlias));
			Element association = newElement("collection",attrs.toArray(),values.toArray());
			nestedResultMap.add(association);
			mapper.add(newSubSelect(proModel));
		}
		
	}

	/**
	 * 
	 * @Title	newSubSelect 
	 * @Description	创建一个嵌套结果集引用的子查询,里边ResultMap引用外部命名空间中的resultMap
	 * @param proModel void
	 * @return Element
	 */
	private Element newSubSelect(PropertyModel proModel) {
		String alias = NameUtils.getShortName(proModel.getJavaType());
		String refSqlMapPacket = proModel.getRefSqlMapPacket();
		//关联表名和关联字段名
		String[] keys = proModel.getKeys();
		String[] relKeys = proModel.getRelKeys();
		String relTableName = proModel.getRelTableName();
		String[] relRefKeys = proModel.getRelRefKeys();
		String refTableName = proModel.getRefTableName();
		String[] refKeys = proModel.getRefKeys();
		
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		attrs.add("id");
		attrs.add("parameterType");
		attrs.add("resultMap");
		
		values.add(NameUtils.getSubSelectName(alias));
		//由于结果集中的关联字段column中用了{prop1=key1,prop2=key2}这样的复合主键。
		//所以这里参数类型必须为HashMap
		values.add("HashMap");
		//resultMap的全限定名 = packet+mapperName+resultMapName
		String refResultMap = refSqlMapPacket+"."+NameUtils.getMapperName(alias)+"."+NameUtils.getNestedResultMapName(alias);
		values.add(refResultMap);
		
		Element subSelect = newElement("select",attrs.toArray(),values.toArray());
		//拼装sql语句
		StringBuffer sql = new StringBuffer();
		String tableAlias = NameUtils.getTableAlias(refTableName);
		List<PropertyModel> properties = proModel.getSimpleRefProperties();
		sql.append("select ");
		for(int i=0; i<properties.size(); ++i){
			PropertyModel property = properties.get(i);
			sql.append(tableAlias+"."+property.getColumnName());
			if(i<properties.size()-1){
				sql.append(",");
			}
		}
		sql.append(" from "+refTableName+" "+tableAlias);
		//关系表名
		String relTableAlias = null;
		if(StringUtils.isNotBlank(relTableName)){
			relTableAlias = NameUtils.getTableAlias(relTableName);
			//若表别名一样。则再关系表名后添加'_'
			if(tableAlias.equals(relTableAlias)){
				relTableAlias+="_";
			}
			sql.append(",");
			sql.append(relTableName+" "+relTableAlias);
			
		}
		sql.append(" where ");
		if(StringUtils.isNotBlank(proModel.getRelTableName())){
			//有关系表,先用refKeys和relRefKeys相关联
			for(int i=0; i<refKeys.length; ++i){
				String refKey = refKeys[i];
				String relRefKey = relRefKeys[i];
				sql.append(tableAlias+"."+refKey + " = "+relTableAlias+"."+relRefKey);
				sql.append(" and ");
			}
			
			//再用relKeys 和 keys 相关联
			for(int i=0; i<keys.length; ++i){
				//由于ResultMap中有进行转换。区别于字段名。作为参数名
				String key = NameUtils.toCamelCase(keys[i]);
				String relKey = relKeys[i];
				sql.append(tableAlias+"."+relKey+" = "+"#{"+key+"}");
				if(i<keys.length-1){
					sql.append(" and ");
				}
			}
		}else{
			//无关联表,用refkeys和keys相关联
			for(int i=0; i<keys.length; ++i){
				//由于ResultMap中有进行转换。区别于字段名。作为参数名
				String key = NameUtils.toCamelCase(keys[i]);
				String refKey = refKeys[i];
				sql.append(tableAlias+"."+refKey+" = "+"#{"+key+"}");
				if(i<keys.length-1){
					sql.append(" and ");
				}
			}
		}
		subSelect.addCDATA(sql.toString());
		return subSelect;
	}

	/**
	 * 
	 * @Title	newDelete 
	 * @Description	根据tableModel对象,创建一个delete元素。按条件删除数据
	 * @param table
	 * @return Element
	 */
	private Element newDelete(TableModel table) {
		String alias = table.getAlias();
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		PropertyModel primaryKey = table.getPrimaryKey();
		
		//delete标签
		attrs.add("id");
		values.add("delete"+alias);
		attrs.add("parameterType");
		values.add(alias);
		attrs.add("flushCache");
		values.add("true");
		if(StringUtils.isNotBlank(getConfig().getTimeout())){
			attrs.add("timeout");
			values.add(getConfig().getTimeout());
		}
		
		Element delete = newElement("delete",attrs.toArray(),values.toArray());
		
		delete.addCDATA("delete from "+table.getTableName()+" ");
		
		Element where = DocumentHelper.createElement("where");
		delete.add(where);
		
		if(primaryKey != null){
			Element choose = DocumentHelper.createElement("choose");
			where.add(choose);
			
			String[] attr = {"test"};
			String[] value = {primaryKey.getPropertyName()+" != null"};
			Element when = newElement("when",attr,value);
			when.addCDATA("and " + primaryKey.getColumnName()+" = #{"+primaryKey.getPropertyName()+"}");
			choose.add(when);
			
			Element otherwise = DocumentHelper.createElement("otherwise");
			
			for(PropertyModel property : table.getProperties()){
				//主键不创建if标签
				if(property.getColumnName().equals(primaryKey.getColumnName())){
					continue;
				}
				value[0] = property.getPropertyName()+" != null";
				Element IF = newElement("if",attr,value);
				IF.addCDATA("and " +property.getColumnName()+" "+property.getLogic()+" #{"+property.getPropertyName()+"}");
				otherwise.add(IF);
			}
			
			choose.add(otherwise);
			
		}
		
		return delete;
	}
	/**
	 * 
	 * @Title	newUpdate
	 * @Description	返回update元素,用于映射更新语句.id = update+alias
	 * @param table
	 * @return Element
	 */
	private Element newUpdate(TableModel table) {
		PropertyModel primaryKey = table.getPrimaryKey();
		String alias = table.getAlias();
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		//update标签
		attrs.add("id");
		values.add("update"+alias);
		attrs.add("parameterType");
		values.add(alias);
		attrs.add("flushCache");
		values.add("true");
		if(StringUtils.isNotBlank(getConfig().getTimeout())){
			attrs.add("timeout");
			values.add(getConfig().getTimeout());
		}
		Element update = newElement("update",attrs.toArray(),values.toArray());
		
		update.addCDATA("update "+table.getTableName()+" ");
		
		Element set = DocumentHelper.createElement("set");
		update.add(set);
		//set标签
		attrs = new ArrayList<String>();
		attrs.add("test");
		for(PropertyModel property : table.getSimpleProperties()){

			String propertyName = property.getPropertyName();
			String columnName = property.getColumnName();
			values = new ArrayList<String>();
			values.add(propertyName+" != null");
			//主键则不创建if标签
			if(columnName.equals(primaryKey.getColumnName())){
				continue;
			}
			Element ifColumn = newElement("if",attrs.toArray(),values.toArray());
			ifColumn.addCDATA(columnName + "=#{"+propertyName+"},");
			set.add(ifColumn);
		}
		//where
		update.addCDATA("where "+primaryKey.getColumnName()+" = #{" +primaryKey.getPropertyName()+"}");
		
		return update;
	}

	/**
	 * 
	 * @Title	newInsert
	 * @Description	返回一个insert元素,id=insert+alias,用于新增对象sql的映射
	 * @param table
	 * @return Element
	 */
	private Element newInsert(TableModel table) {
		String alias = table.getAlias();
		List<String> attrs = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		//insert标签
		attrs.add("id");
		values.add("insert"+alias);
		attrs.add("parameterType");
		values.add(alias);
		attrs.add("flushCache");
		values.add("true");
		if(StringUtils.isNotBlank(getConfig().getTimeout())){
			attrs.add("timeout");
			values.add(getConfig().getTimeout());
		}
		if(getConfig().isUseGenerateKeys()){
			String key = table.getPrimaryKey().getColumnName();
			if(key != null){
				attrs.add("useGeneratedKeys");
				values.add("true");
				attrs.add("keyProperty");
				values.add(key);
			}
		}
		Element insert = newElement("insert",attrs.toArray(),values.toArray());
		//insert标签内容
		insert.addCDATA("insert into "+table.getTableName()+" ");
		//trim标签
		attrs = new ArrayList<String>();
		values = new ArrayList<String>();
		attrs.add("prefix");
		attrs.add("suffix");
		attrs.add("prefixOverrides");
		values.add("(");
		values.add(")");
		values.add(",");
		Element trimColumns = newElement("trim",attrs.toArray(),values.toArray());
		values.set(0, "values(");
		Element trimValues = newElement("trim",attrs.toArray(),values.toArray());
		insert.add(trimColumns);
		insert.add(trimValues);
		
		
		attrs = new ArrayList<String>();
		attrs.add("test");
		for(PropertyModel property : table.getSimpleProperties()){

			String propertyName = property.getPropertyName();
			String columnName = property.getColumnName();
			values = new ArrayList<String>();
			values.add(propertyName+" != null");
			
			Element ifColumn = newElement("if",attrs.toArray(),values.toArray());
			ifColumn.addCDATA(","+columnName);
			trimColumns.add(ifColumn);
			
			Element ifValue = newElement("if",attrs.toArray(),values.toArray());
			ifValue.addCDATA(",#{"+propertyName+"}");
			trimValues.add(ifValue);
		}
		
		return insert;
	}

	/**
	 * 
	 * @Title	newConfigurationFile 
	 * @Description	创建一个mybatis配置文件的结构
	 * @return Document
	 */
	private Document newConfigurationFile() {
		DocumentFactory docFactory = DocumentFactory.getInstance();
		DocumentType docType = docFactory.createDocType("message","public","system");
		//增加DTD文件规范约束
		//<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
		docType.setName("configuration");
		docType.setPublicID("-//mybatis.org//DTD Config 3.0//EN");
		docType.setSystemID("http://mybatis.org/dtd/mybatis-3-config.dtd");
		Document doc = new DefaultDocument(docType);
		//配置文件主要元素 configuration
		Element root = DocumentHelper.createElement("configuration");
		Element properties = newDefaultProperties();
		Element settings = newDefaultSettings();
		Element typeAliases = DocumentHelper.createElement("typeAliases");
		Element typeHandlers = DocumentHelper.createElement("typeHandlers");
		Element objectFactory = DocumentHelper.createElement("objectFactory");
		Element plugins = newDefaultPlugins();
		Element environments = newDefaultEnvironments();
		Element mappers = DocumentHelper.createElement("mappers");
		
		root.addComment("the database connection properties");
		root.add(properties);
		root.addComment("mybatis default settings");
		root.add(settings);
		root.addComment("type aliases");
		root.add(typeAliases);
		root.addComment("plugins");
		root.add(plugins);
		root.addComment("database environments");
		root.add(environments);
		root.addComment("mappers");
		root.add(mappers);
		
		doc.add(root);
		return doc;
	}

	/**
	 * 
	 * @Title	newDefaultPlugins 
	 * @Description	声明了分页拦截器
	 * @return Element
	 */
	private Element newDefaultPlugins() {
		Element plugins = DocumentHelper.createElement("plugins");
		String[] attrs = {"interceptor"};
		//拦截器叫PaginationInterceptor 存放在com.projectName.base.pagination包下
		String[] values = {"com."+getConfig().getTargetProject().toLowerCase()+".base.pagination.PaginationInterceptor"};
		plugins.add(newElement("plugin",attrs,values));
		return plugins;
	}

	/**
	 * 
	 * @Title	newDefaultEnvironments 
	 * @Description	返回一个默认的Environmenets元素
	 * @return Element
	 */
	private Element newDefaultEnvironments() {
		Element environments = DocumentHelper.createElement("environments");
		environments.addAttribute("default", "freeHands");
		
		Element environment = environments.addElement("environment");
		environment.addAttribute("id", "freeHands");
		
		Element transactionManager = environment.addElement("transactionManager");
		transactionManager.addAttribute("type", "JDBC");
		
		Element dataSource = environment.addElement("dataSource");
		dataSource.addAttribute("type", "POOLED");
		dataSource.add(newElement("property","driver","${driver}"));
		dataSource.add(newElement("property","url","${url}"));
		dataSource.add(newElement("property","username","${username}"));
		dataSource.add(newElement("property","password","${password}"));
		
		return environments;
	}

	/**
	 * 
	 * @Title	newDefaultSettings 
	 * @Description	返回一个默认的Settings元素
	 * @return Element
	 */
	private Element newDefaultSettings() {
		Element settings = DocumentHelper.createElement("settings");
		settings.add(newElement("setting","cacheEnabled","true"));
		settings.add(newElement("setting","lazyLoadingEnabled","true"));
		settings.add(newElement("setting","aggressiveLazyLoading","false"));
//		settings.add(newElement("setting","multipleResultSetsEnabled","true"));
//		settings.add(newElement("setting","useColumnLabel","true"));
		if(getConfig().isUseGenerateKeys()){
			settings.add(newElement("setting","useGeneratedKeys","true"));
		}else{
			settings.add(newElement("setting","useGeneratedKeys","false"));
		}
//		settings.add(newElement("setting","defaultExecutorType","SIMPLE"));
		String timeout = this.getConfig().getTimeout();
		if(StringUtils.isNotBlank(timeout)){
			settings.add(newElement("setting","defaultStatementTimeout",this.getConfig().getTimeout()));
		}
		settings.add(newElement("setting","autoMappingBehavior","FULL"));
		
		return settings;
	}

	/**
	 * 
	 * @Title	newElement 
	 * @Description	创建一个标签为qname的元素,其name值为attr,value值为value
	 * @param qname	标签名
	 * @param attr name属性值
	 * @param value value属性值
	 * @return Element 返回值类型
	 */
	private Element newElement(String qname, String attr, String value) {
		Element ele = DocumentHelper.createElement(qname);
		ele.addAttribute("name", attr);
		ele.addAttribute("value", value);
		return ele;
	}

	/**
	 * 
	 * @Title	newElement 
	 * @Description	根据qname,arrts参数名数组,values值数组,创建一个带有属性的qname元素
	 * 				并返回
	 * @param qname
	 * @param attrs
	 * @param values
	 * @return Element
	 */
	private Element newElement(String qname,Object[] attrs,Object[] values){
		Element ele = DocumentHelper.createElement(qname);
		if(attrs != null && values != null)
		for(int i=0; i< attrs.length; ++i){
			String attr = attrs[i].toString();
			String value = "";
			if(values.length-1 >= i){
				value = values[i].toString();
			}
			ele.addAttribute(attr, value);
		}
		return ele;
	}
	
	/**
	 * 
	 * @Title	newDefaultProperties 
	 * @Description	创建一个默认的Properties元素。里边包含了数据库连接配置
	 * @return Element
	 */
	private Element newDefaultProperties() {
		Element properties = DocumentHelper.createElement("properties");
		//添加properties内容
		JdbcConfigurationBean jdbcConfig = this.getConfig().getJdbcConfig();
		properties.add(newElement("property","driver",jdbcConfig.getDriver()));
		properties.add(newElement("property","url",jdbcConfig.getUrl()));
		properties.add(newElement("property","username",jdbcConfig.getUser()));
		properties.add(newElement("property","password",jdbcConfig.getPassword()));
		properties.addComment("写数据库类型。。支持mysql，oracle");
		properties.add(newElement("property","dialect","databaseType"));
		return properties;
	}

}
