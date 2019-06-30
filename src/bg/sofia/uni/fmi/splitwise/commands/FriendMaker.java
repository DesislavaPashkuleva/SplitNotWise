package bg.sofia.uni.fmi.splitwise.commands;

import bg.sofia.uni.fmi.splitwise.server.Server;

import java.io.PrintWriter;

public class FriendMaker {
	
	private String username;
	private PrintWriter writer;
	private Server server;
	
	public FriendMaker(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}
	
	public void addFriend(String[] input) {
		if (input.length != 2) {
			writer.println("Wrong command! You have to write add-friend friend_name!");
			return;
		}
		String friend = input[1];
		if (!server.isUserExisting(friend)) {
			writer.println("This user doesn't extist: " + friend);
			return;
		}
		server.getUser(username).addFriend(friend);
		server.getUser(friend).addFriend(username);
		writer.println("User " + username + " is now friend with " + server.getUserInfo(friend));
	}
}
