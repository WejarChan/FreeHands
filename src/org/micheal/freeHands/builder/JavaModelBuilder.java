package org.micheal.freeHands.builder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.micheal.freeHands.model.AccessPermission;
import org.micheal.freeHands.model.ClassModel;
import org.micheal.freeHands.model.ClassType;
import org.micheal.freeHands.model.PropertyModel;
import org.micheal.freeHands.model.TableModel;
import org.micheal.freeHands.util.IOUtils;
import org.micheal.freeHands.util.NameUtils;
import org.micheal.freeHands.util.PathUtils;
import org.micheal.freeHands.util.StringUtils;

/**
 * 
* @ClassName: JavaModelBuilder 
* @Description: 用于生成与数据库对应的pojo类
* @author Micheal_Chan 553806198@qq.com 
* @date 2013-4-19 下午5:16:53 
*
 */
public class JavaModelBuilder extends Builder {
	
	@Override
	protected void buildSomething() throws Exception {
		for (TableModel table : this.getTableModelList()) {
			buildJavaModel(table);
		}
	}

	/**
	 * 
	 * @Title	buildJavaModel 
	 * @Description	根据TableModel的信息创建一个Java类
	 * @param table
	 * @throws Exception void
	 */
	private void buildJavaModel(TableModel table) throws Exception {
		File packet = PathUtils.getPakcetFile(this.getConfig().getTargetProject(), table.getPojoTargetPacket());
		//不存在的包,创建
		if (!packet.exists()) {
			packet.mkdirs();
		}

		File modelFile = new File(packet.getAbsolutePath() + "/" + table.getAlias() + ".java");
		
		if (!modelFile.exists()) {
			ClassModel classModel = new ClassModel();
			classModel.setAuthor(getConfig().getAuthor());
			classModel.setAccessPermission(AccessPermission.PUBLIC);
			classModel.setAlias(table.getAlias());
			classModel.setPacket(table.getPojoTargetPacket());
			classModel.setSuper(table.getPojoSuperClass());
			classModel.setClassType(ClassType.CLASS);
			List<PropertyModel>properties = new ArrayList<PropertyModel>();
			properties.addAll(table.getProperties());
			properties.addAll(table.getAssociations());
			properties.addAll(table.getCollections());
			classModel.setProperties(properties);
			
			IOUtils.write2File(modelFile, classModel.toCode(), getCharset());
			System.out.println(modelFile.getAbsolutePath()+" build !");
		} else {
			System.out.println("File "+modelFile.getAbsolutePath()+" is exists!Please check up!");
			// TODO 文件已经存在。需要记录？
		}
	}

}
