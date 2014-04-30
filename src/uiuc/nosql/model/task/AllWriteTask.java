package uiuc.nosql.model.task;

import java.util.HashSet;
import java.util.Set;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.Response;

public class AllWriteTask  extends Task{
	private Level level;
	private Action action;
	private int taskId;
	private Set<Integer> sources;
	private int numReplicas;
	
	public AllWriteTask(){
		sources = new HashSet<Integer>();
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
		if(sources.contains(source) == false){
			System.out.println("task #"+taskId+" receives acks from: "+source);
			sources.add(source);
		}
		if(sources.size() == numReplicas){
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
		return "One Write " + taskId;
	}
}
