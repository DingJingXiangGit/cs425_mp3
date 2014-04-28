package model;

public class Profile {
	private static Profile _instance = new Profile();
	
	public int id;
	public int port;
	public String ip;
	public String name;
	public boolean isDetailMode = false;
	public boolean isBoost;

	private Profile(){
		
	}
	
	public static Profile getInstance(){
		return _instance;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
