package bg.sofia.uni.fmi.splitwise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRunnable implements Runnable {

	private Socket socket;

	public ClientRunnable(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				if (socket.isClosed()) {
					return;
				}
				System.out.println(reader.readLine());
			}
		} catch (IOException e) {
			System.out.println("Problem with the client socket!");
		}
	}
}
