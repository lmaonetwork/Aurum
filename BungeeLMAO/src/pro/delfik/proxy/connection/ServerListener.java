package pro.delfik.proxy.connection;

import net.md_5.bungee.BungeeCord;
import pro.delfik.net.Listener;
import pro.delfik.net.P2P;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketInit;
import pro.delfik.proxy.AurumPlugin;
import pro.delfik.util.Scheduler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener implements Listener{

	private static int port;

	public static void init(int port){
		ServerListener.port = port;
		Scheduler.runThr(ServerListener::run);
	}

	public static void run(){
		try{
			ServerSocket read = new ServerSocket(port);
			while (true){
				Socket socket = read.accept();
				if(socket != null)new P2P(socket, AurumPlugin.getCryptoUtils(), new ServerListener());
			}
		}catch (IOException ex){}
	}

	private P2P p2p;
	private String server = null;

	@Override
	public void on(P2P p2p) {
		this.p2p = p2p;
	}

	@Override
	public void update(Packet packet) {
		if(packet instanceof PacketInit){
			this.server = ((PacketInit) packet).getServer();
			new Server(server, p2p);
			return;
		}
		BungeeCord.getInstance().getScheduler().runAsync(AurumPlugin.instance, () -> BungeeCord.getInstance()
				.pluginManager.callEvent(new PacketEvent(server, packet)));
	}

	@Override
	public void off() {
		if(server != null)Server.removeServer(server);
	}
}
