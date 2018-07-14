package pro.delfik.proxy.data;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.AurumPlugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class DataEvent {
	
	public static void broadevent(String channel, String message) {
		ServerInfo[] infos = BungeeCord.getInstance().getServers().values().toArray(new ServerInfo[0]);
		for(ServerInfo info : infos) event(info, channel, message);
	}
	
	public static void event(String motd, String channel, String message) {
		try {
			ServerInfo ServerInfo = BungeeCord.getInstance().getServerInfo(motd);
			if (ServerInfo != null) event(ServerInfo, channel, message);
		} catch (Exception ignored) {}
	}
	
	public static void event(Server server, String channel, String message) {
		if (server != null) event(server.getInfo(), channel, message);
	}
	
	public static void typeevent(String type, String channel, String message) {
		ServerInfo[] infos = BungeeCord.getInstance().getServers().values().toArray(new ServerInfo[0]);
		for (ServerInfo info : infos)
			if (info.getName().replaceAll("_.*", "").equals(type)) event(info, channel, message);
	}
	
	public static void event(ServerInfo info, String channel, String message) {
		if (info != null) {
			Socket socket = null;
			try {
				int port = DataPort.getPort(info.getName());
				if (port == -1) return;
				
				socket = new Socket(info.getAddress().getHostName(), port);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				String crypt = AurumPlugin.getCryptoUtils().encrypt(channel + '\n' + message);
				writer.write(crypt);
				writer.newLine();
				writer.flush();
				socket.close();
			} catch (IOException var8) {
				try {socket.close();} catch (Exception ignored) {}
			}
			
		}
	}
}
