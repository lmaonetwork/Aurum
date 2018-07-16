package pro.delfik.proxy.data;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketInit;
import pro.delfik.net.packet.PacketPunishment;
import pro.delfik.net.packet.PacketSSU;
import pro.delfik.net.packet.PacketSummon;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.handling.Bans;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.connection.PacketEvent;
import pro.delfik.net.packet.PacketToggle;
import pro.delfik.proxy.connection.Server;

public class PacketListener implements Listener {
	@EventHandler
	public void event(PacketEvent event) {
		Packet packet = event.getPacket();
		if (packet instanceof PacketPunishment) {
			PacketPunishment punish = (PacketPunishment) packet;
			PacketPunishment.Punishment punishment = punish.getPunishment();
			if (punishment == PacketPunishment.Punishment.BAN) {
				Bans.ban(punish.getNick(), punish.getReason(), punish.getTime(), punish.getModer());
			} else if (punishment == PacketPunishment.Punishment.UNBAN) {
				Bans.unban(punish.getNick(), punish.getModer());
			} else if (punishment == PacketPunishment.Punishment.MUTE) {
				Mutes.mute(punish.getNick(), punish.getReason(), punish.getTime(), punish.getModer());
			} else if (punishment == PacketPunishment.Punishment.UNMUTE) {
				Mutes.unmute(punish.getNick(), punish.getModer());
			}
			// TODO ban-ip & kick
		} else if (packet instanceof PacketToggle) {
			// TODO toggle pmdisable & ip
		} else if (packet instanceof PacketSummon) {
			ProxiedPlayer p = Proxy.getPlayer(((PacketSummon) packet).getPlayer());
			ServerInfo info = Proxy.getServer(((PacketSummon) packet).getServer());
			if (p != null && info != null) p.connect(info);
		} else if (packet instanceof PacketInit) {
			ServerInfo info = Proxy.getServer(((PacketInit) packet).getServer());
			if (info == null) return;
			if (info.getName().startsWith("LOBBY_")) {
				Server server = Server.get(event.getServer());
				for (ServerInfo i : Proxy.getServers().values()) {
					server.send(new PacketSSU(i.getName(), i.getPlayers().size()));
					Proxy.ifServerOffline(i, () -> server.send(new PacketSSU(i.getName(), -1)), null);
				}
			} else for (ServerInfo serverInfo : Proxy.getServers().values())
				if (serverInfo.getName().startsWith("LOBBY_"))
					Server.get(serverInfo.getName()).send(new PacketSSU(info.getName(), info.getPlayers().size()));
			
		}
	}
}
