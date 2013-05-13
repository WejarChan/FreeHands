package org.micheal.freeHands.parser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.micheal.freeHands.bean.ConfigurationBean;
import org.micheal.freeHands.bean.DaoGenerator;
import org.micheal.freeHands.bean.JavaModelGenerator;
import org.micheal.freeHands.bean.JdbcConfigurationBean;
import org.micheal.freeHands.bean.PropertyBean;
import org.micheal.freeHands.bean.SqlMapGenerator;
import org.micheal.freeHands.bean.TableBean;
import org.micheal.freeHands.loader.UrlJarLoader;
import org.micheal.freeHands.model.PropertyModel;
import org.micheal.freeHands.model.TableModel;
import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.StringUtils;

public class DatabaseParser {
	
	/**
	 * 
	 * @Title	parseTableInfo 
	 * @Description	根据配置文件。读取数据库信息
	 * @param config
	 * @return
	 * @throws Exception 
	 */
	public static List<TableModel> parseTableInfo(ConfigurationBean config) throws Exception{
		List<TableModel> tableModelList = new ArrayList<TableModel>();
		//加载jdbc的jar包
		UrlJarLoader jarLoader = new UrlJarLoader();
		JdbcConfigurationBean jdbcConfig = config.getJdbcConfig();
		jarLoader.load(jdbcConfig.getJdbcClassPath());

		//打开数据库连接
		String url = jdbcConfig.getUrl();
		String username = jdbcConfig.getUser();
		String password = jdbcConfig.getPassword();

		Connection conn = null;
		conn = DriverManager.getConnection(url, username,password);
		DatabaseMetaData metaData = conn.getMetaData();
		
		//遍历配置信息,通过查数据库字段生成tableModel
		List<TableBean> tables = config.getTables();
		for(TableBean bean : tables){
			TableModel model = new TableModel();
			//先从数据库读取出数据表所有信息。转换成tableModel。再根据配置进行更新
			setProperties(model,bean,metaData,config);
			tableModelList.add(model);
		}
		
		//添加condition元素转换成的查询条件属性.添加再model中
		for(int i=0; i<tables.size(); ++i){
			TableBean tableBean = tables.get(i);
			TableModel tableModel = tableModelList.get(i);
			//吧条件属性添加到tableModel中
			addConditionProperties(tableBean,tableModel,metaData);
		}
		
		//添加association属性和collection属性
		//检查关联的表是否在这次要生成配置的表中
		//tables和tableModelList里边的顺序是一样的
		for(int i=0; i<tables.size(); ++i){
			TableBean tableBean = tables.get(i);
			TableModel tableModel = tableModelList.get(i);
			//把复杂属性添加到model中
			addComplexProperties(tableBean,tableModel,metaData,config,tableModelList);
		}
		
		//关闭连接
		conn.close();
		
		return tableModelList;
	}

	/**
	 * 
	 * @Title	addConditionProperties 
	 * @Description	若有配置条件属性。则转换成propertyModel并添加到tableModel中
	 * @param tableBean
	 * @param tableModel
	 * @param metaData
	 * @throws Exception 
	 */
	private static void addConditionProperties(TableBean tableBean,TableModel tableModel, DatabaseMetaData metaData) throws Exception {
		List<PropertyBean> conditionProperties = tableBean.getConditionProperties();
		//有查询条件属性
		if(conditionProperties.size() > 0){
			for(PropertyBean propertyBean : conditionProperties){
				String javaType = null;
				String dataType = null;
				String comment = null;
				if(StringUtils.isNotBlank(propertyBean.getJavaType())){
					javaType = propertyBean.getJavaType();
				}else{
					//查数据库
					ResultSet rs = metaData.getColumns(null, tableBean.getSchema(), tableBean.getTableName(), propertyBean.getColumn());
					if(rs.next()){
						dataType = rs.getString("TYPE_NAME");
						comment = rs.getString("REMARKS");
						javaType = getJavaType(dataType);
					}else{
						//条件属性对应的数据字段没找到
						throw new Exception("Column ["+propertyBean.getColumn()+"] not found in "+tableBean.getTableName()+"! maybe columnName is wrong!");
					}
				}
				PropertyModel propertyModel = new PropertyModel();
				propertyModel.setPropertyName(propertyBean.getName());
				propertyModel.setColumnName(propertyBean.getColumn());
				if(StringUtils.isNotBlank(propertyBean.getLogic())){
					propertyModel.setLogic(propertyBean.getLogic());
				}
				propertyModel.setDataType(dataType);
				propertyModel.setComment(comment);
				propertyModel.setJavaType(javaType);
				propertyModel.setCondition();
				tableModel.getProperties().add(propertyModel);
			}
		}
	}

