package pro.delfik.proxy.permissions;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.net.packet.PacketPex;
import pro.delfik.net.packet.PacketUser;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.connection.Server;
import pro.delfik.proxy.data.DataEvent;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.data.PlayerDataManager;
import pro.delfik.util.Converter;
import pro.delfik.util.CryptoUtils;
import pro.delfik.util.Rank;
import pro.delfik.util.U;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Person {
	
	private static final HashMap<Integer, Person> list = new HashMap<>();
	
	public static Person get(String name) {
		return list.get(Converter.smartLowercase(name).hashCode());
	}

	public static Person get(CommandSender sender) {
		return list.get(Converter.smartLowercase(sender.getName()).hashCode());
	}
	
	public static Person load(String name) {
		PersonInfo info = PlayerDataManager.load(Converter.smartLowercase(name));
		if (info == null) return new Person(new PersonInfo(name, "", 0, Rank.PLAYER,
			0L, "", false, new ArrayList<>(), false, new ArrayList<>()),null, false);
		
		ProxiedPlayer p = Proxy.getPlayer(name);
		
		boolean auth = false;
		Mutes.MuteInfo mute = Mutes.get(name);
		String lastSeenIP = info.getIp();
		boolean ipbound = info.ipbound;
		
		if (lastSeenIP != null && info.ipbound) {
			if (lastSeenIP.equals(p.getAddress().getHostName())) {
				auth = true;
				U.msg(p, "§aАвтоматическая авторизация прошла успешно.");
			} else {
				throw new DifferentIPException(name);
			}
		}
		
		return new Person(info, mute, auth);
	}
	public static void unload(String name) {
		Person p = get(name);
		if (p == null) return;
		list.remove(p.definition().hashCode());
		if (!p.authorized) return;
		PlayerDataManager.save(p.getInfo());
		try {
			PlayerDataManager.save(p.getInfo());
		} catch (RuntimeException e) {
			Proxy.log(Level.SEVERE, "Player " + name + " §cwasn't saved properly.");
			e.printStackTrace();
		}
	}
	
	
	public static Collection<Person> getAll() {
		return list.values();
	}
	
	// non-static
	
	public final String name; // Ник игрока
	public final String definition; // Адрес, по которому хранится игрок в базе данных
	private long connectedAt; // Время, в которое игрок зашёл на сервер (Нужно для подсчёта онлайна)
	private boolean authorized = false; // Авторизован ли игрок
	private String password ; // Hash пароля
	private final long online; // Онлайн до захода на сервер
	private int money; // Баланс игрока
	private Rank rank; // Ранг игрока
	private String server = ""; // Сервер, на котором находится игрок
	private boolean ipbound; // Разрешён ливход только с сохранённого IP
	private boolean pmDisabled; // Включён ли ЛС
	private List<String> friends;
	private List<String> ignoredPlayers;
	
	private Mutes.MuteInfo mute;
	
	public String lastWriter = null;
	
	public Person(PersonInfo personInfo, Mutes.MuteInfo mute, boolean auth) {
		this.name = personInfo.name;
		this.definition = Converter.smartLowercase(name);
		this.rank = personInfo.rank;
		this.password = personInfo.password;
		this.online = personInfo.online;
		this.money = personInfo.money;
		this.mute = mute;
		this.ipbound = personInfo.ipbound;

		if (auth) this.authorize();
		this.connectedAt = System.currentTimeMillis();
		list.put(definition.hashCode(), this);
	}
	
	// Implementation
	
	public void msg(Object... o) {
		U.msg(getHandle(), o);
	}

	public void kick(String reason) {
		getHandle().disconnect(new TextComponent(reason));
	}

	public String getIP() {return getHandle().getAddress().getHostName();}

	public ServerInfo getServerInfo() {return getHandle().getServer().getInfo();}
	
	
	// Getters & Setters
	
	protected final String definition() {return definition;}

	public ProxiedPlayer getHandle() {
		return Proxy.getPlayer(name);
	}
	
	public boolean isAuthorized() {
		return authorized;
	}

	public void authorize() {
		authorized = true;
		DataEvent.event(getHandle().getServer(), "auth", name);
	}
	
	public void setRank(Rank rank) {
		this.rank = rank;
		server().send(new PacketPex(name, rank));
	}

	public Rank getRank() {
		return rank;
	}

	public boolean hasRank(Rank rank) {
		return isAuthorized() && this.rank.ordinal() <= rank.ordinal();
	}

	public Server server(){
		return Server.get(getServer());
	}

	public String getServer() {
		return this.server.equals("") ? "LOBBY_1" : this.server;
	}

	public void setServer(String server) {
		this.server = server;
		server().send(new PacketUser(name, rank, authorized));
	}
	
	public void earn(int money) {
		this.money += money;
		updateMoney();
	}
	public void disburse(int money) {
		this.money -= money;
		updateMoney();
	}
	
	private void updateMoney() {
		Database.sendUpdate("UPDATE Users SET money = " + money + " WHERE name = " + definition());
	}
	
	public long getMoney() {
		return money;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password.length() == 0 ? "" : CryptoUtils.getHash(password);
	}
	
	public long getOnline() {
		return System.currentTimeMillis() - connectedAt + online;
	}
	
	public PersonInfo getInfo() {
		return new PersonInfo(name, password, money, rank, getOnline(), getIP(), ipbound, ignoredPlayers, pmDisabled, friends);
	}
	
	public Mutes.MuteInfo getActiveMute() {
		return mute;
	}
	
	public void clearMute() {
		mute = null;
	}
	
	public void mute(Mutes.MuteInfo muteInfo) {
		mute = muteInfo;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isIPBound() {
		return ipbound;
	}
}
