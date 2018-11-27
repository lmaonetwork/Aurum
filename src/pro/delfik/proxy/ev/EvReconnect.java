package pro.delfik.proxy.ev;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import implario.net.packet.PacketSSU;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.user.User;

public class EvReconnect implements Listener{
	@EventHandler
	public void event(ServerConnectEvent event) {
		User p = User.get(event.getPlayer());
		if (p == null) return;
		if (!event.getTarget().getName().equals("LOBBY_1"))
			Server.get("LOBBY_1").send(new PacketSSU(p.getServer(),
					Proxy.getServer(p.getServer()).getPlayers().size() - 1));
		p.setServer(event.getTarget().getName());
	}

	@EventHandler
	public void event(ServerConnectedEvent e) {
		User p = User.get(e.getPlayer());
		if (p == null) return;
		if (!e.getServer().getInfo().getName().equals("LOBBY_1"))
			Server.get("LOBBY_1").send(new PacketSSU(e.getServer().getInfo().getName(), e.getServer().getInfo().getPlayers().size()));
		if (e.getServer().getInfo().getName().equals("BW_2")) {
			e.getPlayer().setTabHeader(new TextComponent("Игроков онлайн: §e8648 §fиз §e20000"), new TextComponent("§eVimeWorld.ru"));
		} else e.getPlayer().setTabHeader(new TextComponent(
						"§a Вы находитесь в кластере §eLMAO/" + p.getServer() + " §a §a"),
				new TextComponent("§aФорум сервера: §elmaonetwork.ru\n§aГруппа сервера: §evk.com/lmaonetwork"));
		p.updateTab(e.getPlayer());
	}
}
