package bg.sofia.uni.fmi.splitwise;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserInfo {

	private String username;
	private String password;
	private String name;
	private String surname;
	private ConcurrentHashMap<String, Double> friends;
	private Set<String> groups;

	private final Double INITIAL_SUM = 0.0;

	public UserInfo(String username, String password, String name, String surname) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.surname = surname;
		friends = new ConcurrentHashMap<String, Double>();
		groups = new HashSet<>();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public Set<String> getFriends() {
		return friends.keySet();
	}

	public Set<String> getGroups() {
		return groups;
	}

	public boolean isPasswordCorrect(String password) {
		return password.equals(this.password);
	}

	public void addFriend(String username) {
		friends.put(username, INITIAL_SUM);
	}

	public void addGroup(String group) {
		groups.add(group);
	}

	public double getFriendsSum(String friend) {
		return friends.get(friend);
	}

	public void updateSum(Double sum, String friend) {
		Double oldSum = friends.get(friend);
		friends.put(friend, sum + oldSum);
	}

}
