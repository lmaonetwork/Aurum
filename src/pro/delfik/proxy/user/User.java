package pro.delfik.proxy.user;

import implario.net.packet.PacketAuth;
import implario.net.packet.PacketPex;
import implario.net.packet.PacketUser;
import implario.util.*;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Server;
import pro.delfik.util.TimedHashMap;
import pro.delfik.util.TimedList;
import pro.delfik.util.U;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class User implements ManualByteable {

	private static final HashMap<String, User> list = new HashMap<>();
	public static final TimedHashMap<String, String> outAuth = new TimedHashMap<>(60);
	public static final TimedList<String> allowedIP = new TimedList<>(60);

	/**
	 * Путь к личной папке игрока.
	 * @param nick Ник игрока
	 * @return Путь к папке в ./Core/
	 */
	public static String getPath(String nick) {
		return "players/" + Converter.smartLowercase(nick) + "/";
	}

	static {
		list.put("CONSOLE", new User("CONSOLE", Rank.DEV, true));
	}


	/**
	 * Получение юзера по нику.
	 * @param name Ник игрока.
	 * @return Если игрок существует, User, иначе null.
	 */
	public static User get(String name) {
		return list.get(Converter.smartLowercase(name));
	}

	/**
	 * Получение юзера по отправителю команд BungeeCord.
	 * @param sender CommandSender банжика.
	 * @return User, обёртывающий этого сендера.
	 */
	public static User get(CommandSender sender) {
		return list.get(Converter.smartLowercase(sender.getName()));
	}

	/**
	 * Прогрузка пользователя только для модификации, когда игрок находится в оффлайне.
	 */
	public static User loadOffline(String name) {
		allowedIP.add(name.toLowerCase());
		User u = DataIO.readByteable(getPath(name) + "player", User.class);
		allowedIP.remove(name.toLowerCase());
		return u;
	}

	/**
	 * Прогружает юзера по данным с диска.
	 * Если на диске ничего нет, регистрирует нового.
	 * @param name Имя игрока.
	 * @return Юзер, обёртывающий игрока с заданным ником.
	 */
	public static User load(String name) {
		try {
			User u = DataIO.readByteable(getPath(name) + "player", User.class);
			if (u == null) u = new User(name, Rank.PLAYER, false);
			return u;
		} catch (IllegalArgumentException ex) {
			throw new DifferentIPException(name);
		}
	}

	public static UserInfo a(String name) {
		ByteUnzip unzip = new ByteUnzip(DataIO.readBytes(getPath(name) + "player"));
		return UserInfo.Version.unzip(unzip);
	}

	public static void save(UserInfo info) {
		ManualByteZip zip = info.zip();
		DataIO.writeBytes(getPath(info.name) + "player", zip.build());
	}

	/**
	 * Выгружает юзера из памяти, сохраняя все данные на диск (Если юзер авторизован).
	 * @param name Имя юзера.
	 */
	public static void unload(String name) {
		User user = get(name);
		if (user == null) return;
		list.remove(Converter.smartLowercase(name));
		if (!user.authorized) return;
		if (user.mute != null) user.mute.write(name);
		DataIO.writeByteable(getPath(name) + "player", user);
	}

	/**
	 * @return Все прогруженные юзеры (Включая виртуальных и консоль)
	 */
	public static Collection<User> getAll() {
		return list.values();
	}


	public final String name; 								// Ник игрока
	private final int connectedAt; 							// Время, в которое игрок зашёл на сервер (Нужно для подсчёта онлайна)
	private final int online; 								// Онлайн до захода на сервер
	private Rank rank;								 		// Ранг игрока
	private final List<String> friends;						// Список друзей
	private final List<String> ignoredPlayers;				// Чёрный список
	private boolean authorized 			= false; 			// Авторизован ли игрок
	private String password 			= ""; 				// Hash пароля
	private int money 					= 0; 				// Баланс игрока
	private String server 				= ""; 				// Сервер, на котором находится игрок
	private boolean ipbound				= false;			// Разрешён ли вход только с сохранённого IP
	private boolean pmDisabled			= false; 			// Откючён ли ЛС
	private String lastIP				= "";				// Последний IP, с которого был выполнен вход.
	public String lastPenPal			= null;				// Последний человек, с которым игрок общался в лс.
	private Mute mute 					= null;				// Данные о муте игрока
	private String last = "", lastLast  = null;				// Последние сообщения игрока
	private volatile String forcedIP	= null;				// IP, который будет принудительно сохранён при выходе с сервера.
	private boolean darkTheme			= false;			// ТЁМНАЯ ТЕМА!!!111 АААААааааААААА

	public User(String nick, Rank rank, boolean auth) {
		this.name = nick;
		this.rank = rank;
		this.online = 0;
		this.connectedAt = (int) (System.currentTimeMillis() / 60000);
		this.friends = new ArrayList<>();
		this.ignoredPlayers = new ArrayList<>();
		this.authorized = auth;
		list.put(Converter.smartLowercase(name), this);
	}

	public User(UserInfo info) {
		name = info.name;
		connectedAt = (int) (System.currentTimeMillis() / 60000);
		online = info.online;
		rank = info.rank;
		friends = info.friends;
		ignoredPlayers = info.ignored;
		password = info.passhash;
		money = info.money;
		ipbound = info.ipAttached;
		pmDisabled = info.pmDisabled;
		lastIP = info.lastIP;
		mute = Mute.get(name);
	}

	public User(ManualByteUnzip unzip) {
		name = unzip.getString();
		password = unzip.getString();
		rank = Rank.byChar.get((char) unzip.getByte());
		online = unzip.getInt();
		String lastSeenIP = unzip.getString();
		money = unzip.getInt();
		ipbound = unzip.getBoolean();
		pmDisabled = unzip.getBoolean();
		ignoredPlayers = unzip.getList();
		friends = unzip.getList();

		connectedAt = (int) (System.currentTimeMillis() / 60000);
		list.put(Converter.smartLowercase(name), this);
		mute = Mute.get(name);

		if (!allowedIP.contains(name.toLowerCase()))
		if (lastSeenIP != null && lastSeenIP.length() != 0 && ipbound) {
			ProxiedPlayer p = Proxy.getPlayer(name);
			String playerIP = p.getAddress().getAddress().getHostAddress();
			String outAuthIP = outAuth.get(name);
			if (outAuthIP != null && outAuthIP.equals(p.getAddress().getHostName())) {
				outAuth.remove(name);
				lastSeenIP = playerIP;
			}
			if (lastSeenIP.equals(playerIP)) {
				authorize();
				U.msg(p, "§aАвтоматическая авторизация прошла успешно.");
			} else throw new DifferentIPException(name);
		}
	}


	@Override
	public ManualByteZip zip() {
		return new ManualByteZip()
					   .add(name)
					   .add(password)
					   .add(rank.getByte())
					   .add(getOnline())
					   .add(forcedIP == null ? getIP() : forcedIP)
					   .add(getMoney())
					   .add(ipbound)
					   .add(pmDisabled)
					   .add(ignoredPlayers)
					   .add(friends);
	}


	// Implementation
	public void msg(Object... o) {U.msg(getSender(), o);}

	public void kick(String reason) {getHandle().disconnect(new TextComponent(reason));}

	public void updateTab(ProxiedPlayer handle) {
		Proxy.i().getScheduler().schedule(Aurum.instance, () -> {
			PlayerListItem item = getTab(handle);
			for (ProxiedPlayer player : handle.getServer().getInfo().getPlayers()) {
				User user = get(player);
				player.unsafe().sendPacket(item);
				if (user != null)
					handle.unsafe().sendPacket(user.getTab(player));
			}
		}, 1, TimeUnit.SECONDS);
	}

	public void sendPM(User dest, String msg) {
		if(isIgnoring(dest.getName()))
			throw new ExCustom("§cВы не можете писать игроку из чёрного списка.");
		if(dest.isIgnoring(getName()))
			throw new ExCustom("§cВы находитесь в чёрном списке у игрока §e" + dest.getName() + "§c.");
		if(pmDisabled){
			msg("§cУ вас выключены приватные сообщения. ", U.run("(§a§nВключить§f)", "§f>> §a§lВключить §f<<", "/ignore @a"));
			throw new ExCustom(null);
		}
		if(dest.pmDisabled) throw new ExCustom("§cИгрок отключил приватные сообщения.");
		lastPenPal = dest.getName();
		msg(U.simple("§e[§fВы §e-> §f" + dest.getName() + "§e] " + msg, "§f>> §e§lОтветить §f<<", "/msg " + dest.getName()));
		dest.recievePM(this, msg);
	}

	private void recievePM(User user, String msg) {
		lastPenPal = user.getName();
		msg(U.simple("§e[§f" + user.getName() + "§e -> §fВы§e] " + msg, "§f>> §e§lОтветить §f<<", "/msg " + user.getName()));
	}

	// Getters & Setters


	public CommandSender getSender() {
		return name.equals("CONSOLE") ? Proxy.getConsole() : getHandle();
	}

	public String getIP() {
		ProxiedPlayer p = getHandle();
		return p == null ? "" : p.getAddress().getAddress().getHostAddress();
	}

	public String getLastIP() {
		return lastIP;
	}

	public ServerInfo getServerInfo() {
		return getHandle().getServer().getInfo();
	}

	public ProxiedPlayer getHandle() {
		return Proxy.getPlayer(name);
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void authorize() {
		authorized = true;
		Server server = server();
		if (server != null) {
			PacketAuth packet = new PacketAuth(name);
			System.out.println(Coder.toString(packet.zip()));
			server.send(packet);
		}
		else throw new IllegalStateException("Игрок " + name + " не смог авторизоваться на null-сервере " + server);
	}

	public void setRank(Rank rank) {
		this.rank = rank;
		server().send(new PacketPex(name, rank));
		updateTab(getHandle());
	}

	public Rank getRank() {
		return rank;
	}
	public void setLast(String last) {
		lastLast = this.last;
		this.last = last;
	}

	public String getLast() {
		return last;
	}

	public String getLastLast() {
		return lastLast;
	}

	public boolean hasRank(Rank rank) {
		return isAuthorized() && this.rank.ordinal() <= rank.ordinal();
	}

	public Server server() {
		return Server.get(getServer());
	}

	public String getServer() {
		return this.server.equals("") ? "LOBBY_1" : this.server;
	}

	public void setServer(String server) {
		this.server = server;
		server().send(new PacketUser(name, rank, authorized, online, money));
		updateTab(getHandle());
	}

	public void earn(int money) {
		this.money += money;
	}

	public void disburse(int money) {
		this.money -= money;
	}


	public long getMoney() {
		return money;
	}

	public String getPassword() {
		return password == null ? "" : password;
	}

	public void setPassword(String password) {
		this.password = password.length() == 0 ? "" : CryptoUtils.getHash(password);
	}

	public int getOnline() {
		return (int) (System.currentTimeMillis() / 60000) - connectedAt + online;
	}

	public UserInfo getInfo() {
		return new UserInfo(name, password, rank, getOnline(), getIP(), money, ipbound, pmDisabled, ignoredPlayers, friends, darkTheme);
	}

	public Mute getActiveMute() {
		return mute;
	}

	public void clearMute(String moderator) {
		mute = null;
		if (moderator.equals(name)) return;
		msg("§aТы снова можешь писать в чат. Поблагодари §f" + moderator + "§a за размут.");
	}

	public void mute(Mute muteInfo) {
		mute = muteInfo;
	}

	public String getName() {
		return name;
	}

	public boolean isIPBound() {
		return ipbound;
	}

	private PlayerListItem getTab(ProxiedPlayer player) {
		PlayerListItem.Item item = new PlayerListItem.Item();
		item.setUsername(name);
		item.setDisplayName(rank.getNameColor() + name);
		item.setUuid(player.getUniqueId());
		PlayerListItem list = new PlayerListItem();
		list.setItems(new PlayerListItem.Item[] {item});
		list.setAction(PlayerListItem.Action.UPDATE_DISPLAY_NAME);
		return list;
	}

	public boolean setIPBound(boolean IPBound) {
		return this.ipbound = IPBound;
	}

	public void ignore(String player) {
		ignoredPlayers.add(Converter.smartLowercase(player));
	}

	public boolean unignore(String player) {
		return ignoredPlayers.remove(Converter.smartLowercase(player));
	}

	public boolean isIgnoring(String player) {
		return ignoredPlayers.contains(Converter.smartLowercase(player));
	}

	public List<String> getIgnoredPlayers() {
		return ignoredPlayers;
	}


	public boolean togglePM() {
		return this.pmDisabled = !pmDisabled;
	}

	public void setForcedIP(String forcedIP) {
		this.forcedIP = forcedIP;
	}
}
