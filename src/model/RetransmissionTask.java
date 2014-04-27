package model;

import java.util.TimerTask;

import strategy.ReliableUnicastSender;
public class RetransmissionTask extends TimerTask {
	private int _sequence = 0;
	private Member _member;
	private ReliableUnicastSender _owner;
	public RetransmissionTask(int seq, Member member, ReliableUnicastSender owner){
		this._sequence = seq;
		this._member = member;
		this._owner = owner;
	}

	public int getSequence(){
		return _sequence;
	}
	
	public Member getMember(){
		return _member;
	}

		
	@Override
	public void run() {
		//System.out.println("timer expire "+_sequence);
		_owner.resend(this);
	}
	
}
