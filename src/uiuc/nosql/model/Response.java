package uiuc.nosql.model;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable, IMessageContent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6152452364222421899L;
	private List<Tuple> tuples;
	private int source;
	private int taskId;
	
	
	public List<Tuple> getTuples() {
		return tuples;
	}
	public void setTuples(List<Tuple> tuples) {
		this.tuples = tuples;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("task #"+taskId+" response from :"+source).append("\n");
		if(tuples != null){
			for(Tuple tuple:tuples){
				stringBuffer.append(tuple).append("\n");
			}
		}
		return stringBuffer.toString();
	}
}
