package uiuc.nosql.model.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uiuc.nosql.model.NodeConf;
import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;
import uiuc.nosql.model.remote.ServerNode;

public class SearchTask extends Task{
	private Map<Integer, ServerNode> nodeMap;
	private List<Integer> waitingList;
	private List<Integer> receivedList;
	private String key;
	
	public SearchTask(){
		nodeMap = new HashMap<Integer, ServerNode>();
		receivedList = new ArrayList<Integer>();
		setWaitingList(new ArrayList<Integer>());
	}
	
	@Override
	public void consume(Response response) {
		List<Tuple> tuples = response.getTuples();
		tuples.removeAll(Collections.singleton(null));
		int sender = response.getSender();
		receivedList.add(sender);
		if(tuples.size() > 0){
			NodeConf nodeConf = NodeConf.getInstance();
			ServerNode node = nodeConf.getServerNode(sender);
			nodeMap.put(sender, node);
		}
	}
	
	@Override
	public boolean isResultReady() {
		return receivedList.size() == waitingList.size();
	}

	@Override
	public void printResult() {
		System.out.println("Search " + key + " Completed:");
		for(Entry<Integer, ServerNode> pair: nodeMap.entrySet()){
			System.out.println(pair.getValue());
		}
	}

	public List<Integer> getWaitingList() {
		return waitingList;
	}

	public void setWaitingList(List<Integer> waitingList) {
		this.waitingList = waitingList;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
