package uiuc.nosql.model.task;

import java.util.LinkedList;
import java.util.List;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class OneReadTask extends Task{
	private Level level;
	private Action action;
	private int taskId;
	private List<Tuple> tuples;
	//private int source;
	/*
	private int numReplicas;
	*/
	public OneReadTask(){
		tuples = new LinkedList<Tuple>();
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
	
	public List<Tuple> getTuples() {
		return tuples;
	}
	
	public void setTuples(List<Tuple> tuples) {
		this.tuples = tuples;
	}

	@Override
	public boolean consume(Response response) {
		int source = response.getSource();
		if(source != -1){
			//this.source = source;
			System.out.println("receive tuples from: "+source);
			this.tuples.addAll(response.getTuples());
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
		return "All Read " + taskId;
	}
}
