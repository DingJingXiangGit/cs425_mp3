package uiuc.nosql.model.task;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.Response;

public class OneWriteTask  extends Task{
	private Level level;
	private Action action;
	private int taskId;
	//private int source;
	//private int numReplicas;
	
	public OneWriteTask(){
		//source = -1;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Action getAction() {
		return action;
	}
	
	public void setAction(Action action) {
		this.action = action;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public boolean consume(Response response) {
		int source = response.getSource();
		if(source != -1){
			System.out.println("task #"+taskId+" receives acks from: "+source);
			//this.source = source;
			return true;
		}
		return false;
	}

	/*
	public int getNumReplicas() {
		return numReplicas;
	}

	public void setNumReplicas(int numReplicas) {
		this.numReplicas = numReplicas;
	}
	*/
	
	public String toString(){
		return "All Write " + taskId;
	}
}
