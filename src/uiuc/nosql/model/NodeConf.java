package uiuc.nosql.model;
import java.util.*;

import uiuc.nosql.model.remote.ServerNode;

public class NodeConf {
	private int machineHashCode;
	private int replicas;
	private Map<Integer, ServerNode> serverNodes;
	private List<Integer> nodeIds;
	private static NodeConf instance;
	private int delay;
	private double dropRate;
	private boolean isDetailMode;

	private NodeConf(){
		serverNodes = new HashMap<Integer, ServerNode>();
		nodeIds = new ArrayList<Integer>();
		isDetailMode = false;
	}
	
	public static NodeConf getInstance(){
		if(instance == null){
			instance = new NodeConf();
		}
		return instance;
	}
	
	public void setMachineHashCode(int hashCode){
		machineHashCode = hashCode;
	}
	
	public int getMachineHashCode(){
		return machineHashCode;
	}
	
	public void insertServerNode(ServerNode node){
		serverNodes.put(node.getHashCode(), node);
		nodeIds.add(node.getHashCode());
	}
	
	public List<Integer> getNodeIds() {
		return nodeIds;
	}

	
	public ServerNode getServerNode(int hashCode){
		return serverNodes.get(hashCode);
	}
	
	public Map<Integer, ServerNode> getServerNodes(){
		return serverNodes;
	}

	public void setReplicas(int replicas) {
		this.replicas = replicas;
	}
	
	public int getReplicas(){
		return this.replicas;
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

	public boolean isDetailMode() {
		return isDetailMode;
	}

	public void setDetailMode(boolean isDetailMode) {
		this.isDetailMode = isDetailMode;
	}
}
