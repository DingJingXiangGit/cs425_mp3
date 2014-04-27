package strategy;

import model.*;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class BasicMulticast{
	private static BasicMulticast _instance = new BasicMulticast();
	
	private Map<Integer, Integer> _sourceGroupSequence;
	private ReliableUnicastSender _sender;

	private BasicMulticast(){
		_sourceGroupSequence = new Hashtable<Integer, Integer>();
		_sender = ReliableUnicastSender.getInstance();
	}

	public static BasicMulticast getInstance(){
		return _instance;
	}

	public void send(int groupId, IMessage content) {
		BasicMulticastMessage message = new BasicMulticastMessage();
		message.content = content;
		message.groupId = groupId;
		if(_sourceGroupSequence.containsKey(groupId) == false){
			_sourceGroupSequence.put(groupId, 0);
		}
		message.sourceGroupSequence = _sourceGroupSequence.get(groupId);
		_sourceGroupSequence.put(groupId, message.sourceGroupSequence + 1);
		multicast(groupId, message);
	}
	
	public void reply(int groupId, int source, IMessage content){
		MemberIndexer memberIndexer;
		Map<Integer, Member> groupMembers;
		Profile profile;
		Message message;
		BasicMulticastMessage basicMessage;
		
		basicMessage = new BasicMulticastMessage();
		basicMessage.content = content;
		basicMessage.groupId = groupId;
		if(_sourceGroupSequence.containsKey(groupId) == false){
			_sourceGroupSequence.put(groupId, 0);
		}
		basicMessage.sourceGroupSequence = _sourceGroupSequence.get(groupId);
		
		memberIndexer = MemberIndexer.getInstance();
		groupMembers = memberIndexer.getByGroupId(groupId);
		profile = Profile.getInstance();
		message = new Message();
		message.setAction("delivery");
		message.setContent(basicMessage);
		message.setId(profile.getId());
		
		_sender.send(message, groupMembers.get(source));
	}

    /*
        Unicast send the message to all group members
     */
	private void multicast(int groupId, BasicMulticastMessage data ){
		MemberIndexer memberIndexer = MemberIndexer.getInstance();
		Map<Integer, Member> groupMembers = memberIndexer.getByGroupId(groupId);
		Profile profile = Profile.getInstance();

        for(Entry<Integer, Member> entry: groupMembers.entrySet()){
			Message message = new Message();
			message.setAction("delivery");
			message.setContent(data);
			message.setId(profile.getId());
			_sender.send(message, entry.getValue());
		}
	}

    /*
        Deliver message based on total order and type of process
     */
	public void delivery(BasicMulticastMessage message) {
        if (Profile.getInstance().getMulticastType() == MulticastType.CausalOrder){
			CausalOrderMulticast com = CausalOrderMulticast.getInstance();
			com.delivery(message.getContent());
		} else if (Profile.getInstance().getMulticastType() == MulticastType.TotalOrder){
			TotalOrderMulticast totalOrderMulticast = TotalOrderMulticast.getInstance();
			totalOrderMulticast.delivery(message.getContent());
        }
		
	}
}