	/**
	 * 
	 * @Title	addComplexProperties 
	 * @Description	若有配置复杂属性。则转换成propertyModel添加到tableModel中
	 * @param tableBean
	 * @param tableModel
	 * @param metaData
	 * @param config
	 * @param tableModelList
	 * @throws Exception void
	 */
	private static void addComplexProperties(TableBean tableBean, TableModel tableModel,DatabaseMetaData metaData, ConfigurationBean config,List<TableModel> tableModelList) throws Exception {
		//有复杂属性(要关联查询)
		List<PropertyBean> complexProperties = tableBean.getComplexProperties();
		if(complexProperties.size()>0){
			//遍历每一个复杂属性
			for(PropertyBean refBean : complexProperties){
				//设置变量属性
				PropertyModel propertyModel = new PropertyModel();
				propertyModel.setPropertyName(refBean.getName());
				propertyModel.setJavaType(refBean.getJavaType());
				propertyModel.setKeys(refBean.getKeys());//配置在嵌套查询那，用到数据库字段名
				
				propertyModel.setRelKeys(refBean.getRelKeys());
				propertyModel.setRelTableName(refBean.getRelTableName());
				propertyModel.setRelRefKeys(refBean.getRelRefKeys());
				
				propertyModel.setRefKeys(refBean.getRefKeys());
				propertyModel.setRefTableName(refBean.getRefTableName());

				String refTableName = propertyModel.getRefTableName();
				//在此次生成配置的表中,则添加关联表字段和属性。
				TableModel refModel = getTableModel(tableModelList,refTableName);
				if(refModel != null){
					propertyModel.setJavaType(refModel.getJavaType());
					propertyModel.setRefProperties(refModel.getProperties());
					//关联的sqlMap的包路劲。用于在sqlMap中引用外部命名空间的resultMap
					propertyModel.setRefSqlMapPacket(refModel.getSqlMapTargetPacket());
				}else{
					//没在当前要生成的表里边,查找数据表的字段。并按驼峰标识活列名生成变量名
					if(StringUtils.isNotBlank(propertyModel.getJavaType())){
						propertyModel.setRefProperties(getTableProperties(refTableName,metaData,tableBean,config));
						//判断是否存放于统一sqlMap的包
						SqlMapGenerator sqlMapGenerator = config.getSqlMapGenerator();
						String sqlMapPacket = sqlMapGenerator.getTargetPacket();
						if(StringUtils.isBlank(sqlMapPacket)){
							//从propertyModel的javaType中截取出packetAlias。
							//拼装成sqlMapAlias
							String packetAlias = propertyModel.getJavaType();
							packetAlias = packetAlias.substring(0,packetAlias.lastIndexOf('.'));
							packetAlias = packetAlias.substring(packetAlias.lastIndexOf('.')+1,packetAlias.length());
							sqlMapPacket = sqlMapGenerator.getTargetPacketPrefix()+"."+packetAlias+"."+sqlMapGenerator.getTargetPacketSuffix();
						}
						propertyModel.setRefSqlMapPacket(sqlMapPacket);
					}else{
						//TODO 抛出异常  配置不明确，没有javaType
						throw new Exception("没有javaType 配置不明确");
					}
				}
				//添加进model中
				if(refBean.isAssociation()){
					propertyModel.setAssociation();
					tableModel.getAssociations().add(propertyModel);
				}else{
					propertyModel.setCollection();
					tableModel.getCollections().add(propertyModel);
				}
			}
		}
	}

