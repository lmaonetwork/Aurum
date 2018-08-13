package pro.delfik.proxy.data;

import net.md_5.bungee.BungeeCord;
import pro.delfik.net.Listener;
import pro.delfik.net.P2P;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketInit;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.ev.added.PacketEvent;
import pro.delfik.util.Scheduler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener implements Listener{

	private static int port;
	private static boolean closed;

	public static void init(int port){
		ServerListener.port = port;
		Scheduler.runThr(ServerListener::run);
	}

	public static void run(){
		try{
			ServerSocket read = new ServerSocket(port);
			while (!closed){
				Socket socket = read.accept();
				if(socket != null)new P2P(socket, Aurum.getCryptoUtils(), new ServerListener());
			}
			read.close();
		}catch (IOException ignored){}
	}

	public static void close(){
		closed = false;
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
		}
		BungeeCord.getInstance().getScheduler().runAsync(Aurum.instance, () -> BungeeCord.getInstance()
				.pluginManager.callEvent(new PacketEvent(server, packet)));
	}

	@Override
	public void off() {
		if(server != null)Server.removeServer(server);
	}
}
