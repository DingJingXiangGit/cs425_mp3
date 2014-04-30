package uiuc.nosql.model.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class AllReadTask extends Task{
	private int taskId;
	private List<Tuple> tuples;
	private Set<Integer> sources;
	private int numReplicas;
	
	public AllReadTask(){
		tuples = new LinkedList<Tuple>();
		sources = new HashSet<Integer>();
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
		int source = response.getSender();
		if(sources.contains(source) == false){
			sources.add(source);
			this.tuples.addAll(response.getTuples());
			tuples.removeAll(Collections.singleton(null));  
		}
		
		if(sources.size() == numReplicas){
			if(this.tuples.size() > 0){
				Collections.sort(this.tuples, Collections.reverseOrder());
				System.out.println(request + " completed");
				System.out.println("result: "+this.tuples.get(0));
			}else{
				System.out.println(request + " completed");
				System.out.println("result: null");
			}
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
		return "All Read " + taskId;
	}
}
