package pro.delfik.proxy.ev;

import implario.net.Packet;
import implario.net.packet.*;
import implario.util.FileConverter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.ev.added.PacketEvent;
import pro.delfik.proxy.module.Ban;
import pro.delfik.proxy.module.Kick;
import pro.delfik.proxy.module.Mute;
import pro.delfik.proxy.stats.Top;
import pro.delfik.proxy.user.User;
import pro.delfik.proxy.user.UserConnection;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class EvPacket implements Listener {

	@EventHandler
	public void event(PacketEvent event) {
		Packet packet = event.getPacket();
		if (packet instanceof PacketPunishment) punish((PacketPunishment) packet);
		else if (packet instanceof PacketSummon) summon((PacketSummon) packet);
		else if (packet instanceof PacketInit) init((PacketInit) packet, event.getServer());
		else if (packet instanceof PacketWrite) write((PacketWrite) packet);
		else if (packet instanceof PacketRead) read((PacketRead) packet, event.getServer());
		else if (packet instanceof PacketOutAuth) outAuth((PacketOutAuth) packet);
		else if (packet instanceof PacketCheckUpdate) checkUpdate((PacketCheckUpdate) packet, event.getServer());
		else if (packet instanceof PacketTopUpdate) topUpdate((PacketTopUpdate) packet);
		else if (packet instanceof PacketMoney) coins((PacketMoney) packet);
	}

	private static void coins(PacketMoney packet) {
		User u = User.get(packet.getUser());
		if (u != null) u.earn(packet.getEarned());
	}

	private static void punish(PacketPunishment packet) {
		PacketPunishment.Punishment punishment = packet.getPunishment();
		switch (punishment) {
			case BAN:
				Ban.ban(packet.getNick(), packet.getReason(), packet.getTime(), packet.getModer());
				break;
			case UNBAN:
				Ban.unban(packet.getNick(), packet.getModer());
				break;
			case MUTE:
				Mute.mute(packet.getNick(), packet.getReason(), packet.getTime(), packet.getModer());
				break;
			case UNMUTE:
				Mute.unmute(packet.getNick(), packet.getModer());
				break;
			case KICK:
				Kick.kick(Proxy.getPlayer(packet.getNick()), packet.getModer(), packet.getReason());
				break;
		}
	}

	private static void summon(PacketSummon packet) {
		ProxiedPlayer p = Proxy.getPlayer(packet.getPlayer());
		ServerInfo info = Proxy.getServer(packet.getServer());
		if (p != null && info != null) p.connect(info);
	}

	private static void init(PacketInit packet, String serverName) {
		Proxy.i().getScheduler().schedule(Aurum.instance, () -> {
			ServerInfo from = Proxy.getServer(packet.getServer());
			if (from == null) return;
			if (from.getName().startsWith("LOBBY_")) {
				Server server = Server.get(serverName);
				for (Server i : Server.getServers()) {
					ServerInfo info = Proxy.getServer(i.getServer());
					server.send(new PacketSSU(i.getServer(), info.getPlayers().size()));
				}
			}
			for (ServerInfo serverInfo : Proxy.getServers().values())
				if (serverInfo.getName().startsWith("LOBBY_"))
					Server.get(serverInfo.getName()).send(new PacketSSU(from.getName(), from.getPlayers().size()));

		}, 5, TimeUnit.SECONDS);
	}

	private static void write(PacketWrite packet) {
		FileConverter.write(DataIO.getFile(packet.getName()), packet.getFile());
	}

	private static void read(PacketRead packet, String serverName) {
		byte write[] = FileConverter.read(DataIO.getFile(packet.getRead()));
		if (write == null) return;
		Server.get(serverName).send(new PacketWrite(packet.getWrite(), write));
	}

	private static void outAuth(PacketOutAuth packet) {
		UserConnection.outAuth.put(packet.getNick(), packet.getIp());
	}

	private static void checkUpdate(PacketCheckUpdate packet, String server) {
		File f = DataIO.getFile("Core/plugins/" + packet.getPlugin());
		if (!f.exists()) return;
		long time = packet.getTime();
		if (f.lastModified() <= time) return;
		Server.get(server).send(new PacketWrite("plugins/" + f.getName(), FileConverter.read(f)));
	}

	private static void topUpdate(PacketTopUpdate packet) {
		Top top = Top.get(packet.type());
		if (top != null) top.update(packet);
	}

}
