package org.micheal.freeHands.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.micheal.freeHands.bean.PropertyBean;
import org.micheal.freeHands.bean.TableBean;
import org.micheal.freeHands.bean.ConfigurationBean;
import org.micheal.freeHands.bean.DaoGenerator;
import org.micheal.freeHands.bean.JavaModelGenerator;
import org.micheal.freeHands.bean.JdbcConfigurationBean;
import org.micheal.freeHands.bean.SqlMapGenerator;
import org.micheal.freeHands.exception.ConfigurationFormatException;
import org.micheal.freeHands.exception.FileNotFoundException;
import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.PathUtils;
import org.micheal.freeHands.util.StringUtils;

public class ConfigurationParser {
	
	/**
	* @Title: parse 
	* @Description: 解析一个配置文件，生成一个ConfigurationBean。 
	* @param url 配置文件存放位置URL
	* @throws DocumentException 
	* @throws ConfigurationFormatException    配置文件格式异常
	* @return ConfigurationBean    返回类型 
	* @throws FileNotFoundException 
	 */
	public static ConfigurationBean parse(String url) throws DocumentException, ConfigurationFormatException, FileNotFoundException {
		int configFileElementCount = 0;
		int settingsElementCount = 0;
		int jdbcElementCount = 0;
		int javaModelElementCount = 0;
		int sqlMapElementCount = 0;
		int daoElementCount = 0;
		int tablesElementCount = 0;
		
		SAXReader reader = null;
		Document doc = null;
		ConfigurationBean config = null;
		File file = new File(url);
		if(PathUtils.checkFileExist(file) && file.getName().trim().endsWith(".xml")){
			reader = new SAXReader();
			doc = reader.read(file);
			config = new ConfigurationBean();

			Element root = doc.getRootElement();
			String targetProject = root.attributeValue("targetProject");
			if(StringUtils.isBlank(targetProject)){
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_ATTRIBUTE,root.getName(),"targetProject");
			}
			config.setTargetProject(targetProject.trim());
			
			//根据各个元素的名字解析
			Iterator<Element> it = root.elementIterator();
			while(it.hasNext()){
				Element someEle = it.next();
				String qName = someEle.getName();
				if(qName.equals("configurationFile")){
					if(++configFileElementCount >1){
						throw new ConfigurationFormatException(ConfigurationFormatException.TOO_MANY_ELEMENT,someEle.getName(),root.getName());
					}
					parseElementConfigurationFile(someEle,config);
				}else if(qName.equals("author")){
					String author = someEle.getTextTrim();
					config.setAuthor(author);
				}else if(qName.equals("settings")){
					if(++settingsElementCount > 1){
						throw new ConfigurationFormatException(ConfigurationFormatException.TOO_MANY_ELEMENT,someEle.getName(),root.getName());
					}
					parseElementSettings(someEle,config);
				}else if(qName.equals("jdbcConnection")){
					if(++jdbcElementCount > 1){
						throw new ConfigurationFormatException(ConfigurationFormatException.TOO_MANY_ELEMENT,someEle.getName(),root.getName());
					}
					parseElementJdbcConnection(someEle,config);
				}else if(qName.equals("javaModelGenerator")){
					if(++javaModelElementCount > 1){
						throw new ConfigurationFormatException(ConfigurationFormatException.TOO_MANY_ELEMENT,someEle.getName(),root.getName());
					}
					parseElementJavaModelGenerator(someEle,config);
				}else if(qName.equals("sqlMapGenerator")){
					if(++sqlMapElementCount > 1){
						throw new ConfigurationFormatException(ConfigurationFormatException.TOO_MANY_ELEMENT,someEle.getName(),root.getName());
					}
					parseElementSqlMapGenerator(someEle,config);
				}else if(qName.equals("daoGenerator")){
					if(++daoElementCount > 1){
						throw new ConfigurationFormatException(ConfigurationFormatException.TOO_MANY_ELEMENT,someEle.getName(),root.getName());
					}
					parseElementDaoGenerator(someEle,config);
				}else if(qName.equals("tables")){
					++tablesElementCount;
					parseElementTables(someEle,config);
				}else{
					throw new ConfigurationFormatException(ConfigurationFormatException.UNKNOW_SUBELEMENT,someEle.getName(),root.getName());
				}
			}
			
			//缺少必要元素时报异常
			if(jdbcElementCount <= 0){
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_SUBELEMENT, "jdbcConnection",root.getName());
			}
			if(javaModelElementCount <= 0){
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_SUBELEMENT, "javaModelGenerator",root.getName());
			}
			if(sqlMapElementCount <= 0){
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_SUBELEMENT, "sqlMapGenerator",root.getName());
			}
			if(daoElementCount <= 0){
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_SUBELEMENT, "daoGenerator",root.getName());
			}
			if(tablesElementCount <= 0){
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_SUBELEMENT, "tables",root.getName());
			}
		}else{
			throw new FileNotFoundException(file);
		}
		
		return config;
	}

	/**
	 * 
	 * @Title	parseElementSettings 
	 * @Description	解析settings元素的配置
	 * @param settings
	 * @param config
	 * @throws ConfigurationFormatException void
	 */
	private static void parseElementSettings(Element settings,
			ConfigurationBean config) throws ConfigurationFormatException {
		String charset = parsePropertyFromElement(settings, "charset",false);
		String timeout = parsePropertyFromElement(settings, "timeout",false);
		String useGenerateKeys = parsePropertyFromElement(settings, "useGenerateKeys",false);
		
		if(StringUtils.isNotBlank(charset)){
			config.setCharset(charset);
		}
		if(StringUtils.isNotBlank(timeout)){
			config.setTimeout(timeout);
		}
		if(StringUtils.isNotBlank(useGenerateKeys) && useGenerateKeys.equals("true")){
			config.setUseGenerateKeys(true);
		}
		
	}

	/**
	 * 
	* @Title: parseElementTables 
	* @Description: 解析配置文件中的tables元素 
	* @param tables	Element实例
	* @param config 配置文件实例
	* @throws ConfigurationFormatException    配置文件格式异常
	* @return void    返回类型 
	 */
	private static void parseElementTables(Element tables, ConfigurationBean config) throws ConfigurationFormatException {
		int tableElementCount = 0;
		String schema = tables.attributeValue("schema");
		if(StringUtils.isBlank(schema)){
			throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_ATTRIBUTE,tables.getName(),"schema");
		}else{
			schema = schema.trim();
		}
		String nameRule = tables.attributeValue("nameRule");
		if(StringUtils.isNotBlank(nameRule)){
			nameRule = nameRule.trim();
		}
		Iterator<Element> eleIt = tables.elementIterator();
		//解析tables下边的子元素
		while(eleIt.hasNext()){
			Element ele = eleIt.next();
			if(ele.getName().equals("table")){
				++tableElementCount;
				parseElementTable(ele,config,schema,nameRule);
			}else{
				throw new ConfigurationFormatException(ConfigurationFormatException.UNKNOW_SUBELEMENT,ele.getName(),tables.getName());
			}
		}
		if(tableElementCount <= 0){
			throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_SUBELEMENT, "table",tables.getName());
		}
	}

	/**
	 * 
	* @Title: parseElementTable
	* @Description: 解析配置文件中的table元素 
	* @param table	Element实例
	* @param config 配置文件实例
	* @param schema 
	 * @param nameRule 
	* @throws ConfigurationFormatException    配置文件格式异常
	* @return void    返回类型 
	 */
	private static void parseElementTable(Element table, ConfigurationBean config, String schema, String nameRule) throws ConfigurationFormatException {
		TableBean tableBean = new TableBean();
		String tableName = parsePropertyFromElement(table,"tableName");
		String alias = parsePropertyFromElement(table,"alias",false);
		String columnPrefix = parsePropertyFromElement(table,"columnPrefix",false);
		String columnSuffix = parsePropertyFromElement(table,"columnSuffix",false);
		String propertyNameRule = parsePropertyFromElement(table,"propertyNameRule",false);
		String targetPacket = parsePropertyFromElement(table,"targetPacket",false);
		String packetAlias = parsePropertyFromElement(table,"packetAlias",false);
		String javaType = null;
		
		if(StringUtils.isBlank(alias)){
			alias = NameUtils.toCamelCaseClassName(tableName);
		}
		
		if(StringUtils.isBlank(targetPacket)){
			JavaModelGenerator javaModelGenerator = config.getJavaModelGenerator();
			if(StringUtils.isNotBlank(javaModelGenerator.getTargetPacket())){
				targetPacket = javaModelGenerator.getTargetPacket();
			}else{
				String prefix = javaModelGenerator.getTargetPacketPrefix();
				String suffix = javaModelGenerator.getTargetPacketSuffix();
				if(StringUtils.isBlank(packetAlias)){
					packetAlias = NameUtils.lowerCaseStart(alias);
				}
				targetPacket = prefix+"."+packetAlias+"."+suffix;
			}
		}
		javaType = targetPacket+"."+alias;
		
		tableBean.setSchema(schema);
		tableBean.setTableName(tableName);
		tableBean.setJavaType(javaType);
		tableBean.setTargetPacket(targetPacket);
		tableBean.setAlias(alias);
		tableBean.setPacketAlias(packetAlias);
		tableBean.setColumnPrefix(columnPrefix);
		tableBean.setColumnSuffix(columnSuffix);
		
		//若tables有配置nameRule而table没有配置。则继承父节点的配置
		if(StringUtils.isBlank(propertyNameRule)){
			if(StringUtils.isNotBlank(nameRule)){
				propertyNameRule = nameRule;
			}
		}
		
		if(StringUtils.isNotBlank(propertyNameRule)){
			tableBean.setPropertyNameRule(propertyNameRule);
		}
		
		Element properties = table.element("properties");
		if(properties != null){
			Iterator<Element> eleIt = properties.elementIterator();
			nameRule = tableBean.getPropertyNameRule();
			while(eleIt.hasNext()){
				Element ele = eleIt.next();
				if(ele.getName().equals("property")){
					tableBean.getProperties().add(parseElementProperty(ele,nameRule));
				}else if(ele.getName().equals("condition")){
					tableBean.getProperties().add(parseElementCondition(ele,nameRule));
				}else if(ele.getName().equals("association")){
					tableBean.getProperties().add(parseElementAssociation(ele,nameRule));
				}else if(ele.getName().equals("collection")){
					tableBean.getProperties().add(parseElementCollection(ele,nameRule));
				}else{
					throw new ConfigurationFormatException(ConfigurationFormatException.UNKNOW_SUBELEMENT,ele.getName());
				}
			}
		}
		config.getTables().add(tableBean);
	}

	/**
	 * 
	 * @Title	parseElementCondition 
	 * @Description	解析condition 查询条件元素.(pojo添加一个属性做为查询条件用的) 
	 * @param ele
	 * @param nameRule
	 * @return PropertyBean
	 * @throws ConfigurationFormatException 
	 */
	private static PropertyBean parseElementCondition(Element ele,String nameRule) throws ConfigurationFormatException {
		String column = parsePropertyFromElement(ele,"column");
		String name = parsePropertyFromElement(ele,"name");
		String javaType = parsePropertyFromElement(ele,"javaType",false);
		String logic = parsePropertyFromElement(ele,"logic",false);
		
		PropertyBean property = null;
		if(StringUtils.isNotBlank(column)){
			property = new PropertyBean(column);
		}
		
		if(StringUtils.isNotBlank(logic)){
			property.setLogic(logic);
		}
		
		//JavaType有可能为空。在DatabaseParser中有处理
		property.setName(name);
		property.setJavaType(javaType);
		property.setType(PropertyBean.CONDITION);
		return property;
	}

	/**
	 * 
	* @Title: parseElementProperty 
	* @Description: 解析配置文件中的property元素
	* @param ele
	 * @param nameRule 
	* @throws ConfigurationFormatException    配置文件格式异常 
	* @return PropertyBean    返回类型 
	 */
	private static PropertyBean parseElementProperty(Element ele, String nameRule) throws ConfigurationFormatException {
		String column = parsePropertyFromElement(ele,"column");
		String name = parsePropertyFromElement(ele,"name",false);
		String javaType = parsePropertyFromElement(ele,"javaType",false);
	
		PropertyBean property = null;
		if(StringUtils.isNotBlank(column)){
			property = new PropertyBean(column);
		}
		//name和JavaType有可能为空。在DatabaseParser中有处理
		property.setName(name);
		property.setJavaType(javaType);
	
		return property;
	}

	/**
	 * 
	 * @Title	parseElementCollection 
	 * @Description	解析集合类型复杂属性配置
	 * @param ele
	 * @param nameRule
	 * @return
	 * @throws ConfigurationFormatException PropertyBean
	 */
	private static PropertyBean parseElementCollection(Element ele, String nameRule) throws ConfigurationFormatException {
		PropertyBean property = parseElementAssociation(ele, nameRule);
		property.setType(PropertyBean.COLLECTION);
		return property;
	}

	/**
	 * 
	 * @Title	parseElementAssociation 
	 * @Description	解析复杂属性配置
	 * @param ele
	 * @param nameRule
	 * @return
	 * @throws ConfigurationFormatException PropertyBean
	 */
	private static PropertyBean parseElementAssociation(Element ele, String nameRule) throws ConfigurationFormatException {
		String refTableName = parsePropertyFromElement(ele,"refTableName");
		String keys = parsePropertyFromElement(ele,"keys");
		String refKeys = parsePropertyFromElement(ele,"refKeys");
		
		String relTableName = parsePropertyFromElement(ele,"relTableName",false);
		String relKeys = null;
		String relRefKeys = null;

		String name = parsePropertyFromElement(ele,"name",false);
		String javaType = parsePropertyFromElement(ele,"javaType",false);
		
		if(StringUtils.isNotBlank(relTableName)){
			relKeys = parsePropertyFromElement(ele,"relKeys");
			relRefKeys = parsePropertyFromElement(ele,"relRefKeys");
		}
		
		PropertyBean property = new PropertyBean();
		property.setRefTableName(refTableName);
		property.setKeys(keys.split(","));
		property.setRefKeys(refKeys.split(","));
		
		if(StringUtils.isNotBlank(relTableName)){
			property.setRelTableName(relTableName);
			property.setRelKeys(relKeys.split(","));
			property.setRelRefKeys(relRefKeys.split(","));
		}
		//复杂属性没有配置变量名
		if(StringUtils.isBlank(name)){
			if(nameRule.equals(TableBean.COLUMN_NAME)){
				name = refTableName;
			}else{
				name = NameUtils.toCamelCase(refTableName);
				name = NameUtils.lowerCaseStart(name);
			}
		}
		property.setName(name);
		property.setJavaType(javaType);
		property.setType(PropertyBean.ASSOCIATION);
		checkKeysNum(property);
		return property;
	}

	private static void checkKeysNum(PropertyBean property) throws ConfigurationFormatException {
		String[] keys = property.getKeys();
		String relTableName = property.getRelTableName();
		String[] relKeys = property.getRelKeys();
		String[] relRefKeys = property.getRelRefKeys();
		String[] refKeys = property.getRefKeys();
		
		//验证一下keys的数量是否相等。必须相等
		if(StringUtils.isNotBlank(relTableName)){
			if(keys.length != relKeys.length ||
					relRefKeys.length != refKeys.length){
				//TODO 不相等。需要抛异常
				throw new ConfigurationFormatException("keys的数量不相等！");
			}
		}else{
			System.out.println(keys.length+" "+refKeys.length);
			if(keys.length != refKeys.length){
				//TODO 不相等。需要抛异常
				throw new ConfigurationFormatException("keys的数量不相等！");
			}
		}
	}

	/**
	 * 
	* @Title: parseElementDaoGenerator 
	* @Description: 解析配置文件中的DaoGenerator元素
	* @param dao Element实例
	* @param config 配置文件实例
	* @throws ConfigurationFormatException    配置文件格式异常 
	* @return void    返回类型 
	*
	 */
	private static void parseElementDaoGenerator(Element dao, ConfigurationBean config) throws ConfigurationFormatException {
		DaoGenerator daoGenerator = config.getDaoGenerator();
		
		String targetPacket = parsePropertyFromElement(dao, "targetPacket",false);
		if(targetPacket != null){
			daoGenerator.setTargetPacket(targetPacket);
		}else{
			daoGenerator.setTargetPacketPrefix(parsePropertyFromElement(dao, "targetPacketPrefix"));
			daoGenerator.setTargetPacketSuffix(parsePropertyFromElement(dao, "targetPacketSuffix"));
		}
		
		daoGenerator.setSuperClass(parsePropertyFromElement(dao, "superClass",false));
	}

	/**
	 * 
	* @Title: parseElementSqlMapGenerator 
	* @Description: 解析配置文件中的sqlMapGenerator元素
	* @param sqlMap
	* @param config
	* @throws ConfigurationFormatException    配置文件格式异常
	* @return void    返回类型 
	 */
	private static void parseElementSqlMapGenerator(Element sqlMap, ConfigurationBean config) throws ConfigurationFormatException {
		SqlMapGenerator sqlMapGenerator = config.getSqlMapGenerator();
		String targetPacket = parsePropertyFromElement(sqlMap, "targetPacket",false);
		if(targetPacket != null){
			sqlMapGenerator.setTargetPacket(targetPacket);
		}else{
			sqlMapGenerator.setTargetPacketPrefix(parsePropertyFromElement(sqlMap, "targetPacketPrefix"));
			sqlMapGenerator.setTargetPacketSuffix(parsePropertyFromElement(sqlMap, "targetPacketSuffix"));
		}
	}

	/**
	 * 
	 * @Title	parseElementJavaModelGenerator 
	 * @Description	从javaModelGenerator元素中解析出javaModelGenerator的配置 
	 * @param javaModel
	 * @param config
	 * @throws ConfigurationFormatException void
	 */
	private static void parseElementJavaModelGenerator(Element javaModel, ConfigurationBean config) throws ConfigurationFormatException {
		JavaModelGenerator javaModelGenerator = config.getJavaModelGenerator();
		
		String targetPacket = parsePropertyFromElement(javaModel, "targetPacket",false);
		if(targetPacket != null){
			javaModelGenerator.setTargetPacket(targetPacket);
		}else{
			javaModelGenerator.setTargetPacketPrefix(parsePropertyFromElement(javaModel, "targetPacketPrefix"));
			javaModelGenerator.setTargetPacketSuffix(parsePropertyFromElement(javaModel, "targetPacketSuffix"));
		}
		javaModelGenerator.setSuperClass(parsePropertyFromElement(javaModel, "superClass",false));
	}

	/**
	 * 
	 * @Title	parseElementJdbcConnection 
	 * @Description	从jdbcConnection元素中解析出jdbc配置
	 * @param jdbc
	 * @param config
	 * @throws ConfigurationFormatException void
	 */
	private static void parseElementJdbcConnection(Element jdbc, ConfigurationBean config) throws ConfigurationFormatException {
		JdbcConfigurationBean jdbcConfig = config.getJdbcConfig();
		jdbcConfig.setJdbcClassPath(parsePropertyFromElement(jdbc,"classPath"));
		jdbcConfig.setDriver(parsePropertyFromElement(jdbc,"driver"));
		jdbcConfig.setUrl(parsePropertyFromElement(jdbc,"url"));
		jdbcConfig.setUser(parsePropertyFromElement(jdbc,"user"));
		jdbcConfig.setPassword(parsePropertyFromElement(jdbc,"password"));
	}

	private static void parseElementConfigurationFile(Element configuration, ConfigurationBean config) throws ConfigurationFormatException {
		config.setConfigurationFile(parsePropertyFromElement(configuration,"location"));
		//TODO 验证路径是否文件。是否合法
		
	}
	
	/**
	 * 
	 * @Title	parsePropertyFromElement 
	 * @Description	从 ele 元素下边查找 propertyName 的属性或子元素，并返回其值。不存在则抛出异常
	 * @param ele
	 * @param propertyName
	 * @return
	 * @throws ConfigurationFormatException String
	 */
	private static String parsePropertyFromElement(Element ele,String propertyName) throws ConfigurationFormatException{
		return parsePropertyFromElement(ele,propertyName,true);
	}
	
	/**
	 * 
	 * @Title	parsePropertyFromElement 
	 * @Description	从 ele 元素下边查找 propertyName 的属性或子元素，并返回其值。require为true时候抛出异常，false不抛出异常
	 * @param ele
	 * @param propertyName
	 * @param require
	 * @return
	 * @throws ConfigurationFormatException String
	 */
	private static String parsePropertyFromElement(Element ele,String propertyName,boolean require) throws ConfigurationFormatException{
		String attribute = ele.attributeValue(propertyName);
		String elementText = ele.elementText(propertyName);
		if(elementText != null && elementText.trim().length()>0){
			return elementText.trim();
		}else if(attribute != null && attribute.trim().length()>0){
			return attribute.trim();
		}else{
			if(require)
				throw new ConfigurationFormatException(ConfigurationFormatException.MISSING_PROPERTY,propertyName,ele.getName());
			return null;
		}
	}
}
