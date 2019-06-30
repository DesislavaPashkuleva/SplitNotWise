package bg.sofia.uni.fmi.splitwise.commands;

import bg.sofia.uni.fmi.splitwise.server.Server;

import java.io.PrintWriter;
import java.util.Arrays;

public class Payment {

	private String username;
	private PrintWriter writer;
	private Server server;

    private final double INITIAL_SUM = 0.0;
	
	public Payment(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void pay(String[] input) {
		if (isInputCorrect(input)) {
			String friend = input[2];
			Double sum = Double.parseDouble(input[1]);
			server.getUser(username).updateSum(-sum, friend);
			server.getUser(friend).updateSum(sum, username);
			writer.println(server.getUserInfo(friend) + " payed you " + sum + " lv");
			makeNotification(input);
			currentStatus(friend, server.getUser(username).getFriendsSum(friend));
		}
	}

	private boolean isInputCorrect(String[] input) {
		if (input.length != 3) {
			writer.println("Wrong command! You have writen less than 3 arguments!");
			return false;
		}
		String friend = input[2];
		if (!server.isUserExisting(friend)) {
			writer.println("User " + friend + " doesn't exist!");
			return false;
		}
		return true;
	}

	private void makeNotification(String[] input) {
		String sum = input[1];
		String name = input[2];
		String[] purpose = Arrays.copyOfRange(input, 3, input.length);
		String message = username + " approved your payment " + sum + " lv [" + String.join(" ", purpose) + "].";
		server.makeNotificationForOfflineUsers(name, message, "friends");
	}

	private void currentStatus(String user, double sum) {
		new Status(username, writer, server).currentStatus(user, sum);
	}

}
