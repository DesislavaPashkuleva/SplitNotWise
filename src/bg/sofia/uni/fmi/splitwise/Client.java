package bg.sofia.uni.fmi.splitwise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

	private BufferedReader reader;
	private PrintWriter writer;
	private Socket socket;

	private final int PORT = 8080;
	private final String LOCALHOST = "localhost";

	public Client() {
		try {
			socket = new Socket(LOCALHOST, PORT);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println(
					"There was a problem while connecting the server! Try again later or contact the administrator.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Client().run();
	}

	public void run() {
		System.out.println("Welcome! Register or login?");

		try (Scanner scanner = new Scanner(System.in)) {
			String input = getRightInput(scanner);
			connectToServer(input);
			acceptUserCommands(scanner);
		} finally {
			cleanUp();
		}
	}

	private String getRightInput(Scanner scanner) {
		String input = scanner.nextLine();
		while (!isRightInput(input)) {
			System.out.println("Wrong command! Register or login?");
			input = scanner.nextLine();
		}
		return input;
	}

	private boolean isRightInput(String input) {
		return input.startsWith("register") || input.startsWith("login");
	}

	private void connectToServer(String input) {
		new Thread(new ClientRunnable(socket)).start();
		writer.println(input);
		System.out.println("Successfully connected to the server!");
	}

	private void acceptUserCommands(Scanner scanner) {
		while (true) {
			String input = scanner.nextLine();
			if (input.startsWith("logout")) {
				writer.println(input);
				System.out.println("Loggin out");
				return;
			}
			writer.println(input);
		}
	}

	private void cleanUp() {
		try {
			socket.close();
			writer.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("Problem with connecting the server!");
			e.printStackTrace();
		}
	}
}
