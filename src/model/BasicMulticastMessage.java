package model;

import java.io.Serializable;

public class BasicMulticastMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 41234021L;
	public int groupId;
	public int sourceId;
	public int sourceGroupSequence;
	public IMessage content;
	
	public int getGroupId() {
		return groupId;
	}


	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}


	public int getSourceId() {
		return sourceId;
	}


	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}


	public int getSourceGroupSequence() {
		return sourceGroupSequence;
	}


	public void setSourceGroupSequence(int sourceGroupSequence) {
		this.sourceGroupSequence = sourceGroupSequence;
	}


	public IMessage getContent() {
		return content;
	}


	public void setContent(IMessage content) {
		this.content = content;
	}


	public String toString(){
		return String.format("{group:%d, source:%d, group_sequence:%s, content:%s}", groupId, sourceId, sourceGroupSequence, content);
	}
}
