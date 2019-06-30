package bg.sofia.uni.fmi.splitwise.connection;

import bg.sofia.uni.fmi.splitwise.client.UserInfo;
import bg.sofia.uni.fmi.splitwise.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Connection {

	private String username;
	private String password;
	private BufferedReader reader;
	private PrintWriter writer;
	private Server server;

	public Connection(BufferedReader reader, PrintWriter writer, Server server) {
		this.reader = reader;
		this.writer = writer;
		this.server = server;
	}

	public void connect() {
		try {
			String command = reader.readLine();
			while (true) {
				getUsernameAndPassword();
				synchronized (server.getUsers()) {
					if (command.equals("login") && logIn()) {
						return;
					} else if (register()) {
						return;
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Connection problem! Try again later!");
			server.registerError(e.getMessage());
			e.printStackTrace();
		}
	}

	private void getUsernameAndPassword() {
		while (true) {
			writer.println("Enter username and password: ");
			try {
				String clientInput = reader.readLine();
				if (!areCredentialsCorrect(clientInput)) {
					continue;
				}
				String[] credentials = clientInput.split(" ");
				username = credentials[0];
				password = credentials[1];
				break;
			} catch (IOException e) {
				System.out.println("Problem while entering username and password! Try again later!");
				server.registerError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private boolean areCredentialsCorrect(String command) {
		if (command == null) {
			writer.println("Please fill your credentials! Enter again!");
			return false;
		}

		String[] names = command.split(" ");
		if (names.length != 2) {
			writer.println("Wrong entered credentials! Please enter again!");
			return false;
		}

		return true;
	}

	private boolean logIn() {
		if (!server.isUserExisting(username)) {
			writer.println("This username: " + username + " doesn't exist! Enter again!");
			return false;
		} else if (!server.getUser(username).isPasswordCorrect(password)) {
			writer.println("Wrong password! Enter username and password again!");
			return false;
		} else {
			server.getNotification(username);
			writer.println("Successfully login user: " + server.getUserInfo(username));
			return true;
		}
	}

	private boolean register() {
		if (server.isUserExisting(username)) {
			writer.println("This username: " + username + " already exist! Enter again!");
			return false;
		} else {
			String[] names = getNameAndSurname().split(" ");
			server.registerUser(username, new UserInfo(username, password, names[0], names[1]));
			writer.println("Successfully registered user: " + server.getUserInfo(username));
			return true;
		}
	}

	private String getNameAndSurname() {
		while (true) {
			writer.println("Add name and surname: ");
			try {
				String clientInput = reader.readLine();
				if (areCredentialsCorrect(clientInput)) {
					return clientInput;
				}
			} catch (IOException e) {
				System.out.println("Connection problem!");
				server.registerError(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public String getUsername() {
		return username;
	}
}
