package uiuc.nosql.view;
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
	private int localHashCode;
	public Console(){
		controller = DatabaseController.getInstance();
		taskManager = TaskManager.getInstance();
		conf = NodeConf.getInstance();
		localHashCode = conf.getMachineHashCode();
	}
	
	public void start(){
		Scanner scanner = new Scanner(System.in);
		String operator = null;
		Request request = null;
		Task task = null;
		while(scanner.hasNext()){
			try{
				operator = scanner.next();
				if(operator.equals("delete")){
					request = new Request();
					request.setAction(Action.Delete);
					request.setKey(scanner.next());
					request.setInitiator(localHashCode);
				}else if(operator.equals("get")){
					request = new Request();
					request.setAction(Action.Get);
					request.setKey(scanner.next());
					request.setLevel(Level.valueOf(scanner.next()));
					//request.setInitiator(conf.getMachineHashCode());
					request.setInitiator(localHashCode);
				}else if(operator.equals("insert")){
					request = new Request();
					request.setAction(Action.Insert);
					request.setKey(scanner.next());
					request.setValue(scanner.next());
					request.setLevel(Level.valueOf(scanner.next()));
					request.setInitiator(localHashCode);
				}else if(operator.equals("update")){
					request = new Request();
					request.setAction(Action.Update);
					request.setKey(scanner.next());
					request.setValue(scanner.next());
					request.setLevel(Level.valueOf(scanner.next()));
					request.setInitiator(localHashCode);
				}else if(operator.equals("show-all")){
					request = new Request();
					request.setAction(Action.ShowAll);
					request.setInitiator(localHashCode);
				}else if(operator.equals("search")){
					request = new Request();
					request.setAction(Action.Search);
					request.setLevel(Level.All);
					request.setKey(scanner.next());
					request.setInitiator(localHashCode);
				}else if(operator.equals("exit")){
					System.out.println("Bye.");
					System.exit(-1);
				}
				
				if(request != null){
					//System.out.println(request);
					task = taskManager.registerTask(request);
					request.setInitiator(conf.getMachineHashCode());
					controller.execute(task);
				}
			}catch(Exception e){
				System.out.println("Invalid Input format.");	
			}
			request = null;
		}
	}
}
