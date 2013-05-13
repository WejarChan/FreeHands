package org.micheal.freeHands.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

import org.dom4j.DocumentException;
import org.micheal.freeHands.bean.ConfigurationBean;
import org.micheal.freeHands.bean.TableBean;
import org.micheal.freeHands.builder.Builder;
import org.micheal.freeHands.builder.BuilderFactory;
import org.micheal.freeHands.exception.ConfigurationFormatException;
import org.micheal.freeHands.exception.FileFormatException;
import org.micheal.freeHands.exception.FileNotFoundException;
import org.micheal.freeHands.model.TableModel;
import org.micheal.freeHands.parser.ConfigurationParser;
import org.micheal.freeHands.parser.DatabaseParser;

public class FreeHands {
	
	public static Integer MYBATIS = 0;
	
	/**
	 * 
	 * @Title	free 
	 * @Description	解析FreeHands配置文件。生成映射文件
	 * @param path FreeHands的配置文件绝对路劲
	 * @param type
	 * @throws Exception void
	 */
	public void free(String path,Integer type) throws Exception{
		ConfigurationBean config = ConfigurationParser.parse(path);
		List<TableBean> tb = config.getTables();
		List<TableModel> tableModelList = DatabaseParser.parseTableInfo(config);
		
		Builder javaModelBuilder = BuilderFactory.get("javaModelBuilder");
		javaModelBuilder.setConfig(config);
		javaModelBuilder.setTableModelList(tableModelList);
		javaModelBuilder.setCharset(config.getCharset());
		javaModelBuilder.build();
		
		Builder sqlMapBuilder = BuilderFactory.get("myBatisSqlMapBuilder");
		sqlMapBuilder.setConfig(config);
		sqlMapBuilder.setTableModelList(tableModelList);
		sqlMapBuilder.setCharset(config.getCharset());
		sqlMapBuilder.build();
		
		Builder paginationBuilder = BuilderFactory.get("mybatisPaginationBuilder");
		paginationBuilder.setConfig(config);
		paginationBuilder.setTableModelList(tableModelList);
		paginationBuilder.setCharset(config.getCharset());
		paginationBuilder.build();
		
		
		Builder daoBuilder = BuilderFactory.get("myBatisDaoBuilder");
		daoBuilder.setConfig(config);
		daoBuilder.setTableModelList(tableModelList);
		daoBuilder.setCharset(config.getCharset());
		daoBuilder.build();
		
	}
	
	
}
