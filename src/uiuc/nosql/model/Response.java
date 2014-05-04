package uiuc.nosql.model;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable, IMessageContent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6152452364222421899L;
	private List<Tuple> tuples;
	private int initiator;
	private int sender;
	private int taskId;
	
	
	public List<Tuple> getTuples() {
		return tuples;
	}
	public void setTuples(List<Tuple> tuples) {
		this.tuples = tuples;
	}
	public int getInitiator() {
		return initiator;
	}
	public void setInitiator(int source) {
		this.initiator = source;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	public int getSender() {
		return sender;
	}
	public void setSender(int sender) {
		this.sender = sender;
	}

	
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{content:response, initiator:"+initiator+", task:"+taskId);
		stringBuffer.append(", sender:").append(sender);
		stringBuffer.append(", content:[");
		if(tuples != null){
			for(Tuple tuple:tuples){
				stringBuffer.append(tuple).append(", ");
			}
		}
		stringBuffer.append("]").append("}");
		return stringBuffer.toString();
	}
}
