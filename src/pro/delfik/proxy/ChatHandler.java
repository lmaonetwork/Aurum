package pro.delfik.proxy;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.util.Rank;

public class ChatHandler implements Listener {
	@EventHandler
	public void onChat(ChatEvent e) {
		if (e.isCommand()) return;
		if (!(e.getSender() instanceof UserConnection)) return;
		UserConnection c = (UserConnection) e.getSender();
		Person p = Person.get(c.getName());
		
		if (p.hasRank(Rank.BUILDER) && e.getMessage().startsWith("%")) {
			e.setCancelled(true);
			for (Person receiver : Person.getAll()) if (receiver.hasRank(Rank.BUILDER)) receiver.msg("§c§o%A% " + p.getRank().getNameColor() + p.name + "§7§o: §f§o" + e.getMessage().substring(1));
			return;
		}
		
		Mutes.MuteInfo mute = p.getActiveMute();
		if (mute == null) return;
		if (mute.until < System.currentTimeMillis()) Mutes.clear(p.name);
		else {
			e.setCancelled(true);
			mute.sendChatDisallowMessage(c);
		}
	}
}
