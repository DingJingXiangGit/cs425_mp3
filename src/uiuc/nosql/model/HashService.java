package uiuc.nosql.model;

import java.util.List;

public class HashService {
	private int nodes;
	
	public int getNodes() {
		return nodes;
	}

	public void setNodes(int nodes) {
		this.nodes = nodes;
	}

	public HashService(int nodes){
		this.nodes = nodes;
	}
	
	public int hash(int key){
		return key % nodes;
	}
	
	
	public int hash(long key){
		return (int)(key%nodes);
	}
	
	public int hash(short key){
		return (int)(key%nodes);
	}
	
	
	public int[] hash(String key, int numReplicas){
		List<Integer> nodeHashCodes = NodeConf.getInstance().getNodeIds();
		int[] hashCodes = new int[numReplicas];
		for(int i = 0; i < numReplicas;++i){
			hashCodes[i] = nodeHashCodes.get((key.hashCode() + i) % nodes);
		}
		return hashCodes;
	}
	
}
