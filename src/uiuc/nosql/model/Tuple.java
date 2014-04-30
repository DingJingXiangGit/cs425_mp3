package uiuc.nosql.model;

import java.io.Serializable;

public class Tuple implements Serializable, Comparable<Tuple>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1496220581224715419L;
	private String key;
	private String value;
	private long timestamp;
	
	public Tuple(String key, String value, long timestamp){
		this.key = key;
		this.value = value;
		this.timestamp = timestamp;
	}

	public Tuple(){
		this.key = null;
		this.value = null;
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
	
	@Override
	public int compareTo(Tuple other) {
		if (this.timestamp < other.timestamp){
			return -1;
		}else if(this.timestamp == other.timestamp ){
			return 0;
		}else{
			return 1;
		}
	}
	
	@Override
	public String toString(){
		return String.format("{%s, %s}", key, value);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
