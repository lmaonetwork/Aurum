package pro.delfik.proxy.data;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import implario.net.P2P;
import implario.net.Packet;
import implario.net.packet.PacketUser;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.user.User;

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

	private final P2P p2p;

	public Server(String server, P2P p2p){
		this.server = server;
		this.p2p = p2p;
		servers.put(server, this);
		for(ProxiedPlayer player : Proxy.getServer(server).getPlayers()){
			User user = User.get(player.getName());
			send(new PacketUser(user.getName(), user.getRank(), user.isAuthorized(), user.getOnline(), ((int) user.getMoney())));
		}
	}

	public String getServer() {
		return server;
	}

	public void send(Packet packet){
		p2p.send(packet);
	}
}
