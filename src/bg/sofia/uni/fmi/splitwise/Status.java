package bg.sofia.uni.fmi.splitwise;

import java.io.PrintWriter;

public class Status {

	private String username;
	private PrintWriter writer;
	private Server server;

	public Status(String username, PrintWriter writer, Server server) {
		this.username = username;
		this.writer = writer;
		this.server = server;
	}
	
	public void get() {
		writer.println("Friends:");
		for (String user : server.getUser(username).getFriends()) {
			double sum = server.getUser(username).getFriendsSum(user);
			currentStatus(user, sum);
		}

		writer.println("\nGroups:");
		for (String group : server.getUser(username).getGroups()) {
			writer.println(group);
			for (String user : server.getUsersInTheGroup(group).keySet()) {
				if (!user.equals(username)) {
					int members = server.getUsersInTheGroup(group).size();
					double sum = server.getUsersInTheGroup(group).get(user);
					currentStatus(user, sum / members);
				}
			}
		}
	}
	
	private void currentStatus(String user, double sum) {
		if (sum > 0.0) {
			writer.println(server.getUserInfo(user) + ": Owes you " + sum + " lv");
		} else if (sum < 0.0) {
			sum = -sum;
			writer.println(server.getUserInfo(user) + ": You owe " + sum + " lv");
		} else {
			writer.println(server.getUserInfo(user) + ": Owes you nothing");
		}
	}
}
