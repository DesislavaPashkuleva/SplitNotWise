package bg.sofia.uni.fmi.splitwise.commands;

import java.io.PrintWriter;
import java.util.HashMap;

import bg.sofia.uni.fmi.splitwise.server.Server;

public class GroupCreator {

	private String username;
	private PrintWriter writer;
	private Server server;
	
	private final int GROUP_INDEX = 1;
	private final int USER_INDEX = 2;
	private final int COMMAND_LENGHT = 4;
	private final Double INITIAL_SUM = 0.0;

	public GroupCreator(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}

	public void createGroup(String[] input) {
		if (isInputCorrect(input)) {
			String group = input[GROUP_INDEX];
			server.getUser(username).addGroup(group);
			HashMap<String, Double> groupMembers = new HashMap<>();
			groupMembers.put(username, INITIAL_SUM);
			for (int i = USER_INDEX; i < input.length; i++) {
				groupMembers.put(input[i], INITIAL_SUM);
				server.getUser(input[i]).addGroup(group);
			}
			server.createGroup(group, groupMembers);
			writer.println("Created group " + group);
		}
	}

	private boolean isInputCorrect(String[] input) {
		if (input.length < COMMAND_LENGHT) {
			writer.println("Wrong command! You have written less than 4 words!");
			return false;
		}
		return true;
	}
}
