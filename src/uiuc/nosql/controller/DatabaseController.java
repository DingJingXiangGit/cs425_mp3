package uiuc.nosql.controller;

import java.util.List;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.DataStoreService;
import uiuc.nosql.model.HashService;
import uiuc.nosql.model.NodeConf;
import uiuc.nosql.model.Tuple;
import uiuc.nosql.model.remote.RemoteListenService;
import uiuc.nosql.model.remote.RemoteRequestService;

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
	
	public void execute(Request command){
		if(command.getAction() == Action.ShowAll){
			digest(command);
		}else{
			int total = Math.min(this.numReplicas, conf.getServerNodes().size());
			int[] hashCodes = hashService.hash(command.getKey(), total);
			int hashCode = -1;
			for(int i = 0; i < hashCodes.length; ++i){
				hashCode = hashCodes[i];
				System.out.println("target code = "+hashCode +" local code = "+localHashCode);
				if(hashCode == localHashCode){
					digest(command);
				}else{
					remoteRequestService.execute(command, hashCode);
				}
			}
		}
	}
	
	private void digest(Request command){
		if(command.getAction() == Action.Delete){
			this.dataStore.delete(command.getKey());
		}else if(command.getAction() == Action.Get){
			Tuple tuple = this.dataStore.get(command.getKey());
			System.out.println(tuple);
		}else if(command.getAction() == Action.Update){
			Tuple tuple = new Tuple();
			tuple.setKey(command.getKey());
			tuple.setValue(command.getValue());
			tuple.setTimestamp(command.getTimestamp());
			this.dataStore.update(tuple);
		}else if(command.getAction() == Action.Insert){
			Tuple tuple = new Tuple();
			tuple.setKey(command.getKey());
			tuple.setValue(command.getValue());
			tuple.setTimestamp(command.getTimestamp());
			this.dataStore.insert(tuple);
		}else if(command.getAction() == Action.ShowAll){
			List<Tuple> tuples = this.dataStore.showAll();
			System.out.println("there are "+tuples.size() +" in local server.");
			for(Tuple tuple: tuples){
				System.out.println(tuple);
			}
		}
	}
}
