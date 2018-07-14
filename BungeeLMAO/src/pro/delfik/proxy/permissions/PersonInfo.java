package pro.delfik.proxy.permissions;

public class PersonInfo {
	private boolean authorized = false;
	public String name;
	public String password = "";
	public int money = 0;
	public Rank rank = Rank.PLAYER;
	public long online = 0;
	public String ip;
	
	public PersonInfo(String name) {this.name = name;}
	
	public PersonInfo(String name, String password, int money, Rank rank, long online, String ip) {
		this.name = name;
		this.password = password;
		this.money = money;
		this.rank = rank;
		this.online = online;
		this.ip = ip;
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
	
	public String getIP() {
		return ip;
	}
	
	public String getPassword() {
		return password;
	}
}
