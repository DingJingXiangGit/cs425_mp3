import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import uiuc.nosql.model.NodeConf;
import uiuc.nosql.model.remote.ServerNode;
import uiuc.nosql.view.Console;

public class NoSQL {
	public static void main(String args[]) throws FileNotFoundException{
		String config = args[0];
		int localHashCode = Integer.parseInt(args[1]);
		int replcas = Integer.parseInt(args[2]);
		Scanner scanner = new Scanner(new File(config));
		NodeConf conf = NodeConf.getInstance();
		conf.setMachineHashCode(localHashCode);
		conf.setReplicas(replcas);
		String[] delayTokens = args[3].split(",");
		for(int i = 0; i < delayTokens.length; ++i){
			String[] pairs = delayTokens[i].split(":");
			int hashCode = Integer.parseInt(pairs[0]);
			int delay = Integer.parseInt(pairs[1]);
			conf.insertDelayEntry(hashCode, delay);
		}
		
		
		String line;
		ServerNode node;
		while(scanner.hasNext()){
			line = scanner.next();
			if(line.equals("exit")){
				break;
			}
			node = new ServerNode(line);
			conf.insertServerNode(node);
		}
		scanner.close();
		
		Console console = new Console();
		console.start();
	}
}
