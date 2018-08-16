package pro.delfik.proxy.ev;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketInit;
import pro.delfik.net.packet.PacketPunishment;
import pro.delfik.net.packet.PacketRead;
import pro.delfik.net.packet.PacketSSU;
import pro.delfik.net.packet.PacketSummon;
import pro.delfik.net.packet.PacketUpdateTop;
import pro.delfik.net.packet.PacketWrite;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.user.Bans;
import pro.delfik.proxy.user.Kicks;
import pro.delfik.proxy.user.Mute;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.ev.added.PacketEvent;
import pro.delfik.proxy.user.SfTop;
import pro.delfik.util.FileConverter;

import java.util.concurrent.TimeUnit;

public class EvPacket implements Listener{
	@EventHandler
	public void event(PacketEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof PacketPunishment){
			PacketPunishment punish = (PacketPunishment) packet;
			PacketPunishment.Punishment punishment = punish.getPunishment();
			if(punishment == PacketPunishment.Punishment.BAN)
				Bans.ban(punish.getNick(), punish.getReason(), punish.getTime(), punish.getModer());
			else if(punishment == PacketPunishment.Punishment.UNBAN)
				Bans.unban(punish.getNick(), punish.getModer());
			else if(punishment == PacketPunishment.Punishment.MUTE)
				Mute.mute(punish.getNick(), punish.getReason(), punish.getTime(), punish.getModer());
			else if(punishment == PacketPunishment.Punishment.UNMUTE)
				Mute.unmute(punish.getNick(), punish.getModer());
			else if(punishment == PacketPunishment.Punishment.KICK)
				Kicks.kick(Proxy.getPlayer(punish.getNick()), punish.getModer(), punish.getReason());
		}else if(packet instanceof PacketSummon){
			ProxiedPlayer p = Proxy.getPlayer(((PacketSummon) packet).getPlayer());
			ServerInfo info = Proxy.getServer(((PacketSummon) packet).getServer());
			if(p != null && info != null) p.connect(info);
		}else if(packet instanceof PacketInit){
			Proxy.i().getScheduler().schedule(Aurum.instance, () -> {
				ServerInfo from = Proxy.getServer(((PacketInit) packet).getServer());
				if(from == null) return;
				if(from.getName().startsWith("LOBBY_")){
					Server server = Server.get(event.getServer());
					for (Server i : Server.getServers()){
						ServerInfo info = Proxy.getServer(i.getServer());
						server.send(new PacketSSU(i.getServer(), info.getPlayers().size()));
					}
				}
				for (ServerInfo serverInfo : Proxy.getServers().values())
					if(serverInfo.getName().startsWith("LOBBY_"))
						Server.get(serverInfo.getName()).send(new PacketSSU(from.getName(), from.getPlayers().size()));

			}, 5, TimeUnit.SECONDS);
		}else if(packet instanceof PacketWrite){
			PacketWrite write = (PacketWrite)packet;
			FileConverter.write(DataIO.getFile(write.getName()), write.getFile());
		}else if(packet instanceof PacketRead){
			PacketRead read = (PacketRead)packet;
			byte write[] = FileConverter.read(DataIO.getFile(read.getRead()));
			if(write == null)return;
			Server.get(event.getServer()).send(new PacketWrite(read.getWrite(), write));
		}else if(event.getPacket() instanceof PacketUpdateTop)
			SfTop.updateTop((PacketUpdateTop)event.getPacket());
	}
}
