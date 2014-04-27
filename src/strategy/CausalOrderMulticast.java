package strategy;

import model.CausalOrderMulticastMessage;
import model.IMessage;
import model.MemberIndexer;
import model.Profile;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CausalOrderMulticast {
	private Map<Integer, Integer[]> groupTimeVector;
	private static CausalOrderMulticast _instance = new CausalOrderMulticast();
	private BasicMulticast _basicMulticast;
	private Map<Integer, List<CausalOrderMulticastMessage>> holdbackQueueTable;
	private Object _mutex;
	private CausalOrderMulticast(){
		groupTimeVector = new Hashtable<Integer, Integer[]>();
		holdbackQueueTable = new Hashtable<Integer, List<CausalOrderMulticastMessage>>();
		_basicMulticast = BasicMulticast.getInstance();
		_mutex = new Object();
	}
	
	public static CausalOrderMulticast getInstance() {
		return _instance;
	}
	
	public void send(int groupId, String groupMessage) {
		synchronized(_mutex){
			Integer[] timeVector;
			CausalOrderMulticastMessage comm;
			int selfId = Profile.getInstance().getId();
			if(groupTimeVector.containsKey(groupId)==false){
				MemberIndexer memberIndexer = MemberIndexer.getInstance();
				int size = memberIndexer.getGroupSize(groupId);
				timeVector = new Integer[size];
				for(int i = 0; i < size; ++i){
					timeVector[i] = 0;
				}
			}else{
				timeVector = groupTimeVector.get(groupId);
			}
			timeVector[selfId] += 1;
			comm = new CausalOrderMulticastMessage();
			comm.setContent(groupMessage);
			comm.setTimeVector(timeVector.clone());
			comm.setSource(selfId);
			comm.setGroupId(groupId);
			_basicMulticast.send(groupId, comm);	
			groupTimeVector.put(groupId, timeVector);
			System.out.println("send: "+getTimeVectorString(timeVector) +": "+comm.getContent());
		}
	}

	public void delivery(IMessage message) {
		synchronized(_mutex){
			CausalOrderMulticastMessage comm = (CausalOrderMulticastMessage)message;
			if(comm.getSource() == Profile.getInstance().getId()){
				return;

			}
			List<CausalOrderMulticastMessage> holdbackQueue;
	//		List<CausalOrderMulticastMessage> deleteQueue;
			Integer[] timeVector = null;
			int groupId = -1;
			
			
			groupId = comm.getGroupId();
			System.out.println("enqueue message from: "+comm.getSource() +" with time vector: "+getTimeVectorString(comm.getTimeVector()) +": "+comm.getContent());
			if(holdbackQueueTable.containsKey(groupId) == false){
				holdbackQueueTable.put(groupId, new LinkedList<CausalOrderMulticastMessage>());
			}
			if(groupTimeVector.containsKey(groupId) == false){
				MemberIndexer memberIndexer = MemberIndexer.getInstance();
				int size = memberIndexer.getGroupSize(groupId);
				timeVector = new Integer[size];
				for(int i = 0; i < size; ++i){
					timeVector[i] = 0;
				}
				groupTimeVector.put(groupId, timeVector);
			}
			
			//deleteQueue = new LinkedList<CausalOrderMulticastMessage>();
			timeVector = groupTimeVector.get(comm.getGroupId());
			holdbackQueue = holdbackQueueTable.get(comm.getGroupId());
			holdbackQueue.add(comm);
			deliveryQueuedMessage(comm.getGroupId());
		}
	}
	
	private void deliveryQueuedMessage(int gourpId){
		//System.out.println("\n =======delivery start =======");
		boolean update = true;
		Integer[] timeVector;
		List<CausalOrderMulticastMessage> deleteQueue;
		List<CausalOrderMulticastMessage> holdbackQueue;
		deleteQueue = new LinkedList<CausalOrderMulticastMessage>();
		
		timeVector = groupTimeVector.get(gourpId);
		holdbackQueue = holdbackQueueTable.get(gourpId);
		while(update){
			update = false;
			boolean isReady = true;
			int sourceId = -1;
			for(CausalOrderMulticastMessage msg : holdbackQueue){
				Integer[] msgTimeVector = msg.getTimeVector();
				sourceId = msg.getSource();
				isReady = true;
				if(msgTimeVector[sourceId] == timeVector[sourceId] + 1){
					for(int i = 0; i < msgTimeVector.length; ++i){
						if(i != sourceId && msgTimeVector[i] > timeVector[i]){
							isReady = false;
							break;
						}
					}
				}else{
					isReady = false;
				}
				if(isReady){
					deleteQueue.add(msg);
					timeVector[sourceId] += 1;
					update = true;
					System.out.println("delivery source form " +msg.getSource() + " with time vector: "+getTimeVectorString(msg.getTimeVector()) +": "+msg.getContent());
					System.out.println("local time vector: "+getTimeVectorString(timeVector));
				}
			}
			holdbackQueue.removeAll(deleteQueue);
			groupTimeVector.put(gourpId, timeVector);
		}
		//System.out.println("======= delivery end =======\n");
	}
	
	private String getTimeVectorString(Integer[] timeVector) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		for(int i = 0; i < timeVector.length; ++i) {
			builder.append(timeVector[i]);
			if(i != timeVector.length - 1){
				builder.append(", ");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}
