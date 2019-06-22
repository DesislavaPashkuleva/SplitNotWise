package bg.sofia.uni.fmi.splitwise;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Server {
	private static final int PORT = 8080;
	private ServerSocket serverSocket;

	private File dataBase;
	private HashMap<String, UserInfo> users;
	private HashMap<String, HashMap<String, Double>> groups;
	private HashMap<String, HashMap<String, ArrayList<String>>> offlineUsers;
	private HashMap<String, File> paymentHistory;
	private File errors;

	Server(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		dataBase = new File("resources/DataBase.ser");
		users = new HashMap<>();
		groups = new HashMap<>();
		offlineUsers = new HashMap<>();
		paymentHistory = new HashMap<>();
		errors = new File("resources/Errors.txt");
	}

	public static void main(String[] args) {
		try {
			new Server(new ServerSocket(PORT)).run();
		} catch (IOException e) {
			System.out.println("A problem occured in the server!");
		}
	}

	private void run() {
		System.out.println("The server is running");
//		restoreUserInformation();
		
		while (true) {
			try {
				new Thread(new ClientConnectionRunnable(serverSocket.accept(), this)).start();
			} catch (IOException e) {
				System.out.println("A problem occured in the server!");
				e.printStackTrace();
				registerError(e.getMessage());
			}
		}
	}

	public HashMap<String, UserInfo> getUsers() {
		synchronized (users) {
			return users;
		}
	}

	public UserInfo getUser(String username) {
		synchronized (users) {
			return users.get(username);
		}
	}

	public Map<String, Double> getUsersInTheGroup(String group) {
		synchronized (groups) {
			return groups.get(group);
		}
	}

	public synchronized HashMap<String, ArrayList<String>> getNotification(String user) {
		HashMap<String, ArrayList<String>> notification = offlineUsers.get(user);
		offlineUsers.remove(user);
		return notification;
	}

	public boolean isUserExisting(String username) {
		synchronized (users) {
			return users.containsKey(username);
		}
	}

	public boolean isGroupExisting(String group) {
		synchronized (groups) {
			return groups.containsKey(group);
		}
	}

	public void registerUser(String username, UserInfo user) {
		synchronized (users) {
			users.put(username, user);
			paymentHistory.put(username, new File("resources/" + username + ".txt"));
//			try {
//				ObjectOutputStream userWriter = new ObjectOutputStream(new FileOutputStream(dataBase));
//				userWriter.writeObject(user);
//				userWriter.close();
//			} catch (IOException e) {
//				System.out.println("There is a problem with registering the user in the server!");
//				registerError(e.getMessage());
//			}
		}
	}

	public void updateGroupSum(String group, String username, Double sum) {
		synchronized (groups) {
			groups.get(group).put(username, sum);
		}
	}

	public void createGroup(String group, HashMap<String, Double> users) {
		synchronized (groups) {
			groups.put(group, users);
		}
	}

	public void logoutUser(String user) {
		HashMap<String, ArrayList<String>> notification = new HashMap<>();
		notification.put("friends", new ArrayList<>());
		notification.put("groups", new ArrayList<>());
		synchronized (offlineUsers) {
			offlineUsers.put(user, notification);
		}
	}

	public void makeNotificationForOfflineUsers(String user, String message, String from) {
		synchronized (offlineUsers) {
			if (offlineUsers.containsKey(user)) {
				offlineUsers.get(user).get(from).add(message);
			}
		}
	}

	public synchronized void registerPayment(String user, String payment) {
		try {
			PrintWriter paymentWriter = new PrintWriter(
					new FileWriter(paymentHistory.get(user).getAbsolutePath(), true));
			paymentWriter.println(payment);
			paymentWriter.close();
		} catch (IOException e) {
			System.out.println("There is a problem while entering payment in user's paymentHistory file!");
			registerError(e.getMessage());
		}
	}

	public synchronized void registerError(String stackTrace) {
		try {
			PrintWriter errorsWriter = new PrintWriter(new FileWriter(errors.getAbsolutePath(), true));
			errorsWriter.println(stackTrace);
			errorsWriter.close();
		} catch (IOException e) {
			System.out.println("There was a problem while registerig the user!");
			registerError(e.getMessage());
		}
	}

	private void restoreUserInformation() {
		try {
			ObjectInputStream userReader = new ObjectInputStream(new FileInputStream(dataBase));
			try {
				UserInfo user = (UserInfo) userReader.readObject();
				while (user != null) {
					users.put(user.getUsername(), user);
					user = (UserInfo) userReader.readObject();
				}
				userReader.close();
			} catch (ClassNotFoundException e) {
				System.out.println("There is a problem with the dataBase!");
				registerError(e.getMessage());
			}
		} catch (FileNotFoundException e) {
			System.out.println("There is a problem with the dataBase! DataBase is not found!");
			registerError(e.getMessage());
		} catch (IOException e) {
			System.out.println("There is a problem with restorig the user information!");
			registerError(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String getUserInfo(String username) {
		UserInfo user = getUser(username);
		String info = user.getName() + " " + user.getSurname() + " (" + username + ")";
		return info;
	}

}
