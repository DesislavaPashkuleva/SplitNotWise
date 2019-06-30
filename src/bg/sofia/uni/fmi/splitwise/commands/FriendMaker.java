package bg.sofia.uni.fmi.splitwise.commands;

import java.io.PrintWriter;

import bg.sofia.uni.fmi.splitwise.server.Server;

public class FriendMaker {

	private String username;
	private PrintWriter writer;
	private Server server;

	private final int COMMAND_LENGTH = 2;
	private final int FRIEND_INDEX = 1;

	public FriendMaker(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void addFriend(String[] input) {
		if (input.length != COMMAND_LENGTH) {
			writer.println("Wrong command! You have to write add-friend friends_name!");
			return;
		}
		String friend = input[FRIEND_INDEX];
		if (!server.isUserExisting(friend)) {
			writer.println("This user doesn't extist: " + friend);
			return;
		}
		server.getUser(username).addFriend(friend);
		server.getUser(friend).addFriend(username);
		writer.println("User " + username + " is now friend with " + server.getUserInfo(friend));
	}
}
