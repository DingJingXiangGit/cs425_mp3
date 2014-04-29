package uiuc.nosql.view;
import java.io.*;
import java.util.Scanner;

import uiuc.nosql.controller.DatabaseController;
import uiuc.nosql.model.Action;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.Level;
public class Console {
	private DatabaseController controller;
	public Console(){
		controller = DatabaseController.getInstance();
	}
	
	public void start(){
		Scanner scanner = new Scanner(System.in);
		String operator = null;
		Request command = null;
		while(scanner.hasNext()){
			operator = scanner.next();
			if(operator.equals("delete")){
				command = new Request();
				command.setAction(Action.Delete);
				command.setKey(scanner.next());
			}else if(operator.equals("get")){
				command = new Request();
				command.setAction(Action.Get);
				command.setKey(scanner.next());
				command.setLevel(Level.valueOf(scanner.next()));
			}else if(operator.equals("insert")){
				command = new Request();
				command.setAction(Action.Insert);
				command.setKey(scanner.next());
				command.setValue(scanner.next());
				command.setLevel(Level.valueOf(scanner.next()));
			}else if(operator.equals("update")){
				command = new Request();
				command.setAction(Action.Update);
				command.setKey(scanner.next());
				command.setValue(scanner.next());
				command.setLevel(Level.valueOf(scanner.next()));
			}else if(operator.equals("show-all")){
				command = new Request();
				command.setAction(Action.ShowAll);
			}
			if(command != null){
				System.out.println(command);
				controller.execute(command);
			}
			command = null;
		}
	}
}
