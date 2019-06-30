package bg.sofia.uni.fmi.splitwise.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import bg.sofia.uni.fmi.splitwise.client.UserInfo;
import bg.sofia.uni.fmi.splitwise.server.Server;

public class Connection {

	private String username;
	private String password;
	private BufferedReader reader;
	private PrintWriter writer;
	private Server server;

	private final int CREDENTIALS_LENGTH = 2;
	private final int NAME_INDEX = 0;
	private final int PASSWORD_INDEX = 1;
	private final int SURNAME_INDEX = 1;

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
					} else if (command.equals("register") && register()) {
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
				username = credentials[NAME_INDEX];
				password = credentials[PASSWORD_INDEX];
				break;
			} catch (IOException e) {
				System.out.println("Problem while entering username and password! Try again later!");
				server.registerError(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean logIn() {
		if (!server.isUserExisting(username)) {
			writer.println("This username: " + username + " doesn't exist! Enter again!");
			return false;
		} else if (!server.getUser(username).isPasswordCorrect(password)) {
			writer.println("Wrong password! Enter username and password again!");
			return false;
		} else {
			writer.println("Successfully login user: " + getUserInfo(username));
			return true;
		}
	}

	private boolean register() {
		if (server.isUserExisting(username)) {
			writer.println("This username: " + username + " already exist! Enter again!");
			return false;
		} else {
			String[] names = getNameAndSurname().split(" ");
			server.registerUser(username, new UserInfo(username, password, names[NAME_INDEX], names[SURNAME_INDEX]));
			writer.println("Successfully registered user: " + getUserInfo(username));
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
				System.out.println("Connection problem! Try again later!");
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
		if (names.length != CREDENTIALS_LENGTH) {
			writer.println("Wrong entered credentials! Please enter again!");
			return false;
		}

		return true;
	}

	public String getUserInfo(String username) {
		UserInfo user = server.getUser(username);
		String info = user.getName() + " " + user.getSurname() + " (" + username + ")";
		return info;
	}

	public String getUsername() {
		return username;
	}
}
