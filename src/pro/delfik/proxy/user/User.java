package pro.delfik.proxy.user;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import pro.delfik.net.packet.PacketAuth;
import pro.delfik.net.packet.PacketPex;
import pro.delfik.net.packet.PacketUser;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.data.PlayerDataManager;
import pro.delfik.util.Converter;
import pro.delfik.util.CryptoUtils;
import pro.delfik.util.Rank;
import pro.delfik.util.TimedList;
import pro.delfik.util.U;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class User {

	public static final TimedList<String> allowedIP = new TimedList<>(60);

	public static final String path = "players";

	public static String getPath(String nick){
		return path + "/" + Converter.smartLowercase(nick) + "/";
	}
	
	private static final HashMap<String, User> list = new HashMap<>();

	static{
		UserInfo info = new UserInfo("CONSOLE");
		info.rank = Rank.DEV;
		list.put("CONSOLE", new User(info, null, true));
	}
	
	public static User get(String name) {
		return list.get(Converter.smartLowercase(name));
	}

	public static User get(CommandSender sender) {
		return list.get(Converter.smartLowercase(sender.getName()));
	}
	
	public static User load(String name) {
		UserInfo info = PlayerDataManager.load(name);
		if (info == null) return new User(new UserInfo(name, "", 0, Rank.PLAYER,
			0L, "", false, new ArrayList<>(), false, new ArrayList<>()),null, false);
		
		boolean auth = false;
		Mutes.MuteInfo mute = Mutes.get(name);
		String lastSeenIP = info.getIp();
		
		if (lastSeenIP != null && info.ipbound) {
			ProxiedPlayer p = Proxy.getPlayer(name);
			if (lastSeenIP.equals(p.getAddress().getHostName())) {
				auth = true;
				U.msg(p, "§aАвтоматическая авторизация прошла успешно.");
			} else if (!allowedIP.contains(name.toLowerCase())) throw new DifferentIPException(name);
		}
		
		return new User(info, mute, auth);
	}

	public static void unload(String name) {
		User p = get(name);
		if (p == null) return;
		list.remove(Converter.smartLowercase(name));
		if (!p.authorized) return;
		PlayerDataManager.save(p.getInfo());
	}

	public static Collection<User> getAll() {
		return list.values();
	}
	
	// non-static
	
	public final String name; // Ник игрока
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

	private String last = "", lastLast;

	public void setLast(String last){
		lastLast = this.last;
		this.last = last;
	}

	public String getLast() {
		return last;
	}

	public String getLastLast() {
		return lastLast;
	}

	public String lastWriter;
	
	public User(UserInfo userInfo, Mutes.MuteInfo mute, boolean auth) {
		this.name = userInfo.getName();
		this.rank = userInfo.getRank();
		this.password = userInfo.getPassword();
		this.online = userInfo.getOnline();
		this.money = userInfo.getMoney();
		this.mute = mute;
		this.ipbound = userInfo.isIPBound();
		this.ignoredPlayers = userInfo.getIgnoredPlayers();

		if (auth) this.authorize();
		this.connectedAt = System.currentTimeMillis();
		list.put(Converter.smartLowercase(name), this);
	}
	
	// Implementation
	public void msg(Object... o) {
		U.msg(getSender(), o);
	}

	public void kick(String reason) {
		getHandle().disconnect(new TextComponent(reason));
	}

	public String getIP() {return getHandle().getAddress().getHostName();}

	public ServerInfo getServerInfo() {return getHandle().getServer().getInfo();}

	public void updateTab(ProxiedPlayer handle){
		Proxy.i().getScheduler().schedule(Aurum.instance, () -> {
			PlayerListItem item = getTab(handle);
			for (ProxiedPlayer player : handle.getServer().getInfo().getPlayers()){
				User user = get(player);
				player.unsafe().sendPacket(item);
				if(user != null)
					handle.unsafe().sendPacket(user.getTab(player));
			}
		}, 1, TimeUnit.SECONDS);
	}

	public void tell(User dest, String msg) {
		if (isIgnoring(dest.getName())) throw new ExCustom("§cВы не можете писать игроку, который находится у вас в игноре.");
		if (dest.isIgnoring(dest.getName())) throw new ExCustom("§cВы находитесь в чёрном списке у игрока §e" + dest.getName() + "§c.");
		lastWriter = dest.getName();
		msg("§e[§f" + dest.getName() + "§e -> §fВы§e] " + msg);
		dest.telled(dest, msg);
	}

	private void telled(User user, String msg){
		lastWriter = user.getName();
		msg("§e[§fВы §e-> §f" + user.getName() + "§e] " + msg);
	}
	
	// Getters & Setters
	public CommandSender getSender(){
		return name.equals("CONSOLE") ? Proxy.getConsole() : getHandle();
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
		if (server != null) server.send(new PacketAuth(name));
	}
	
	public void setRank(Rank rank) {
		this.rank = rank;
		server().send(new PacketPex(name, rank));
		updateTab(getHandle());
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
		server().send(new PacketUser(name, rank, authorized, online, money));
		updateTab(getHandle());
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
		Database.sendUpdate("UPDATE Users SET money = " + money + " WHERE name = " + Converter.smartLowercase(name));
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
	
	public UserInfo getInfo() {
		return new UserInfo(name, password, money, rank, getOnline(), getIP(), ipbound, ignoredPlayers, pmDisabled, friends);
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

	private PlayerListItem getTab(ProxiedPlayer player){
		PlayerListItem.Item item = new PlayerListItem.Item();
		item.setUsername(name);
		item.setDisplayName(rank.getNameColor() + name);
		item.setUuid(player.getUniqueId());
		PlayerListItem list = new PlayerListItem();
		list.setItems(new PlayerListItem.Item[]{item});
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
}
