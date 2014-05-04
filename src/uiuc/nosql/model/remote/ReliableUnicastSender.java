package uiuc.nosql.model.remote;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

import uiuc.nosql.model.NodeConf;

public class ReliableUnicastSender {
	private static int EXPIRE_TIME = 2000;
	private static ReliableUnicastSender _instance = new ReliableUnicastSender();
	
	private DatagramSocket _socket;
	private Map<Integer, Hashtable<Integer, Timer>> _timerTable;
	private Map<Integer, Hashtable<Integer, Message>> _cachedMessages;
	private Map<Integer, Hashtable<Integer, TimerTask>> _cachedRetransmissionTask;
	private Map<Integer, Integer> _nextSendSequence;
	private NodeConf _nodeConf;
	private Random _rand;
	private Object _mutex;
	
	public static ReliableUnicastSender getInstance(){
		return _instance;
	}

	private ReliableUnicastSender(){
		_mutex = new Object();
		_cachedMessages = new Hashtable<Integer, Hashtable<Integer, Message>> ();
		_timerTable =  new Hashtable<Integer, Hashtable<Integer, Timer>> ();
		_nextSendSequence = new Hashtable<Integer, Integer>();
		_cachedRetransmissionTask = new Hashtable<Integer, Hashtable<Integer, TimerTask>>();
		_nodeConf = NodeConf.getInstance();
		_rand = new Random();
		try{
			_socket = new DatagramSocket();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

    private long getRandomDelay(int meanDelay) {
        int variance = meanDelay / 2;
        double randomizedDelay = meanDelay + _rand.nextGaussian() * variance;
        return (long)Math.max(randomizedDelay, 1);
    }

    /*
        Unicast send
        Also retransmits in case of packet drops
     */
	public void send(Message message, ServerNode replica){
		Timer timer;
		TimerTask timerTask;
		InetAddress address;
		byte[] data;
		int replicaHashCode;
		int sendSequence;
		DatagramPacket sendPacket;
		replicaHashCode = replica.getHashCode();
		
		synchronized(_mutex){
            // Check if the message or its sender are new
			if(_nextSendSequence.containsKey(replicaHashCode) == false){
				_nextSendSequence.put(replicaHashCode, 0);
			}
			if(_timerTable.containsKey(replicaHashCode) == false){
				System.out.println("send to replica := "+replica);
				_timerTable.put(replicaHashCode, new Hashtable<Integer, Timer>());
			}
			
			if(_cachedMessages.containsKey(replicaHashCode) == false){
				_cachedMessages.put(replicaHashCode, new Hashtable<Integer, Message>());
			}
			
			if(_cachedRetransmissionTask.containsKey(replicaHashCode) == false){
				_cachedRetransmissionTask.put(replicaHashCode, new Hashtable<Integer, TimerTask>());
			}
			
			sendSequence = _nextSendSequence.get(replicaHashCode);
			try {
				
				message.setSequence(new Integer(sendSequence));

                data =  message.getBytes();
				address = InetAddress.getByName(replica.getIp());
				sendPacket = new DatagramPacket(data, data.length, address,  replica.getPort());
				timer = new Timer();
				timerTask = new MessageRetransmitter(new Integer(sendSequence), replica, this);
				_cachedMessages.get(replicaHashCode).put(new Integer(sendSequence), message);
				_timerTable.get(replicaHashCode).put(new Integer(sendSequence), timer);
				_cachedRetransmissionTask.get(replicaHashCode).put(new Integer(sendSequence), timerTask);

                // Only send with some probability
				if (_rand.nextDouble() >= _nodeConf.getDropRate()) {
                    // Delay based on input argument
                    int meanDelay = _nodeConf.getDelay(replicaHashCode);//_nodeConf.getDelay();
                    if (meanDelay != 0) {
                    	long trueDelay = getRandomDelay(meanDelay);
                    	//System.out.println("true delay is:"+trueDelay);
                        Timer delayTimer = new Timer();
                        TimerTask delaySender = new DelaySender(sendPacket, _socket);
                        delayTimer.schedule(delaySender,trueDelay);
                    }else{
                    	_socket.send(sendPacket);
                    }
                    if(_nodeConf.isDetailMode()){
                    	System.out.println(message.getContent() + " sent");
                    }
				}else{
					if(_nodeConf.isDetailMode()){
						System.out.println(message.getContent() + " dropped");
					}
				}
                // Set up retransmission in case unicast doesn't reach
				timer.schedule(timerTask, EXPIRE_TIME);
				_nextSendSequence.put(replicaHashCode, sendSequence + 1);
			}
            catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

    /*
        The retransmission thread handler
     */
	public void resend(MessageRetransmitter task) {
		Timer timer;
		Message message;
		ServerNode replicaNode;
		int sequence;
		byte[] data;
		DatagramPacket sendPacket;
		InetAddress address;
		TimerTask timerTask;
		int replicaHashCode;

		replicaNode = task.getMember();
		sequence = task.getSequence();

		synchronized(_mutex){
			replicaHashCode = replicaNode.getHashCode();
			message = _cachedMessages.get(replicaHashCode).get(sequence);
			timer = _timerTable.get(replicaHashCode).get(sequence);
            if (timer == null) {
                return;
            }
            //System.out.println("resend message "+message);
			task.cancel();
			timer.cancel();
			timer.purge();
			
			//message.getId();
			
			timer = new Timer();
			timerTask = new MessageRetransmitter(sequence, replicaNode, this);
			data =  message.getBytes();
			try {
				address = InetAddress.getByName(replicaNode.getIp());
				sendPacket = new DatagramPacket(data, data.length, address,  replicaNode.getPort());
				_timerTable.get(replicaHashCode).remove(sequence);
				_timerTable.get(replicaHashCode).put(sequence, timer);
				_cachedRetransmissionTask.get(replicaHashCode).put(sequence, timerTask);
				
				double rate = _rand.nextDouble();
				if (rate >= _nodeConf.getDropRate()) {
					
					int meanDelay = _nodeConf.getDelay(replicaHashCode);
                    if (meanDelay != 0) {
                    	long trueDelay = getRandomDelay(meanDelay);
                    	//System.out.println("resend delay: "+trueDelay);
                        Timer delayTimer = new Timer();
                        TimerTask delaySender = new DelaySender(sendPacket, _socket);
                        delayTimer.schedule(delaySender, trueDelay);
                    }else{
                    	_socket.send(sendPacket);
                    }
					//_socket.send(sendPacket);
                    if(_nodeConf.isDetailMode()){
                    	System.out.println(message.getContent()+ " resend ");
                    }
				}else{
					if(_nodeConf.isDetailMode()){
						System.out.println(message.getContent()+ " resend dropped ");
					}
				}
				timer.schedule(timerTask, EXPIRE_TIME);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    /*
        Acknowledge the message was sent
        Stop tracking and resending
     */
	public void ack(Message message) {
		Timer timer;
		TimerTask task;
		int replicaHashCode = message.getId();
		int sequence = message.getSequence();
		//System.out.println("[start]ack sequence target: " + message);
		synchronized(_mutex){
			//System.out.println("memberId is?= "+replicaHashCode);
			//System.out.println("timer table size = "+_timerTable.get(replicaHashCode).size());
			timer = _timerTable.get(replicaHashCode).get(sequence);
			task = _cachedRetransmissionTask.get(replicaHashCode).get(sequence);
			if(timer != null){
				task.cancel();
				timer.cancel();
				timer.purge();
				_timerTable.get(replicaHashCode).remove(sequence);
				_cachedMessages.get(replicaHashCode).remove(sequence);
				_cachedRetransmissionTask.get(replicaHashCode).remove(sequence);
			}else{
//				System.out.println("null timer.");
			}
		}
		//System.out.println("[end]ack sequence "+sequence);

	}

    /*
        Reply back acknowledging the given message
    */
	public void sendAck(Message msg, ServerNode member) throws IOException {
		InetAddress address;
		Message message;
		byte[] data;
		DatagramPacket sendPacket;
		double rate = 0.0;
		int hashCode = -1;
		message = new Message(msg);
		message.setId(_nodeConf.getMachineHashCode());
		message.setAction("ack");
		message.setSequence(msg.getSequence());
		data =  message.getBytes();
		address = InetAddress.getByName(member.getIp());
		sendPacket = new DatagramPacket(data, data.length, address,  member.getPort());
		rate = _rand.nextDouble();
		hashCode = member.getHashCode();
		
		if (rate >= _nodeConf.getDropRate()) {
			int meanDelay = _nodeConf.getDelay(hashCode);
            if (meanDelay != 0) {
                Timer delayTimer = new Timer();
                TimerTask delaySender = new DelaySender(sendPacket, _socket);
                delayTimer.schedule(delaySender, getRandomDelay(meanDelay));
            }else{
            	_socket.send(sendPacket);
            }
            //_socket.send(sendPacket);
			if(_nodeConf.isDetailMode()){
				System.out.println("ack message sent.");
			}
		}else{
			if(_nodeConf.isDetailMode()){
				System.out.println("ack message dropped.");
			}
		}
	}
}

