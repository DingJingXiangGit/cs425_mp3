package uiuc.nosql.model;
import java.util.*;

public class DataStoreService {
	private Map<String, Tuple> storage;
	private Object mutex;
	
	private static DataStoreService instance = new DataStoreService();
	
	private DataStoreService(){
		storage = new HashMap<String, Tuple>();
		mutex  = new Object();
	}
	
	public static DataStoreService getInstance(){
		return instance;
	}
	
	public void insert(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			if(this.storage.containsKey(key)){
				Tuple item = this.storage.get(key);
				if(item.getTimestamp() < tuple.getTimestamp()){
					this.storage.put(key, tuple);
				}
			}else{
				this.storage.put(key, tuple);
			}
		}
	}
	
	public void delete(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			//System.out.println("delete = "+key);
			if(this.storage.containsKey(key)){
				Tuple item = this.storage.get(key);
				if(item.getTimestamp() < tuple.getTimestamp()){
					this.storage.remove(key);
				}
			}else{
				this.storage.remove(key);
			}
		}
	}
	
	public Tuple get(String key){
		synchronized(mutex){
			//System.out.println("get = "+key);
			return this.storage.get(key);
		}
	}
	
	public void update(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			//System.out.println("update = "+tuple);
			if(this.storage.containsKey(key)){
				Tuple item = this.storage.get(key);
				if(item.getTimestamp() < tuple.getTimestamp()){
					this.storage.put(key, tuple);
				}
			}else{
				this.storage.put(key, tuple);
			}
		}
	}
	
	public List<Tuple> showAll(){
		synchronized(mutex){
			return new ArrayList<Tuple>(this.storage.values());
		}
	}
}
