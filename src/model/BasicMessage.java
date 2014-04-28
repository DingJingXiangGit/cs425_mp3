package model;

import java.io.Serializable;

public class BasicMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 41234021L;
	public int sourceId;
	public IMessage content;

	public int getSourceId() {
		return sourceId;
	}


	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public IMessage getContent() {
		return content;
	}


	public void setContent(IMessage content) {
		this.content = content;
	}


	public String toString(){
		return String.format("{group:%d, source:%d, group_sequence:%s, content:%s}", sourceId, content);
	}
}
