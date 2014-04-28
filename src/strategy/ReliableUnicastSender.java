package strategy;

import model.Member;
import model.Message;
import model.Profile;
import model.RetransmissionTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class ReliableUnicastSender {
	private static int EXPIRE_TIME = 2000;
	private static ReliableUnicastSender _instance = new ReliableUnicastSender();
	
	private DatagramSocket _socket;
	private Map<Integer, Hashtable<Integer, Timer>> _timerTable;
	private Map<Integer, Hashtable<Integer, Message>> _cachedMessages;
	private Map<Integer, Hashtable<Integer, TimerTask>> _cachedRetransmissionTask;
	private Map<Integer, Integer> _nextSendSequence;
	private Profile _profile;
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
		_profile = Profile.getInstance();
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
	public void send(Message message, Member member){
		Timer timer;
		TimerTask timerTask;
		InetAddress address;
		byte[] data;
		int memberId;
		int sendSequence;
		DatagramPacket sendPacket;
		memberId = member.getId();
		synchronized(_mutex){
            // Check if the message or its sender are new
			if(_nextSendSequence.containsKey(memberId) == false){
				_nextSendSequence.put(memberId, 0);
			}
			if(_timerTable.containsKey(memberId) == false){
				_timerTable.put(memberId, new Hashtable<Integer, Timer>());
			}
			
			if(_cachedMessages.containsKey(memberId) == false){
				_cachedMessages.put(memberId, new Hashtable<Integer, Message>());
			}
			
			if(_cachedRetransmissionTask.containsKey(memberId) == false){
				_cachedRetransmissionTask.put(memberId, new Hashtable<Integer, TimerTask>());
			}
			
			sendSequence = _nextSendSequence.get(memberId);
			try {
				
				message.setSequence(new Integer(sendSequence));

                data =  message.getBytes();
				address = InetAddress.getByName(member._ip);
				sendPacket = new DatagramPacket(data, data.length, address,  member._port);
				timer = new Timer();
				timerTask = new RetransmissionTask(new Integer(sendSequence), member, this);
				_cachedMessages.get(memberId).put(new Integer(sendSequence), message);
				_timerTable.get(memberId).put(new Integer(sendSequence), timer);
				_cachedRetransmissionTask.get(memberId).put(new Integer(sendSequence), timerTask);

                // Delay based on input argument
                int meanDelay = member.getDelaySeconds();
                if (meanDelay != 0) {
                    Timer delayTimer = new Timer();
                    TimerTask delaySender = new DelaySender(sendPacket, _socket);
                    delayTimer.schedule(delaySender, getRandomDelay(meanDelay));
                }else{
                	_socket.send(sendPacket);
                }
                if(_profile.isDetailMode){
                	System.out.println(message.getContent().getContent() + " sent");
                }

                // Set up retransmission in case unicast doesn't reach
				timer.schedule(timerTask, EXPIRE_TIME);
				_nextSendSequence.put(memberId, sendSequence + 1);
			}
            catch (IOException e) {
				e.printStackTrace();
			}
        }
	}

    /*
        The retransmission thread handler
     */
	public void resend(RetransmissionTask task) {
		Timer timer;
		Message message;
		Member member;
		int sequence;
		byte[] data;
		DatagramPacket sendPacket;
		InetAddress address;
		TimerTask timerTask;

		member = task.getMember();
		sequence = task.getSequence();

		synchronized(_mutex){
			message = _cachedMessages.get(member.getId()).get(sequence);
			timer = _timerTable.get(member.getId()).get(sequence);
            if (timer == null) {
                return;
            }

			task.cancel();
			timer.cancel();
			timer.purge();
			message.getId();
			timer = new Timer();
			timerTask = new RetransmissionTask(sequence, member, this);
			data =  message.getBytes();
			try {
				address = InetAddress.getByName(member.getIP());
				sendPacket = new DatagramPacket(data, data.length, address,  member.getPort());
				_timerTable.get(member.getId()).remove(sequence);
				_timerTable.get(member.getId()).put(sequence, timer);
				_cachedRetransmissionTask.get(member.getId()).put(sequence, timerTask);
				
				double rate = _rand.nextDouble();
					
				int meanDelay = member.getDelaySeconds();
                if (meanDelay != 0) {
                    Timer delayTimer = new Timer();
                    TimerTask delaySender = new DelaySender(sendPacket, _socket);
                    delayTimer.schedule(delaySender, getRandomDelay(meanDelay));
                }else{
                	_socket.send(sendPacket);
                }
                
                if(_profile.isDetailMode){
                	System.out.println(message.getContent().getContent() + " resend ");
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
		int memberId = message.getId();
		int sequence = message.getSequence();
		// System.out.println("[start]ack sequence target " + memberId + " sequence" + sequence);
		synchronized(_mutex){
			timer = _timerTable.get(memberId).get(sequence);
			task = _cachedRetransmissionTask.get(memberId).get(sequence);
			if(timer != null){
				task.cancel();
				timer.cancel();
				timer.purge();
				_timerTable.get(memberId).remove(sequence);
				_cachedMessages.get(memberId).remove(sequence);
				_cachedRetransmissionTask.get(memberId).remove(sequence);
			}else{
				//System.out.println("null timer.");
			}
		}
		//System.out.println("[end]ack sequence "+sequence);
	}

    /*
        Reply back acknowledging the given message
    */
	public void sendAck(Message msg, Member member) throws IOException {
		InetAddress address;
		Message message;
		byte[] data;
		DatagramPacket sendPacket;
		
		message = new Message(msg);
		message.setId(Profile.getInstance().id);
		message.setAction("ack");
		message.setSequence(msg.getSequence());
		data =  message.getBytes();
		address = InetAddress.getByName(member._ip);
		sendPacket = new DatagramPacket(data, data.length, address,  member._port);
		
		int meanDelay = member.getDelaySeconds();
        if (meanDelay != 0) {
            Timer delayTimer = new Timer();
            TimerTask delaySender = new DelaySender(sendPacket, _socket);
            delayTimer.schedule(delaySender, getRandomDelay(meanDelay));
        }else{
        	_socket.send(sendPacket);
        }

		if(_profile.isDetailMode){
			System.out.println("ack message sent.");
		}
	}
}

