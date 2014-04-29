package uiuc.nosql.model.remote;

import uiuc.nosql.controller.DatabaseController;
import uiuc.nosql.model.Request;
import uiuc.nosql.model.NodeConf;

public class RemoteListenService {
	private ReliableUnicastReceiver unicastReceiver;
	private NodeConf nodeConf;
	private int localHashCode;
	private ServerNode machine;
	private Thread listenThread;

	
	public RemoteListenService(){
		nodeConf = NodeConf.getInstance();
		localHashCode = nodeConf.getMachineHashCode();
		machine = nodeConf.getServerNodes().get(localHashCode);
		unicastReceiver = ReliableUnicastReceiver.getInstance(); 
		unicastReceiver.init(machine.getIp(), machine.getPort(), this);
		listenThread = new Thread(unicastReceiver);
	}
	
	public void acceptMessage(Message message){
		DatabaseController database;
		database = DatabaseController.getInstance();
		Request command = message.getContent();
		database.execute(command);
	}
	
	public void start(){
		listenThread.start();
	}
	
	public void stop(){
		try {
			listenThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
