package uiuc.nosql.model.task;

import java.util.HashSet;
import java.util.Set;

import uiuc.nosql.model.Response;

public class OneWriteTask  extends Task{
	private int taskId;
	private Set<Integer> sources;
	
	public OneWriteTask(){
		sources = new HashSet<Integer>();
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public void consume(Response response) {
		int source = response.getSender();
		if(source != -1){
			System.out.println("task #"+taskId+" receives acks from: "+source);
			sources.add(source);
		}
	}
	
	public String toString(){
		return "One Write " + taskId;
	}

	@Override
	public boolean isResultReady() {
		if(this.sources.size() > 0){
			return true;
		}
		return false;
	}
	
	public void printResult(){
		if(isResultReady()){
			System.out.println("One Write Task #"+taskId+" is completed");
		}else{
			System.out.println("One Write Task #"+taskId+" is completed");
		}
		
	}
}
