package org.micheal.freeHands.bean;

public class SqlMapGenerator {
	private String targetPacketPrefix;
	private String targetPacketSuffix;
	private String targetPacket;
	
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
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("\ttargetPacket: "+this.getTargetPacket());
		sb.append("\n");
		sb.append("\ttargetPacketPrefix: "+this.getTargetPacketPrefix());
		sb.append("\n");
		sb.append("\ttargetPacketSuffix: "+this.getTargetPacketSuffix());
		return sb.toString();
	}
}
