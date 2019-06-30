package bg.sofia.uni.fmi.splitwise.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bg.sofia.uni.fmi.splitwise.client.UserInfo;
import bg.sofia.uni.fmi.splitwise.server.Server;

public class ServerTest {

	private final int PORT = 8088;

	private Server server;
	private ServerSocket serverSocket;

	@Before
	public void setUp() {
		try {
			server = new Server(serverSocket);
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRegisterUser() {
		UserInfo user = new UserInfo("username", "password", "name", "surname");
		server.registerUser("username", user);
		assertEquals(1, server.getUsers().size());
	}

	@Test
	public void testIsUserExisting() {
		UserInfo user = new UserInfo("name", "password", "name", "surname");
		server.registerUser("name", user);
		boolean result = server.isUserExisting("name");
		assertTrue(result);
	}
	
	@Test
	public void testGetUserInfo() {
		UserInfo user = new UserInfo("user", "password", "name", "surname");
		server.registerUser("user", user);
		String expected = "name surname (user)";
		String result = server.getUserInfo("user");
		assertEquals(expected, result);
	}
	
	@Test 
	public void testCreateGroup() {
		HashMap<String, Double> users = new HashMap<>();
		users.put("a", 0.0);
		users.put("b", 0.0);
		server.createGroup("group", users);
		assertTrue(server.isGroupExisting("group"));
	}

	@Test 
	public void testGetUsersInTheGroup() {
		HashMap<String, Double> users = new HashMap<>();
		users.put("a", 0.0);
		users.put("b", 0.0);
		server.createGroup("g", users);
		Map<String, Double> result = server.getUsersInTheGroup("g");
		assertEquals(users, result);
	}
	
	@Test 
	public void testIsGroupExisting() {
		HashMap<String, Double> users = new HashMap<>();
		users.put("a", 0.0);
		users.put("b", 0.0);
		server.createGroup("name", users);
		assertTrue(server.isGroupExisting("group"));
	}
	
	@After
	public void clenUp() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
