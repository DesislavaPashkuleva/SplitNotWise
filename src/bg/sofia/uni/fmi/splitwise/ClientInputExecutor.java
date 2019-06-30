package bg.sofia.uni.fmi.splitwise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientInputExecutor {

	private String username;
	private BufferedReader reader;
	private PrintWriter writer;
	private Server server;

	public ClientInputExecutor(String username, BufferedReader reader, PrintWriter writer, Server server) {
		this.username = username;
		this.reader = reader;
		this.writer = writer;
		this.server = server;
	}

	public void run() {
		while (true) {
			writer.println("Enter command: ");
			String command = getCommand();
			execute(command);
			if (command.startsWith("logout")) {
				return;
			}
		}
	}

	private String getCommand() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			System.out.println("Problem while executing the commands! Try again later!");
			server.registerError(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	private void execute(String commandInput) {
		String[] input = commandInput.split(" ");
		String command = input[0];

		if (command.equals("logout")) {
			logout();
		} else if (command.equals("add-friend")) {
			addFriend(input);
		} else if (command.equals("create-group")) {
			createGroup(input);
		} else if (command.equals("split")) {
			split(input);
		} else if (command.equals("split-group")) {
			splitGroup(input);
		} else if (command.equals("get-status")) {
			getStatus();
		} else if (command.equals("payed")) {
			payed(input);
		} else if (command.equals("help")) {
			help();
		} else {
			handleWrongInput();
		}
	}

	private void logout() {
		writer.println("Disconnect user " + server.getUserInfo(username));
		server.logoutUser(username);
	}

	private void addFriend(String[] input) {
		new FriendMaker(username, writer, server).addFriend(input);
	}

	private void createGroup(String[] input) {
		new GroupCreator(username, writer, server).createGroup(input);
	}

	private void split(String[] input) {
		new MoneySplitter(username, writer, server).split(input);
	}

	private void splitGroup(String[] input) {
		new GroupMoneySplitter(username, writer, server).split(input);
	}

	private void getStatus() {
		new Status(username, writer, server).get();
	}

	private void payed(String[] input) {
		new Payment(username, writer, server).pay(input);
	}

	private void help() {
		writer.println(
				"Possible commands:\nadd-friend \ncreate-group \nsplit \nsplit-group \nget-status \npayed \nhelp");
	}

	private void handleWrongInput() {
		writer.println("Wrong command! Try again!");
	}

}
