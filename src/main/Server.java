package main;

import java.io.File;
import java.util.Scanner;

import model.Member;

public class Server {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("usage: java Server [config file] [id] [delay1] [delay2] [delay3]");
			return;
		}
		
		try {
			int id = Integer.parseInt(args[1]);
			int delay1 = Integer.parseInt(args[2]);
			int delay2 = Integer.parseInt(args[3]);
			int delay3 = Integer.parseInt(args[4]);

			Scanner scanner = new Scanner(new File(args[0]));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] parts = line.split(" ");
				Member member = new Member();
				member._id =  Integer.parseInt(parts[0]);
				member._ip =  parts[1];
				member._port = Integer.parseInt(parts[2]);
				member._userName = parts[3];
			}
		} catch (Exception e) {
			System.out.println("failed parsing arguments");
			return;
		}
		
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
