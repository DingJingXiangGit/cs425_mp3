package uiuc.nosql.model.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class OneReadTask extends Task{
	private int taskId;
	private List<Tuple> tuples;
	//private int source;

	public OneReadTask(){
		tuples = new LinkedList<Tuple>();
		//source = -1;
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
		if(source != -1){
			//this.source = source;
			//System.out.println("receive tuples from: "+source);
			this.tuples.addAll(response.getTuples());
			tuples.removeAll(Collections.singleton(null));  
			if(this.tuples.size() > 0){
				Collections.sort(this.tuples, Collections.reverseOrder());
				System.out.println(request + " completed");
				System.out.println("result: "+this.tuples.get(0));
			}
			return true;
		}
		return false;
	}
	
	public String toString(){
		return "One Read " + taskId;
	}
}
