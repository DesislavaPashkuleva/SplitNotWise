package bg.sofia.uni.fmi.splitwise.commands;

import java.io.PrintWriter;
import java.util.Arrays;

import bg.sofia.uni.fmi.splitwise.server.Server;

public class MoneySplitter {

	private String username;
	private PrintWriter writer;
	private Server server;

	private final int FRIEND_INDEX = 2;
	private final int SUM_INDEX = 1;
	private final int NAME_INDEX = 2;
	private final int NUMBER_OF_FRIENDS = 2;
	private final int COMMAND_LENGHT = 3;

	public MoneySplitter(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void split(String[] input) {
		if (isCorrectInput(input)) {
			String friend = input[FRIEND_INDEX];
			Double sum = Double.parseDouble(input[SUM_INDEX]) / NUMBER_OF_FRIENDS;
			server.getUser(username).updateSum(sum, friend);
			server.getUser(friend).updateSum(-sum, username);
			String message = "Splited " + sum * NUMBER_OF_FRIENDS + " lv between you and " + server.getUserInfo(friend);
			writer.println(message);
			server.registerPayment(username, message);
			makeNotification(input);
			currentStatus(friend, server.getUser(username).getFriendsSum(friend));
		}
	}

	private boolean isCorrectInput(String[] input) {
		if (input.length != COMMAND_LENGHT) {
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
		String name = input[NAME_INDEX];
		String[] purpose = Arrays.copyOfRange(input, COMMAND_LENGHT, input.length);
		String message = username + " payed " + sum + " lv [" + String.join(" ", purpose) + "].";
		server.makeNotificationForOfflineUsers(name, message, "friends");
	}

	private void currentStatus(String user, double sum) {
		new Status(user, writer, server).currentStatus(user, sum);
	}

}
