package pro.delfik.proxy.user;

import implario.util.Byteable;
import implario.util.Converter;
import implario.util.Rank;
import implario.util.debug.UserInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PlayerListItem;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.module.Mute;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface User extends Byteable {
	// Implementation
	void msg(Object... o);

	void kick(String reason);

	void updateTab(ProxiedPlayer handle);

	void sendPM(User dest, String msg);

	// Getters & Setters
	CommandSender getSender();

	String getIP();

	String getLastIP();

	ServerInfo getServerInfo();

	ProxiedPlayer getHandle();

	boolean isAuthorized();

	void authorize();

	void setRank(Rank rank);

	Rank getRank();

	void setLast(String last);

	String getLast();

	String getLastLast();

	boolean hasRank(Rank rank);

	Server server();

	String getServer();

	void setServer(String server);

	void earn(int money);

	long getMoney();

	String getPassword();

	void setPassword(String password);

	int getOnline();

	UserInfo getInfo();

	Mute getActiveMute();

	void clearMute(String moderator);

	void mute(Mute muteInfo);

	String getName();

	boolean isIPBound();

	boolean setIPBound(boolean IPBound);

	void ignore(String player);

	boolean unignore(String player);

	boolean isIgnoring(String player);

	List<String> getIgnoredPlayers();

	boolean togglePM();

	boolean isPmDisabled();

	void setForcedIP(String forcedIP);

	String toggleDarkTheme();

    void recievePM(User user, String msg);

	PlayerListItem getTab(ProxiedPlayer player);

    void unload();

	String getLastPenPal();

	HashMap<String, User> list = new HashMap<>();

    static User get(String name) {
        return list.get(Converter.smartLowercase(name));
    }

    static User get(CommandSender sender) {
		return get(sender.getName());
	}

    static void put(String name, User user){
        list.put(Converter.smartLowercase(name), user);
    }

	static String getPath(String nick) {
		return "players/" + Converter.smartLowercase(nick) + "/";
	}

	static Collection<User> getAll() {
		return list.values();
	}

	static UserInfo read(String name){
    	return UserConnection.read(name);
	}

	static void save(UserInfo info){
    	UserConnection.save(info);
	}

	static User loadOffline(String name){
    	return UserConnection.load(name);
	}

	static void remove(String name){
    	list.remove(name);
	}

	static User getUserHost(String host){
    	for(User user : getAll()){
    		ProxiedPlayer player = user.getHandle();
    		if(player == null)continue;
    		if(player.getAddress().getAddress().getHostAddress().equals(host))return user;
		}
		return null;
	}
}
