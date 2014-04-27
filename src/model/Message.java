package model;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Message")
public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1823741L;

	@XmlElement(name="Content")
	private BasicMulticastMessage _content;
	
	@XmlElement(name="Sequence")
	private int _sequence;
	
	@XmlElement(name="ID")
	private int _id;
	
	@XmlElement(name="Action")
	private String _action;
	
	public Message(){
		
	}
	
	public BasicMulticastMessage getContent(){
		return _content;
	}
	public int getSequence(){
		return _sequence;
	}
	
	public int getId(){
		return _id;
	}
	
	public String getAction(){
		return _action;
	}
	
	public void setContent(BasicMulticastMessage content){
		this._content = content;
	}
	
	public void setSequence(int sequence){
		//System.out.println("original "+this._sequence +" to " +sequence );
		this._sequence = sequence;
	}
	
	public void setId(int id){
		this._id = id;
	}
	
	public void setAction(String action){
		this._action = action;
	}
	
	public Message(Message msg) {
		this._content =msg._content;
		this._id = msg._id;
		this._sequence = msg._sequence;
		this._action = msg._action;
	}

	public byte[] getBytes(){
		ObjectOutputStream out = null;
	    ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toString(){
		return String.format("{sequence:%d, id:%d, action:%s, content:%s}", _sequence, _id, _action, _content);
	}
	
	public static Message parse(byte[] content){
		ByteArrayInputStream bis;
		ObjectInput in;
		try {
			bis = new ByteArrayInputStream(content);
		    in = new ObjectInputStream(bis);
			return (Message)in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getAckString(){
		return String.format("{%d, %d, ack, null}", _sequence,_id);
	}
}
