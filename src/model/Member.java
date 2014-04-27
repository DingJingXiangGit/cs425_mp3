package model;
public class Member {
	public String _ip;
	public int _id;
	public int _port;
	public String _userName;
	public int _groupId;
	
	
	public Member(){
		_groupId = -1;
		_id = -1;
	}

	public String getIP(){
		return _ip;
	}
	
	public int getId(){
		return _id;
	}

	public int getPort(){
		return _port;
	}
	
	public String getUsername(){
		return _userName;
	}
}
