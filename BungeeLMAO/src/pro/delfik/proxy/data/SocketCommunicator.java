package pro.delfik.proxy.data;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.Proxy;

public class SocketCommunicator implements Listener {
	@EventHandler
	public void event(SocketEvent event) {
		String channel = event.getChannel();
		if (channel.equals("summon")) {
			String[] s = event.getMsg().split("/");
			ProxiedPlayer player = BungeeCord.getInstance().getPlayer(s[0]);
			if (player == null) {
				return;
			}
			Proxy.send(player, s[1]);
		}
		
	}
}
