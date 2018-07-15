package pro.delfik.proxy.data;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketPunishment;
import pro.delfik.proxy.command.handling.Bans;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.connection.PacketEvent;

public class PacketListener implements Listener{
	@EventHandler
	public void event(PacketEvent event){
		Packet packet = event.getPacket();
		if(packet instanceof PacketPunishment){
			PacketPunishment punish = (PacketPunishment)packet;
			PacketPunishment.Punishment punishment = punish.getPunishment();
			if(punishment == PacketPunishment.Punishment.BAN){
				Bans.ban(punish.getNick(), punish.getReason(), punish.getTime(), punish.getModer());
			}else if(punishment == PacketPunishment.Punishment.UNBAN){
				Bans.unban(punish.getNick(), punish.getModer());
			}else if(punishment == PacketPunishment.Punishment.MUTE){
				Mutes.mute(punish.getNick(), punish.getReason(), punish.getTime(), punish.getModer());
			}else if(punishment == PacketPunishment.Punishment.UNMUTE){
				Mutes.unmute(punish.getNick(), punish.getModer());
			}
		}
	}
}
