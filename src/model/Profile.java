package model;

public class Profile {
	public int id;
	public int port;
	public String ip;
	public String name;
	public int delay;
	public double dropRate;
	public MulticastType multicastType;
	public boolean isDetailMode = false;
	public boolean isBoost;
	public MulticastType getMulticastType() {
		return multicastType;
	}

	public void setMulticastType(MulticastType multicastType) {
		this.multicastType = multicastType;
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

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public double getDropRate() {
		return dropRate;
	}

	public void setDropRate(double dropRate) {
		this.dropRate = dropRate;
	}

	private static Profile _instance = new Profile();
	private Profile(){
		
	}
	
	public static Profile getInstance(){
		return _instance;
	}
}
