package pro.delfik.proxy.data;

import __google_.net.Response;
import implario.net.Connector;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import implario.net.Packet;
import implario.net.packet.PacketUser;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Server {
	private static final Map<String, Server> servers = new HashMap<>();
	
	public static Collection<Server> getServers() {
		return servers.values();
	}
	
	public static void removeServer(String server){
		servers.get(server);
	}

	public static Server get(String server){
		return servers.get(server);
	}

	private final String server;
	private final Connector connector;

	public Server(String server, Connector connector){
		this.server = server;
		this.connector = connector;
		servers.put(server, this);
		for(ProxiedPlayer player : Proxy.getServer(server).getPlayers()){
			User user = User.get(player.getName());
			send(new PacketUser(user.getInfo(), user.isAuthorized()));
		}
	}

	public String getServer() {
		return server;
	}

	public void send(Packet packet){
		connector.write(new Response(0, packet.zip()));
	}
}
