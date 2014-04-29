package uiuc.nosql.model;

import java.util.List;

public class Response {
	private List<Tuple> tuples;
	private int source;
	
	
	public List<Tuple> getTuples() {
		return tuples;
	}
	public void setTuples(List<Tuple> tuples) {
		this.tuples = tuples;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	
}
