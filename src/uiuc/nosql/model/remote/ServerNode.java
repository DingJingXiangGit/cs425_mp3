package uiuc.nosql.model.remote;

public class ServerNode {
	private int hashCode;
	private String ip;
	private int port;
	private String id;
	
	public ServerNode(String entry){
		String[] tokens = entry.split(",");
		System.out.println(entry);
		this.id = tokens[0].trim();
		this.hashCode = Integer.parseInt(tokens[1].trim());
		this.ip = tokens[2].trim();
		this.port = Integer.parseInt(tokens[3].trim());
	}
	
	public int getHashCode() {
		return hashCode;
	}
	
	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public String toString(){
		return String.format("server #%d address:%s:%d", hashCode, ip, port);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
