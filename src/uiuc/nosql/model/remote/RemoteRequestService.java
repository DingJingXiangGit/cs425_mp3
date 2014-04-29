package uiuc.nosql.model.remote;

import java.util.Map;

import uiuc.nosql.model.Request;
import uiuc.nosql.model.NodeConf;

public class RemoteRequestService{
	private ReliableUnicastSender unicastSender;
	private Map<Integer, ServerNode> serverNodes;
	private int localHashCode;
	
	public RemoteRequestService(){
		unicastSender = ReliableUnicastSender.getInstance();
		serverNodes = NodeConf.getInstance().getServerNodes();
		localHashCode = NodeConf.getInstance().getMachineHashCode();
	}
	
	public void execute(Request command, int hashCode){
		ServerNode replica = serverNodes.get(hashCode);
		Message message = new Message();
		message.setAction("delivery");
		message.setContent(command);
		message.setId(localHashCode);
		message.setSequence(-1);
		unicastSender.send(message, replica);
	}
}
