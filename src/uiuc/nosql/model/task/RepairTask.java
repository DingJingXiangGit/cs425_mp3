package uiuc.nosql.model.task;

import java.util.List;

import uiuc.nosql.model.Response;

public class RepairTask extends Task{
	private List<Integer> targetList;
	
	@Override
	public void consume(Response response) {
		
	}

	@Override
	public boolean isResultReady() {
		return false;
	}

	@Override
	public void printResult() {
		
	}

	public List<Integer> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<Integer> targetList) {
		this.targetList = targetList;
	}
}
