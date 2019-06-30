package bg.sofia.uni.fmi.splitwise.commands;

import java.io.PrintWriter;
import java.util.Arrays;

import bg.sofia.uni.fmi.splitwise.server.Server;

public class GroupMoneySplitter {

	private String username;
	private PrintWriter writer;
	private Server server;

	private final int GROUP_INDEX = 2;
	private final int SUM_INDEX = 1;
	private final int NAME_INDEX = 2;
	private final int COMMAND_LENGHT = 3;
	private final Double INITIAL_SUM = 0.0;

	public GroupMoneySplitter(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void split(String[] input) {
		if (isInputCorrect(input)) {
			Double sum = Double.parseDouble(input[SUM_INDEX]);
			String group = input[GROUP_INDEX];
			int numberOfMembersInTheGroup = server.getUsersInTheGroup(group).size();
			String message = "Splited " + sum + " between you and the members in the group " + group;
			Double newSum = INITIAL_SUM;

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
		if (input.length != COMMAND_LENGHT) {
			writer.println("Wrong command! You have writen less than 3 arguments!");
			return false;
		}
		String group = input[GROUP_INDEX];
		if (!server.isGroupExisting(group)) {
			writer.println("Group " + group + " doesn't exist!");
			return false;
		}
		return true;
	}

	private void makeNotification(String[] input) {
		String sum = input[SUM_INDEX];
		String name = input[NAME_INDEX];
		String[] purpose = Arrays.copyOfRange(input, COMMAND_LENGHT, input.length);
		int membersInGroup = server.getUsersInTheGroup(name).size();
		String message = "You owe " + username + " " + Double.parseDouble(sum) / membersInGroup + " lv ["
				+ String.join(" ", purpose) + "].";
		for (String user : server.getUsersInTheGroup(name).keySet()) {
			server.makeNotificationForOfflineUsers(user, message, "groups");
		}
	}
}
