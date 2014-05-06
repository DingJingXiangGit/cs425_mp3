package uiuc.nosql.model.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class OneReadTask extends Task{
	private int taskId;
	//private Map<Integer, List<Tuple>> responseTuple;
	private Map<Integer, Tuple> responseTuple;
	private int numReplicas;

	public OneReadTask(){
		//responseTuple = new HashMap<Integer, List<Tuple>>();
		responseTuple = new HashMap<Integer, Tuple>();
	}
	
	public int getNumReplicas() {
		return numReplicas;
	}

	public void setNumReplicas(int numReplicas) {
		this.numReplicas = numReplicas;
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
		if(source != -1){
			//this.responseTuple.put(source, response.getTuples());
			this.responseTuple.put(source, response.getTuple());
			System.out.println("task #"+taskId+" receives acks from: "+source);
		}
		
		if(this.responseTuple.size() == numReplicas){
			this.startRepair();
		}
	}
	
	private void startRepair(){
		//System.out.println("\n one start read repair\n");
		String value = null;
		long timestamp = -1;
		List<Tuple> tuples = new ArrayList<Tuple>();
		//for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
		for(Entry<Integer, Tuple> entry: this.responseTuple.entrySet()){
			//tuples.addAll(entry.getValue());
			tuples.add(entry.getValue());
		}
		tuples.removeAll(Collections.singleton(null));
		if(tuples.size() > 0){
			List<Integer> inconsistentList = new ArrayList<Integer>();
			Collections.sort(tuples, Collections.reverseOrder());
			Tuple consistentTuple = tuples.get(0);
			value = consistentTuple.getValue();
			timestamp = consistentTuple.getTimestamp();
			
			//for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
			for(Entry<Integer, Tuple> entry: this.responseTuple.entrySet()){
				Tuple entryTuple = entry.getValue();
				if(entryTuple == null){
					inconsistentList.add(entry.getKey());
				}else{
					//System.out.println("tuple is "+entryTuple);
					if(entryTuple.getTimestamp() != timestamp ||
							entryTuple.getValue().equals(value) == false){
						inconsistentList.add(entry.getKey());
					}
				}
				/*
				entry.getValue().removeAll(Collections.singleton(null));
				if(entry.getValue().size() == 0){
					inconsistentList.add(entry.getKey());
				}else{
					Tuple tuple = entry.getValue().get(0);
					System.out.println("tuple is "+tuple);
					if(tuple.getTimestamp() != timestamp ||
							tuple.getValue().equals(value) == false){
						inconsistentList.add(entry.getKey());
					}
				}
				*/
			}
			if(inconsistentList.size() == 0){
				System.out.println("Consistency Checked No Need to Repair");
			}else{
				System.out.println("Consistency Checked: Repair Start.");
				taskManager.deployRepairTask(consistentTuple, inconsistentList);
			}
		}else{
			System.out.println("Consistency Checked No Need to Repair");
		}
	}
	
	public String toString(){
		return "One Read " + taskId;
	}
	
	@Override
	public boolean isResultReady() {
		return this.responseTuple.size() == 1;
	}
	
	public void printResult(){
		List<Tuple> tuples = new ArrayList<Tuple>();
		/*for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
			for(Entry<Integer, List<Tuple>> entry: this.responseTuple.entrySet()){
			tuples.addAll(entry.getValue());
		}*/
		for(Entry<Integer, Tuple> entry: this.responseTuple.entrySet()){
			tuples.add(entry.getValue());
		}
		
		tuples.removeAll(Collections.singleton(null)); 
		if(tuples.size() > 0){
			Collections.sort(tuples, Collections.reverseOrder());
			System.out.println("One Read Task #"+taskId+" is completed");
			System.out.println("result: " + tuples.get(0));
		}else{
			System.out.println("One Read Task #"+taskId+" is completed");
			System.out.println("result: null");
		}
	}
}
