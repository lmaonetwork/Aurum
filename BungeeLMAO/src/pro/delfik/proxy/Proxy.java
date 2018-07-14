package pro.delfik.proxy;

import io.netty.handler.timeout.ReadTimeoutException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pro.delfik.proxy.data.SocketEvent;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class Proxy {
	public static CommandSender getConsole() {
		return BungeeCord.getInstance().getConsole();
	}
	
	public static BungeeCord i() {
		return BungeeCord.getInstance();
	}
	
	public static Map<String, ServerInfo> getServers() {
		return i().getServers();
	}
	
	public static void registerCommand(Command command) {
		BungeeCord.getInstance().getPluginManager().registerCommand(AurumPlugin.instance, command);
	}
	
	public static ProxiedPlayer getPlayer(String nickname) {
		return i().getPlayer(nickname);
	}
	
	public static ServerInfo hub() {
		return i().getServerInfo("LOBBY_1");
	}
	
	public static void send(ProxiedPlayer player, String server) {
		ServerInfo info = getServerInfo(server);
		if (info != null) player.connect(info);
	}
	
	private static ServerInfo getServerInfo(String server) {
		return i().getServerInfo(server);
	}
	
	public static ServerInfo getServer(String arg) {
		return i().getServerInfo(arg);
	}
	
	public static void log(Level level, String message) {
		BungeeCord.getInstance().getLogger().log(level, message);
	}
	
	public static void callEvent(SocketEvent socketEvent) {
		i().getPluginManager().callEvent(socketEvent);
	}
	
	public static void ifServerOffline(ServerInfo server, Runnable offline, Consumer<ServerPing> online) {
		System.out.println("Проверяем сервер " + server + " на онлайн...");
		server.ping((ping, error) -> {
			System.out.println("Пинг успешен? - " + (error == null));
			if (error != null) error.printStackTrace();
			else System.out.println("Ошибки нет ^^");
			synchronized (server) {
				if (error == null || error instanceof ReadTimeoutException) {
					if (online != null) online.accept(ping);
				} else {
					if (offline != null) offline.run();
				}
			}
		});
	}
}
