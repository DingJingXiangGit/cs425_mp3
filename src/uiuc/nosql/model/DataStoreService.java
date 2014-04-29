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
			System.out.println("add tuple = "+tuple);
			this.storage.put(key, tuple);
		}
	}
	
	public void delete(String key){
		synchronized(mutex){
			System.out.println("delete = "+key);
			this.storage.remove(key);
		}
	}
	
	public Tuple get(String key){
		synchronized(mutex){
			System.out.println("get = "+key);
			return this.storage.get(key);
		}
	}
	
	public void update(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			System.out.println("update = "+tuple);
			this.storage.put(key, tuple);
		}
	}
	
	public List<Tuple> showAll(){
		synchronized(mutex){
			return new ArrayList<Tuple>(this.storage.values());
		}
	}
}
