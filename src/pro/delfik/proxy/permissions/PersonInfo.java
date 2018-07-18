package pro.delfik.proxy.permissions;

import pro.delfik.util.Rank;

import java.util.ArrayList;
import java.util.List;

public class PersonInfo {
	private boolean authorized = false;
	public String name;
	public String password = "";
	public int money = 0;
	public Rank rank = Rank.PLAYER;
	public long online = 0;
	public String ip;
	public boolean ipbound;
	public List<String> ignoredPlayers;
	public boolean pmDisabled;
	public List<String> friends;
	
	public PersonInfo(String name) {this.name = name;}
	
	public PersonInfo(String name, String password, int money, Rank rank, long online, String ip, boolean ipbound,
					  List<String> ignoredPlayers, boolean pmDisabled, List<String> friends) {
		this.name = name;
		this.password = password;
		this.money = money;
		this.rank = rank;
		this.online = online;
		this.ip = ip;
		this.ipbound = ipbound;
		this.ignoredPlayers = ignoredPlayers;
		this.pmDisabled = pmDisabled;
		this.friends = friends;
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public String getName() {
		return name;
	}
	
	public int getMoney() {
		return money;
	}
	
	public long getOnline() {
		return online;
	}
	
	public String getIp() {
		return ip;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean isIPBound() {
		return ipbound;
	}
	public boolean isPmDisabled() {
		return pmDisabled;
	}
	
	public List<String> getFriends() {
		return friends == null ? new ArrayList<>() : friends;
	}
	
	public List<String> getIgnoredPlayers() {
		return ignoredPlayers == null ? new ArrayList<>() : ignoredPlayers;
	}
}
