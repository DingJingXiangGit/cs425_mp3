package uiuc.nosql.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uiuc.nosql.model.Action;
import uiuc.nosql.model.HashService;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.NodeConf;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.Response;
import uiuc.nosql.model.Tuple;
import uiuc.nosql.model.remote.ServerNode;
import uiuc.nosql.model.task.AllReadTask;
import uiuc.nosql.model.task.AllWriteTask;
import uiuc.nosql.model.task.OneReadTask;
import uiuc.nosql.model.task.OneWriteTask;
import uiuc.nosql.model.task.RepairTask;
import uiuc.nosql.model.task.SearchTask;
import uiuc.nosql.model.task.Task;

public class TaskManager {
	private Map<Integer, Task> tasksTable;
	private int taskIdIndexer;
	private int numReplicas;
	private static TaskManager instance = new TaskManager();
	private HashService hashService;
	private NodeConf conf;
	
	public static TaskManager getInstance(){
		return instance;
	}
	
	private TaskManager(){
		taskIdIndexer = 0;
		conf = NodeConf.getInstance();
		numReplicas = conf.getReplicas();
		tasksTable = new HashMap<Integer, Task>();
		hashService = new HashService(conf.getServerNodes().size());
	}
	
	public Task registerTask(Request request){
		Task task;
		if(request.getAction() == Action.Search){
			List<Integer> waitingList = new ArrayList<Integer>();
			String key = request.getKey();
			int total = Math.min(this.numReplicas, conf.getServerNodes().size());
			int[] hashCodes = hashService.hash(key, total);
			int hashCode = -1;
			for(int i = 0; i < hashCodes.length; ++i){
				hashCode = hashCodes[i];
				waitingList.add(hashCode);
			}
			
			SearchTask searchTask = new SearchTask();
			searchTask.setAction(Action.Search);
			searchTask.setLevel(Level.All);
			searchTask.setRequest(request);
			searchTask.setWaitingList(waitingList);
			searchTask.setKey(key);
			searchTask.setTaskId(taskIdIndexer++);
			searchTask.setManager(this);
			task = searchTask;
		}else{
			if(request.getLevel() == Level.All ){
				if(request.getAction() == Action.Get){
					AllReadTask item = new AllReadTask();
					item.setTaskId(taskIdIndexer++);
					item.setNumReplicas(numReplicas);
					item.setAction(Action.Get);
					item.setLevel(Level.All);
					item.setRequest(request);
					task = item;
				}else{
					AllWriteTask item = new AllWriteTask();
					item.setTaskId(taskIdIndexer++);
					item.setNumReplicas(numReplicas);
					item.setAction(request.getAction());
					item.setLevel(Level.All);
					item.setRequest(request);
					task = item;
				}
			}else{
				if(request.getAction() == Action.Get){
					OneReadTask item = new OneReadTask();
					item.setRequest(request);
					item.setNumReplicas(numReplicas);
					item.setTaskId(taskIdIndexer++);
					item.setAction(Action.Get);
					item.setLevel(Level.One);
					task = item;
				}else{
					OneWriteTask item = new OneWriteTask();
					item.setRequest(request);
					item.setTaskId(taskIdIndexer++);
					item.setAction(request.getAction());
					item.setLevel(Level.One);
					task = item;
				}
			}
		}
		
		System.out.println("register task #"+task.getTaskId());
		task.setManager(this);
		request.setTaskId(task.getTaskId());
		tasksTable.put(task.getTaskId(), task);
		return task;
	}
	
	public void processResponse(Response response){
		//System.out.println("process response task id:"+response);
		int taskId = response.getTaskId();
		if(tasksTable.containsKey(taskId)){
			Task task = tasksTable.get(taskId);
			task.consume(response);
			if(task.isResultReady()){
				task.printResult();
			}
		}
	}
	
	public void deployRepairTask(Tuple tuple, List<Integer> targets){
		DatabaseController databaseController = DatabaseController.getInstance();
		Request request= new Request();
		request.setKey(tuple.getKey());
		request.setValue(tuple.getValue());
		request.setTimestamp(tuple.getTimestamp());
		request.setAction(Action.Repair);
		request.setLevel(Level.All);
		request.setInitiator(NodeConf.getInstance().getMachineHashCode());
		
		RepairTask task = new RepairTask();
		task.setRequest(request);
		task.setTargetList(targets);
		task.setTaskId(taskIdIndexer++);
		task.setAction(Action.Repair);
		task.setLevel(Level.One);
		request.setTaskId(task.getTaskId());
		tasksTable.put(task.getTaskId(), task);
		databaseController.execute(task);
	}
}