	/**
	 * 
	 * @Title	getTableProperties 
	 * @Description	获取表的所有字段
	 * @param refTableName
	 * @param metaData
	 * @param schema
	 * @param config
	 * @return
	 * @throws SQLException List<PropertyModel>
	 */
	private static List<PropertyModel> getTableProperties(String refTableName,
			DatabaseMetaData metaData, TableBean tableBean,ConfigurationBean config) throws SQLException {
		String schema = tableBean.getSchema();
		List<PropertyModel> properties = new ArrayList<PropertyModel>();
		List<String> primaryKeys = getPrimaryKeys(metaData,schema,refTableName);
		
		ResultSet rs = metaData.getColumns(null, schema, refTableName, null);
		while(rs.next()){
			String columnName = rs.getString("COLUMN_NAME");
			String propertyName = null;
			if(tableBean.getPropertyNameRule().equals(TableBean.COLUMN_NAME)){
				propertyName = columnName;
			}else{
				NameUtils.toCamelCase(columnName);
			}
			PropertyModel property = new PropertyModel();
			property.setColumnName(columnName);
			property.setPropertyName(propertyName);
			if(primaryKeys.contains(columnName)){
				property.setType(PropertyModel.PRIMARY_KEY);
			}
			properties.add(property);
		}
		
		return properties;
	}

