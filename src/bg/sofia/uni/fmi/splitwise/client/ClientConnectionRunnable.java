package bg.sofia.uni.fmi.splitwise.client;

import bg.sofia.uni.fmi.splitwise.connection.Connection;
import bg.sofia.uni.fmi.splitwise.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

public class ClientConnectionRunnable implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	private String username;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private Server server;

	public ClientConnectionRunnable(Socket socket, Server server) {
		try {
			this.writer = new PrintWriter(socket.getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.socket = socket;
			this.server = server;
		} catch (IOException e) {
			System.out.println("Problem while connecting the user!");
			server.registerError(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
			connect();
			getAndExecuteClientCommands();
			cleanUp();
	}

	private void connect() {
		Connection connection = new Connection(reader, writer, server);
		connection.connect();
		this.username = connection.getUsername();
	}
	
	private void getAndExecuteClientCommands() {
		new ClientInputExecutor(username, reader, writer, server).run();;
	}

	private void cleanUp() {
		try {
			socket.close();
			writer.close();
			reader.close();
		} catch (IOException e) {
			System.out.println("Problem with disconnecting the user!");
			e.printStackTrace();
		}
	}
}
