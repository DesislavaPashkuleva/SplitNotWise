package bg.sofia.uni.fmi.splitwise.commands;

import bg.sofia.uni.fmi.splitwise.server.Server;

import java.io.PrintWriter;
import java.util.Arrays;

public class GroupMoneySplitter {

	private String username;
	private PrintWriter writer;
	private Server server;
	
    private final int COMMAND_LENGTH = 3;

	public GroupMoneySplitter(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void split(String[] input) {
		if (isInputCorrect(input)) {
			Double sum = Double.parseDouble(input[1]);
			String group = input[2];
			int numberOfMembersInTheGroup = server.getUsersInTheGroup(group).size();
			String message = "Splited " + sum + " between you and the members in the group " + group;
			Double newSum = 0.0;
			
			for (String user : server.getUsersInTheGroup(group).keySet()) {
				if (user.equals(username)) {
					newSum = server.getUsersInTheGroup(group).get(user) - sum + (sum / numberOfMembersInTheGroup);
				} else {
					newSum = server.getUsersInTheGroup(group).get(user) + (sum / numberOfMembersInTheGroup);
				}
				server.updateGroupSum(group, user, newSum);
				server.registerPayment(username, message);
			}
			writer.println(message);
			makeNotification(input);
		}
	}

	private boolean isInputCorrect(String[] input) {
		if (input.length != COMMAND_LENGTH) {
			writer.println("Wrong command! You have writen less than 3 arguments!");
			return false;
		}
		String group = input[2];
		if (!server.isGroupExisting(group)) {
			writer.println("Group " + group + " doesn't exist!");
			return false;
		}
		return true;
	}

	private void makeNotification(String[] input) {
		String sum = input[1];
		String name = input[2];
		String[] purpose = Arrays.copyOfRange(input, 3, input.length);
		int membersInGroup = server.getUsersInTheGroup(name).size();
		String message = "You owe " + username + " " + Double.parseDouble(sum) / membersInGroup + " lv ["
				+ String.join(" ", purpose) + "].";
		for (String user : server.getUsersInTheGroup(name).keySet()) {
			server.makeNotificationForOfflineUsers(user, message, "groups");
		}
	}
}
