package main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import strategy.ReliableUnicastReceiver;
import model.Member;
import model.MemberIndexer;
import model.Profile;

public class Server {
	private static ReliableUnicastReceiver _receiver;

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("usage: java Server [config file] [id] [delay1] .. [delayN]");
			return;
		}
		
		List<Member> members = new ArrayList<Member>();
		Profile profile = Profile.getInstance();
		MemberIndexer memberIndexer = MemberIndexer.getInstance();
		
		try {
			int selfId = Integer.parseInt(args[1]);
			int delayIndex = 2;

			Scanner scanner = new Scanner(new File(args[0]));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split(" ");
				
				Member member = new Member();
				member._id =  Integer.parseInt(parts[0]);
				member._ip =  parts[1];
				member._port = Integer.parseInt(parts[2]);
				member._name = parts[3];
				member._delaySeconds = Integer.parseInt(args[delayIndex]);
				members.add(member);
				delayIndex++;
				
				if(member.getId() == selfId){
					profile.id = selfId;
					profile.ip = parts[1];
					profile.port = Integer.parseInt(parts[2]);
					profile.name = parts[3];
				}
			}
			
			memberIndexer.addMembers(members);
		} catch (Exception e) {
			System.out.println("failed parsing arguments");
			return;
		}

        // Set up networking
		_receiver = ReliableUnicastReceiver.getInstance(); 
		_receiver.init(profile.ip, profile.port);
		
		Scanner userInput = new Scanner(System.in);
		while (userInput.hasNextLine()) {
			String line = userInput.nextLine();
			String parts[] = line.split(" ");
			String command = parts[0].toLowerCase();
			
			switch (command) {
			case "delete":
				break;
			case "get":
				break;
			case "insert":
				break;
			case "update":
				break;
			case "show-all":
				break;
			case "search":
				break;
			case "quit":
				return;
			default:
				System.out.println("command not supported");
			}
		}
	}

}
