package org.micheal.freeHands.builder;

/**
 * 
 * @ClassName: BuilderFactory 
 * @Description: Builder工厂类。可以调用get方法获取各类Builder
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-19 下午5:21:15 
 *
 */
public class BuilderFactory {

	/**
	 * 
	 * @Title	get 
	 * @Description	根据全限定名返回相应的builder实例。若没有找到此类。返回null
	 * @param type
	 * @return Builder
	 */
	public static Builder get(String type) {
		if(type.equals("javaModelBuilder")){
			return new JavaModelBuilder();
		}else if(type.equals("myBatisSqlMapBuilder")){
			return new MyBatisSqlMapBuilder();
		}else if(type.equals("myBatisDaoBuilder")){
			return new MyBatisDaoBuilder();
		}else if(type.equals("mybatisPaginationBuilder")){
			return new MyBatisPaginationBuilder();
		}
		return null;
	}
	
}
