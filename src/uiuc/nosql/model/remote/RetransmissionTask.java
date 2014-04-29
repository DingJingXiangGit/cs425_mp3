package uiuc.nosql.model.remote;

import java.util.TimerTask;

public class RetransmissionTask extends TimerTask {
	private int _sequence = 0;
	private ServerNode _member;
	private ReliableUnicastSender _owner;
	public RetransmissionTask(int seq, ServerNode member, ReliableUnicastSender owner){
		this._sequence = seq;
		this._member = member;
		this._owner = owner;
	}

	public int getSequence(){
		return _sequence;
	}
	
	public ServerNode getMember(){
		return _member;
	}

		
	@Override
	public void run() {
		//System.out.println("timer expire "+_sequence);
		_owner.resend(this);
	}
	
}
