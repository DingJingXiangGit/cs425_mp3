package uiuc.nosql.model.task;

import java.util.HashSet;
import java.util.Set;

import uiuc.nosql.model.Response;

public class AllWriteTask  extends Task{
	private int taskId;
	private Set<Integer> sources;
	private int numReplicas;
	
	public AllWriteTask(){
		sources = new HashSet<Integer>();
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public boolean consume(Response response) {
		int source = response.getSender();
		if(sources.contains(source) == false){
			//System.out.println("task #"+taskId+" receives acks from: "+source);
			sources.add(source);
		}
		if(sources.size() == numReplicas){
			System.out.println(request + " completed");
			return true;
		}
		return false;
	}

	public int getNumReplicas() {
		return numReplicas;
	}

	public void setNumReplicas(int numReplicas) {
		this.numReplicas = numReplicas;
	}
	
	public String toString(){
		return "All Write " + taskId;
	}
}
