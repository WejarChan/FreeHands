package org.micheal.freeHands.bean;

/**
 * 
 * @ClassName: DaoGenerator 
 * @Description: 对应daoGenerator元素的一个类
 * @author Micheal_Chan 553806198@qq.com 
 * @date 2013-4-19 下午5:22:10 
 *
 */
public class DaoGenerator {
	private String targetPacketPrefix;
	private String targetPacketSuffix;
	private String targetPacket;
	private String superClass;
	
	public String getTargetPacketPrefix() {
		return targetPacketPrefix;
	}
	public void setTargetPacketPrefix(String targetPacketPrefix) {
		this.targetPacketPrefix = targetPacketPrefix;
	}
	public String getTargetPacketSuffix() {
		return targetPacketSuffix;
	}
	public void setTargetPacketSuffix(String targetPacketSuffix) {
		this.targetPacketSuffix = targetPacketSuffix;
	}
	public String getTargetPacket() {
		return targetPacket;
	}
	public void setTargetPacket(String targetPacket) {
		this.targetPacket = targetPacket;
	}
	public String getSuperClass() {
		return superClass;
	}
	public void setSuperClass(String superClass) {
		this.superClass = superClass;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("\ttargetPacket: "+this.getTargetPacket());
		sb.append("\n");
		sb.append("\ttargetPacketPrefix: "+this.getTargetPacketPrefix());
		sb.append("\n");
		sb.append("\ttargetPacketSuffix: "+this.getTargetPacketSuffix());
		sb.append("\n");
		sb.append("\tsuperClass: "+this.getSuperClass());
		return sb.toString();
	}
}
