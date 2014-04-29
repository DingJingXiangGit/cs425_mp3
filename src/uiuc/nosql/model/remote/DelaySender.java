package uiuc.nosql.model.remote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.TimerTask;

public class DelaySender extends TimerTask{
	private DatagramPacket _packet;
	private  DatagramSocket _socket;
	
	public DelaySender(DatagramPacket packet, DatagramSocket socket){
		_packet = packet;
		_socket = socket;
	}
	
	@Override
	public void run() {
		try {
			_socket.send(_packet);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}
