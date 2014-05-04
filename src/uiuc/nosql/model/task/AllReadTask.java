package uiuc.nosql.model.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class AllReadTask extends Task{
	private int taskId;
	private Set<Integer> sources;
	private Map<Integer, List<Tuple>> responseTuple;
	private int numReplicas;
	
	public AllReadTask(){
		sources = new HashSet<Integer>();
		responseTuple = new HashMap<Integer, List<Tuple>>();
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	@Override
	public void consume(Response response) {
		int source = response.getSender();
		if(sources.contains(source) == false){
			sources.add(source);
			responseTuple.put(source, response.getTuples());
		}
		
		if(this.sources.size() == numReplicas){
			startRepair();
		}
	}
	
	private void startRepair(){
		System.out.println("\n all start read repair\n");
		String value = null;
		long timestamp = -1;
		List<Tuple> tuples = new ArrayList<Tuple>();
		for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
			tuples.addAll(entry.getValue());
		}
		tuples.removeAll(Collections.singleton(null));
		if(tuples.size() > 0){
			List<Integer> inconsistentList = new ArrayList<Integer>();
			Collections.sort(tuples, Collections.reverseOrder());
			Tuple consistentTuple = tuples.get(0);
			value = consistentTuple.getValue();
			timestamp = consistentTuple.getTimestamp();
			
			for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
				entry.getValue().removeAll(Collections.singleton(null));
				if(entry.getValue().size() == 0){
					inconsistentList.add(entry.getKey());
				}else{
					Tuple tuple = entry.getValue().get(0);
					if(tuple.getTimestamp() != timestamp ||
							tuple.getValue().equals(value) == false){
						inconsistentList.add(entry.getKey());
					}
				}
			}
			if(inconsistentList.size() == 0){
				System.out.println("Consistency Checked No Need to Repair");
			}else{
				taskManager.deployRepairTask(consistentTuple, inconsistentList);
			}
		}else{
			System.out.println("Consistency Checked No Need to Repair");
		}
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

	@Override
	public boolean isResultReady() {
		return this.sources.size() == numReplicas;
	}
	
	public void printResult(){
		List<Tuple> tuples = new ArrayList<Tuple>();
		for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
			tuples.addAll(entry.getValue());
		}
		tuples.removeAll(Collections.singleton(null)); 
		if(tuples.size() > 0){
			Collections.sort(tuples, Collections.reverseOrder());
			System.out.println(request + " completed");
			System.out.println("result: " + tuples.get(0));
		}else{
			System.out.println(request + " completed");
			System.out.println("result: null");
		}
	}
}
