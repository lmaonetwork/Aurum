package pro.delfik.proxy.connection;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.net.P2P;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketUser;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.permissions.Person;

import java.util.HashMap;
import java.util.Map;

public class Server {
	private static final Map<String, Server> servers = new HashMap<>();

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
			Person person = Person.get(player.getName());
			send(new PacketUser(person.getName(), person.getRank(), person.isAuthorized()));
		}
	}

	public String getServer() {
		return server;
	}

	public void send(Packet packet){
		p2p.send(packet);
	}
}
