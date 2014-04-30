package uiuc.nosql.model.task;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class AllReadTask extends Task{
	private Level level;
	private Action action;
	private int taskId;
	private List<Tuple> tuples;
	private Set<Integer> sources;
	private int numReplicas;
	
	public AllReadTask(){
		tuples = new LinkedList<Tuple>();
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
	
	public List<Tuple> getTuples() {
		return tuples;
	}
	
	public void setTuples(List<Tuple> tuples) {
		this.tuples = tuples;
	}

	@Override
	public boolean consume(Response response) {
		int source = response.getSource();
		if(sources.contains(source) == false){
			sources.add(source);
			this.tuples.addAll(response.getTuples());
		}
		
		if(sources.size() == numReplicas){
			System.out.println("One read complested.");
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
		return "One Read " + taskId;
	}
}