	/**
	 * 
	 * @Title	getTableModel 
	 * @Description	获取tableName匹配的一个tableModel
	 * @param tableModelList
	 * @param refTableName
	 * @return TableModel
	 */
	private static TableModel getTableModel(List<TableModel> tableModelList,
			String refTableName) {
		for(TableModel model : tableModelList){
			if(model.getTableName().equals(refTableName)){
				return model;
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title	setProperties 
	 * @Description	加载数据库信息进tableModel,并根据配置信息,修改tableModel 
	 * @param tableModel
	 * @param tableBean
	 * @param metaData
	 * @param config
	 * @throws SQLException void
	 */
	private static void setProperties(TableModel tableModel,TableBean tableBean, DatabaseMetaData metaData, ConfigurationBean config) throws SQLException {
		//从tableBean里边获取targetPacket信息。并保存在tableModel里
		SqlMapGenerator sqlMapGenerator = config.getSqlMapGenerator();
		JavaModelGenerator javaModelGenerator = config.getJavaModelGenerator();
		DaoGenerator daoGenerator = config.getDaoGenerator();
		
		String javaType = tableBean.getJavaType();
		String targetPacket = tableBean.getTargetPacket();
		String alias = tableBean.getAlias();
		String packetAlias = tableBean.getPacketAlias();
		
		//设置基础信息
		tableModel.setTableName(tableBean.getTableName());
		tableModel.setJavaType(javaType);
		tableModel.setAlias(alias);
		
		//设置package和superClass
		tableModel.setPojoTargetPacket(targetPacket);
		tableModel.setPojoSuperClass(javaModelGenerator.getSuperClass());
		
		//sqlMap的targetPakcet
		targetPacket = sqlMapGenerator.getTargetPacket();
		String targetPacketPrefix = sqlMapGenerator.getTargetPacketPrefix();
		String targetPacketSuffix = sqlMapGenerator.getTargetPacketSuffix();
		if(StringUtils.isBlank(targetPacket)){
			targetPacket = targetPacketPrefix+"."+packetAlias+"."+targetPacketSuffix;
		}
		tableModel.setSqlMapTargetPacket(targetPacket);
		
		//dao的targetPacket
		targetPacket = daoGenerator.getTargetPacket();
		targetPacketPrefix = daoGenerator.getTargetPacketPrefix();
		targetPacketSuffix = daoGenerator.getTargetPacketSuffix();
		if(StringUtils.isBlank(targetPacket)){
			targetPacket = targetPacketPrefix+"."+packetAlias+"."+targetPacketSuffix;
		}
		tableModel.setDaoTargetPacket(targetPacket);
		tableModel.setDaoSuperClass(daoGenerator.getSuperClass());
		//主键外键
		List<String> primaryKeys = getPrimaryKeys(metaData,tableBean.getSchema(),tableBean.getTableName());
		List<String> foreignKeys = getForeignKeys(metaData,tableBean.getSchema(),tableBean.getTableName());
		
		ResultSet rs = metaData.getColumns(null, tableBean.getSchema(), tableBean.getTableName(), null);
		
		//从数据库中获取字段信息
		while(rs.next()){
			PropertyModel property = new PropertyModel();

			String columnName = rs.getString("COLUMN_NAME");
			String dataType = rs.getString("TYPE_NAME");
			String propertyName = null;
			if(tableBean.getPropertyNameRule().equals(TableBean.COLUMN_NAME)){
				propertyName = columnName;
			}else{
				propertyName = NameUtils.toCamelCase(columnName);
			}
			String comment = rs.getString("REMARKS");
			
			property.setColumnName(columnName);
			property.setPropertyName(propertyName);
			property.setDataType(dataType);
			property.setComment(comment);

			//根据数据库的DataType 计算 出JavaType，并设置
			property.setJavaType(getJavaType(dataType));
			//用数据库查主键和外键。并设置
			if(primaryKeys.contains(columnName)){
				property.setPrimaryKey();
			}else if(foreignKeys.contains(columnName)){
				property.setForeignKey();
			}
			//从配置文件更新信息。配置的变量名、java类型、
			updateFromConfig(property,tableBean.getProperties());
			
			tableModel.getProperties().add(property);
		}
		
	}

	/**
	 * 
	 * @Title	updateFromConfig 
	 * @Description	从配置文件中。更新tableModel的字段
	 * @param model
	 * @param properties void
	 */
	private static void updateFromConfig(PropertyModel model,List<PropertyBean> properties) {
		
		for(PropertyBean bean : properties){
			//普通属性
			if(bean.isProperty()){
				if(bean.getColumn().equals(model.getColumnName())){
					//配置文件设置的javaType
					if(StringUtils.isNotBlank(bean.getJavaType())){
						model.setJavaType(NameUtils.getFullName(bean.getJavaType()));
					}
					//配置文件设置的变量名
					if(StringUtils.isNotBlank(bean.getName())){
						model.setPropertyName(bean.getName());
					}
					//关系逻辑
					if(StringUtils.isNotBlank(bean.getLogic())){
						model.setLogic(bean.getLogic());
					}
				}
			}
		}
	}

	/**
	 * 
	 * @Title	getPrimaryKeys 
	 * @Description	获取这数据表所有主键
	 * @param metaData
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException List<String>
	 */
	private static List<String> getPrimaryKeys(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
		List<String> keys = new ArrayList<String>();
		ResultSet rs = metaData.getPrimaryKeys(null, schema, tableName);
		while(rs.next()){
			keys.add(rs.getString("COLUMN_NAME"));
		}
		return keys;
	}

	/**
	 * 
	 * @Title	getForeignKeys 
	 * @Description	获取这数据表所有外键
	 * @param metaData
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException List<String>
	 */
	private static List<String> getForeignKeys(DatabaseMetaData metaData, String schema, String tableName) throws SQLException {
		List<String> keys = new ArrayList<String>();
		ResultSet rs = metaData.getImportedKeys(null, schema, tableName);
		while(rs.next()){
			keys.add(rs.getString("FKCOLUMN_NAME"));
		}
		return keys;
	}

	/**
	 * 
	* @Title: getJavaType 
	* @Description: 根据数据库的datatype，返回映射的javaType
	* @param dataType
	* @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	private static String getJavaType(String dataType) {
		dataType = dataType.toLowerCase();
		if(dataType.equals("bigint")){
			return BigInteger.class.getName();
		}else if(dataType.equals("decimal") || dataType.equals("numeric")){
			return BigDecimal.class.getName();
		}else if(dataType.equals("bool") || dataType.equals("boolean")){
			return Boolean.class.getName();
		}else if(dataType.matches(".*char.*") || dataType.matches(".*clob.*")){
			return String.class.getName();
		}else if(dataType.matches(".*blob.*") || dataType.matches(".*binary.*")){
			return Byte[].class.getName();
		}else if(dataType.startsWith("int")){
			return Integer.class.getName();
		}else if(dataType.equals("long")){
			return Long.class.getName();
		}else if(dataType.startsWith("float")){
			return Float.class.getName();
		}else if(dataType.endsWith("double")){
			return Double.class.getName();
		}else if(dataType.matches(".*date.*") || dataType.matches(".*time.*")){
			return Date.class.getName();
		}else{
			return Object.class.getName();
		}
	}
}
