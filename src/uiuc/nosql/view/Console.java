package uiuc.nosql.view;
import java.io.*;
import java.util.Scanner;

import uiuc.nosql.controller.DatabaseController;
import uiuc.nosql.controller.TaskManager;
import uiuc.nosql.model.Action;
import uiuc.nosql.model.NodeConf;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.Level;
import uiuc.nosql.model.task.Task;
public class Console {
	private DatabaseController controller;
	private TaskManager taskManager;
	private NodeConf conf;
	public Console(){
		controller = DatabaseController.getInstance();
		taskManager = TaskManager.getInstance();
		conf = NodeConf.getInstance();
	}
	
	public void start(){
		Scanner scanner = new Scanner(System.in);
		String operator = null;
		Request request = null;
		Task task = null;
		while(scanner.hasNext()){
			operator = scanner.next();
			if(operator.equals("delete")){
				request = new Request();
				request.setAction(Action.Delete);
				request.setKey(scanner.next());
			}else if(operator.equals("get")){
				request = new Request();
				request.setAction(Action.Get);
				request.setKey(scanner.next());
				request.setLevel(Level.valueOf(scanner.next()));
			}else if(operator.equals("insert")){
				request = new Request();
				request.setAction(Action.Insert);
				request.setKey(scanner.next());
				request.setValue(scanner.next());
				request.setLevel(Level.valueOf(scanner.next()));
			}else if(operator.equals("update")){
				request = new Request();
				request.setAction(Action.Update);
				request.setKey(scanner.next());
				request.setValue(scanner.next());
				request.setLevel(Level.valueOf(scanner.next()));
			}else if(operator.equals("show-all")){
				request = new Request();
				request.setAction(Action.ShowAll);
			}
			if(request != null){
				System.out.println(request);
				task = taskManager.registerTask(request);
				request.setSource(conf.getMachineHashCode());
				controller.execute(task);
			}
			request = null;
		}
	}
}
