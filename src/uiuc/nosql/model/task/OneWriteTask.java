package uiuc.nosql.model.task;

import uiuc.nosql.model.Response;

public class OneWriteTask  extends Task{
	
	private int taskId;
	//private int source;
	//private int numReplicas;
	
	public OneWriteTask(){
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public boolean consume(Response response) {
		int source = response.getSender();
		if(source != -1){
			//System.out.println("task #"+taskId+" receives acks from: "+source);
			System.out.println(request + " completed");
			return true;
		}
		return false;
	}
	
	public String toString(){
		return "One Write " + taskId;
	}
	
	/*
	public int getNumReplicas() {
		return numReplicas;
	}

	public void setNumReplicas(int numReplicas) {
		this.numReplicas = numReplicas;
	}
	*/
	
	
}
