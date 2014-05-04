package uiuc.nosql.model.task;

import java.util.LinkedList;
import java.util.List;

import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;

public class RepairTask extends Task{
	private List<Integer> targetList;
	private List<Integer> receivedList;
	private Tuple tuple;
	
	public RepairTask(){
		receivedList = new LinkedList<Integer>();
	}
	
	@Override
	public void consume(Response response) {
		receivedList.add(response.getSender());
	}

	@Override
	public boolean isResultReady() {
		if(receivedList.size() == targetList.size()){
			int received = -1;
			for(int i = 0; i < receivedList.size(); ++i){
				received = receivedList.get(i);
				if(targetList.contains(received) == false){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void printResult() {
		if(isResultReady()){
			System.out.println("tuple: "+tuple +" read repair finished.");
		}
	}

	public List<Integer> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<Integer> targetList) {
		this.targetList = targetList;
	}
	

	public Tuple getTuple() {
		return tuple;
	}

	public void setTuple(Tuple tuple) {
		this.tuple = tuple;
	}

}
