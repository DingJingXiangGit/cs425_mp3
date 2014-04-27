package strategy;

import model.Member;
import model.MemberIndexer;
import model.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

public class ReliableUnicastReceiver implements Runnable {
	private static int BUFFER_SIZE = 1024;
	private static ReliableUnicastReceiver _instance = new ReliableUnicastReceiver();
	
	private ReliableUnicastSender _unicastSender;
	private Map<Integer, Hashtable<Integer,Message>> _messageBufferTable;
	private Map<Integer, Integer> _nextSequenceTable;
	private DatagramSocket _socket;
	
	public static ReliableUnicastReceiver getInstance(){
		return _instance;
	}
	
	private ReliableUnicastReceiver(){
		_unicastSender = ReliableUnicastSender.getInstance();
		_messageBufferTable = new Hashtable<Integer, Hashtable<Integer, Message>>();
		_nextSequenceTable = new Hashtable<Integer, Integer>();
	}
	
	public void init(String ip, int port) {
		InetAddress address;
		try {
			address = InetAddress.getByName(ip);
			_socket = new DatagramSocket(port, address);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run(){
		byte[] buf;
		byte[] data;
		Message message;
		Member member;
		DatagramPacket packet;
		MemberIndexer memberIndexer;
		int senderId;
		
		memberIndexer = MemberIndexer.getInstance();
		while(true){
			buf = new byte[BUFFER_SIZE];
			packet = new DatagramPacket(buf, buf.length);
			
			try {
				_socket.receive(packet);
				data = Arrays.copyOf(packet.getData(), packet.getLength());
				packet.setLength(packet.getLength());
				message = Message.parse(data);
				senderId = message.getId();
				member = memberIndexer.getById(senderId);
				
				//System.out.println("receive: " + message.toString());
				if(message.getAction().equals("delivery")){
					int _nextReceiveSequence;
					Map<Integer, Message> _messageBuffer;
					
					if(_messageBufferTable.containsKey(senderId) == false){
						_messageBufferTable.put(senderId, new Hashtable<Integer, Message>());
					}
					if(_nextSequenceTable.containsKey(senderId) == false){
						_nextSequenceTable.put(senderId, 0);
					}
					_messageBuffer = _messageBufferTable.get(senderId);
					_nextReceiveSequence = _nextSequenceTable.get(senderId);
					//System.out.println("received sequence = "+message.getSequence()+" expected sequence = " + _nextReceiveSequence);
					_unicastSender.sendAck(message, member);
					if(message.getSequence() >= _nextReceiveSequence){
						//avoid duplicated message
						//_unicastSender.sendAck(message, member);
						if(message.getSequence() == _nextReceiveSequence){
							delivery(message);
							++_nextReceiveSequence;
							while(_messageBuffer.containsKey(_nextReceiveSequence)){
								delivery(_messageBuffer.get(_nextReceiveSequence));
								_messageBuffer.remove(_nextSequenceTable);
								++_nextReceiveSequence;
							}
						}else{
							if(_messageBuffer.containsKey(message.getSequence()) == false){
								_messageBuffer.put(message.getSequence(), message);
							}
						}
					}
					_nextSequenceTable.put(senderId, _nextReceiveSequence);
				} else if (message.getAction().equals("ack")) {
					//receive ack message, cancel retransmission
                    //System.out.println(message.toString());
					_unicastSender.ack(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void delivery(Message message){
		//if(message.getId() != Profile.getInstance().id){
		BasicMulticast basicMulticast = BasicMulticast.getInstance();
		basicMulticast.delivery(message.getContent());
		//}
	}
}
