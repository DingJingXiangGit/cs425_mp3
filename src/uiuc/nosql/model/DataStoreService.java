package uiuc.nosql.model;
import java.util.*;

public class DataStoreService {
	private Map<String, Tuple> storage;
	private Map<String, Long> ghostTable;
	private Object mutex;
	
	private static DataStoreService instance = new DataStoreService();
	
	private DataStoreService(){
		storage = new HashMap<String, Tuple>();
		ghostTable = new HashMap<String, Long>();
		mutex  = new Object();
	}
	
	public static DataStoreService getInstance(){
		return instance;
	}
	
	public boolean exist(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			return this.storage.containsKey(key);
		}
	}
	
	public void insert(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			if(this.storage.containsKey(key) == true){
				Tuple item = this.storage.get(key);
				if(item.getTimestamp() < tuple.getTimestamp()){
					this.storage.put(key, tuple);
				}
			}else{
				if(ghostTable.containsKey(key)==false){
					this.storage.put(key, tuple);
				}else{
					if(ghostTable.get(key) < tuple.getTimestamp()){
						this.storage.put(key, tuple);
					}
				}
			}
		}
	}
	
	public void delete(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			if(this.storage.containsKey(key)){
				Tuple item = this.storage.get(key);
				if(item.getTimestamp() < tuple.getTimestamp()){
					this.storage.remove(key);
					this.ghostTable.put(key, tuple.getTimestamp());
				}
			}else{
				this.storage.remove(key);
				this.ghostTable.put(key, tuple.getTimestamp());
			}
		}
	}
	
	public Tuple get(String key){
		synchronized(mutex){
			return this.storage.get(key);
		}
	}
	
	public void update(Tuple tuple){
		String key = tuple.getKey();
		synchronized(mutex){
			if(this.storage.containsKey(key)){
				Tuple item = this.storage.get(key);
				if(item.getTimestamp() < tuple.getTimestamp()){
					this.storage.put(key, tuple);
				}
			}else{
				if(ghostTable.containsKey(key)==false){
					this.storage.put(key, tuple);
				}else{
					if(ghostTable.get(key) < tuple.getTimestamp()){
						this.storage.put(key, tuple);
					}
				}
			}
		}
	}
	
	public List<Tuple> showAll(){
		synchronized(mutex){
			return new ArrayList<Tuple>(this.storage.values());
		}
	}
}
