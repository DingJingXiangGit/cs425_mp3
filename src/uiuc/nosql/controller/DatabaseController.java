package uiuc.nosql.controller;

import java.util.LinkedList;
import java.util.List;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.DataStoreService;
import uiuc.nosql.model.HashService;
import uiuc.nosql.model.NodeConf;
import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;
import uiuc.nosql.model.remote.RemoteListenService;
import uiuc.nosql.model.remote.RemoteRequestService;
import uiuc.nosql.model.remote.ServerNode;
import uiuc.nosql.model.task.RepairTask;
import uiuc.nosql.model.task.SearchTask;
import uiuc.nosql.model.task.Task;

public class DatabaseController {
	private DataStoreService dataStore;
	private RemoteRequestService remoteRequestService;
	private RemoteListenService remoteListenService ;
	private NodeConf conf;
	private HashService hashService;
	private int localHashCode;
	private int numReplicas;
	private static DatabaseController instance;
	
	public static DatabaseController getInstance(){
		if(instance == null){
			instance = new DatabaseController();
		}
		return instance;
	}
	
	private DatabaseController (){
		dataStore = DataStoreService.getInstance();
		conf = NodeConf.getInstance();
		hashService = new HashService(conf.getServerNodes().size());
		localHashCode = conf.getMachineHashCode();
		numReplicas = conf.getReplicas();
		remoteRequestService = new RemoteRequestService();
		remoteListenService = new RemoteListenService();
		remoteListenService.start();
	}
	
	public void execute(Task task){
		Request request = task.getRequest();
		Action action = request.getAction();
		if(action == Action.Repair){
			RepairTask repairTask = (RepairTask)task;
			List<Integer> targetList =  repairTask.getTargetList();
			for(int i = 0; i < targetList.size(); ++i){
				remoteRequestService.execute(request, targetList.get(i));
			}
		}else if(action == Action.ShowAll){
			digest(request);
		}else if(action == Action.Search){
			search(task);
		}else{
			int total = Math.min(this.numReplicas, conf.getServerNodes().size());
			int[] hashCodes = hashService.hash(request.getKey(), total);
			int hashCode = -1;
			for(int i = 0; i < hashCodes.length; ++i){
				hashCode = hashCodes[i];
				//System.out.println("target code = "+hashCode +" local code = "+localHashCode);
				if(hashCode == localHashCode){
					digest(request);
				}else{
					remoteRequestService.execute(request, hashCode);
				}
			}
		}
	}
	
//	private void search(Request request) {
//		String key = request.getKey();
//		int total = Math.min(this.numReplicas, conf.getServerNodes().size());
//		int[] hashCodes = hashService.hash(key, total);
//		int hashCode = -1;
//		ServerNode replica;
//		for(int i = 0; i < hashCodes.length; ++i){
//			hashCode = hashCodes[i];
//			replica = conf.getServerNode(hashCode);
//			System.out.println(replica);
//		}
//	}
	
	private void search(Task task) {
		SearchTask searchTask = (SearchTask)task;
		List<Integer> waitingList = searchTask.getWaitingList();
		Request request = searchTask.getRequest();
		int hashCode = -1;
		for(int i = 0; i < waitingList.size(); ++i){
			hashCode = waitingList.get(i);
			if(hashCode == localHashCode){
				digest(request);
			}else{
				remoteRequestService.execute(request, hashCode);
			}
		}
	}

	public void execute(Request command){
		digest(command);
	}
	
	private void digest(Request command){
		if(command.getAction() == Action.Delete){
			Tuple tuple = new Tuple();
			tuple.setKey(command.getKey());
			tuple.setValue(command.getValue());
			tuple.setTimestamp(command.getTimestamp());
			this.dataStore.delete(tuple);
		}else if(command.getAction() == Action.Get){
			Tuple tuple = this.dataStore.get(command.getKey());
			Response response = new Response();
			List<Tuple> result = new LinkedList<Tuple>();
			result.add(tuple);
			response.setTuples(result);
			response.setInitiator(command.getInitiator());
			response.setSender(localHashCode);
			response.setTaskId(command.getTaskId());
			if(command.getInitiator() != localHashCode){
				this.remoteRequestService.execute(response, command.getInitiator());
			}else{
				TaskManager.getInstance().processResponse(response);
			}
		}else if(command.getAction() == Action.Update){
			Tuple tuple = new Tuple();
			tuple.setKey(command.getKey());
			tuple.setValue(command.getValue());
			tuple.setTimestamp(command.getTimestamp());
			this.dataStore.update(tuple);
			
			Response response = new Response();
			response.setInitiator(command.getInitiator());
			response.setTaskId(command.getTaskId());
			response.setSender(localHashCode);
			if(command.getInitiator() != localHashCode){
				this.remoteRequestService.execute(response, command.getInitiator());
			}else{
				TaskManager.getInstance().processResponse(response);
			}
		}else if(command.getAction() == Action.Insert){
			Tuple tuple = new Tuple();
			tuple.setKey(command.getKey());
			tuple.setValue(command.getValue());
			tuple.setTimestamp(command.getTimestamp());
			this.dataStore.insert(tuple);
			Response response = new Response();
			response.setInitiator(command.getInitiator());
			response.setTaskId(command.getTaskId());
			response.setSender(localHashCode);
			if(command.getInitiator() != localHashCode){
				this.remoteRequestService.execute(response, command.getInitiator());
			}else{
				TaskManager.getInstance().processResponse(response);
			}
		}else if(command.getAction() == Action.ShowAll){
			List<Tuple> tuples = this.dataStore.showAll();
			System.out.println("there are "+tuples.size() +" in local server.");
			for(Tuple tuple: tuples){
				System.out.println(tuple);
			}
		}else if(command.getAction() == Action.Search){
			Tuple tuple = this.dataStore.get(command.getKey());
			Response response = new Response();
			List<Tuple> result = new LinkedList<Tuple>();
			result.add(tuple);
			response.setTuples(result);
			response.setInitiator(command.getInitiator());
			response.setSender(localHashCode);
			response.setTaskId(command.getTaskId());
			if(command.getInitiator() != localHashCode){
				this.remoteRequestService.execute(response, command.getInitiator());
			}else{
				TaskManager.getInstance().processResponse(response);
			}
		}else if(command.getAction() == Action.Repair){
			Tuple tuple = new Tuple();
			tuple.setKey(command.getKey());
			tuple.setValue(command.getValue());
			tuple.setTimestamp(command.getTimestamp());
			this.dataStore.update(tuple);
			
			Response response = new Response();
			response.setInitiator(command.getInitiator());
			response.setTaskId(command.getTaskId());
			response.setSender(localHashCode);
			if(command.getInitiator() != localHashCode){
				this.remoteRequestService.execute(response, command.getInitiator());
			}else{
				TaskManager.getInstance().processResponse(response);
			}
		}
	}
}
