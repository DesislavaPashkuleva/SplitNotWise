package bg.sofia.uni.fmi.splitwise.commands;

import java.io.PrintWriter;
import java.util.Arrays;

import bg.sofia.uni.fmi.splitwise.server.Server;

public class Payment {

	private String username;
	private PrintWriter writer;
	private Server server;

	private final int SUM_INDEX = 1;
	private final int FRIEND_INDEX = 2;
	private final int COMMAND_LENGTH = 3;

	public Payment(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void pay(String[] input) {
		if (isInputCorrect(input)) {
			String friend = input[FRIEND_INDEX];
			Double sum = Double.parseDouble(input[SUM_INDEX]);
			server.getUser(username).updateSum(-sum, friend);
			server.getUser(friend).updateSum(sum, username);
			writer.println(server.getUserInfo(friend) + " payed you " + sum + " lv");
			makeNotification(input);
			currentStatus(friend, server.getUser(username).getFriendsSum(friend));
		}
	}

	private boolean isInputCorrect(String[] input) {
		if (input.length != COMMAND_LENGTH) {
			writer.println("Wrong command! You have writen less than 3 arguments!");
			return false;
		}
		String friend = input[FRIEND_INDEX];
		if (!server.isUserExisting(friend)) {
			writer.println("User " + friend + " doesn't exist!");
			return false;
		}
		return true;
	}

	private void makeNotification(String[] input) {
		String sum = input[SUM_INDEX];
		String name = input[FRIEND_INDEX];
		String[] purpose = Arrays.copyOfRange(input, COMMAND_LENGTH, input.length);
		String message = username + " approved your payment " + sum + " lv [" + String.join(" ", purpose) + "].";
		server.makeNotificationForOfflineUsers(name, message, "friends");
	}

	private void currentStatus(String user, double sum) {
		new Status(user, writer, server).currentStatus(user, sum);
	}

}
