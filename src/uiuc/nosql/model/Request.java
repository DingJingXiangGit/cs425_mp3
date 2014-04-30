package uiuc.nosql.model;

import java.io.Serializable;

public class Request implements Serializable, IMessageContent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4024541804035159627L;
	public static String DELETE="delete";
	public static String INSERT="insert";
	public static String GET="get";
	public static String UPDATE="update";
	
	private Action action;
	private String key;
	private String value;
	private long timestamp;
	private Level level;
	private int initiator;
	private int taskId;
	
	
	public int getInitiator() {
		return initiator;
	}

	public void setInitiator(int source) {
		this.initiator = source;
	}

	public Request(){
		java.util.Date date= new java.util.Date();
		this.timestamp = date.getTime();
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString(){
		return String.format("{content: request, initiaor: %d, task:%d, action: %s, key: %s, value: %s, level: %s, timestamp:%d}",
				initiator,
				taskId,
				action,
				key,
				value,
				level,
				timestamp);
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
}
