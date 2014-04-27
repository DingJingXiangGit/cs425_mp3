package model;

import java.io.Serializable;

public class CausalOrderMulticastMessage implements IMessage, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 11278371231L;
	private Integer[] timeVector;
	private String content;
	private Integer groupId;
	private Integer source;
	
	
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	public Integer getGroupId() {
		return groupId;
	}
	
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public Integer[] getTimeVector() {
		return timeVector;
	}
	public void setTimeVector(Integer[] timeVector) {
		this.timeVector = timeVector;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String toString(){
		return String.format("{group:%d, time:%s, content:%s}", groupId, getTimeVectorString(), content);
	}
	
	private String getTimeVectorString(){
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(int i = 0; i < timeVector.length; ++i){
			builder.append(timeVector[i]);
			if(i != timeVector.length - 1){
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
