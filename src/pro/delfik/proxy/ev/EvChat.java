package pro.delfik.proxy.ev;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.modules.Chat;
import pro.delfik.proxy.modules.Mute;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.User;
import implario.util.Rank;
import implario.util.Scheduler;
import implario.util.StringUtils;

import java.util.HashMap;

public class EvChat implements Listener{
	private static volatile HashMap<String, Integer> messages = new HashMap<>();

	public EvChat() {
		Scheduler.addTask(new Scheduler.RunTask(10, () -> messages = new HashMap<>()));
	}

	@EventHandler
	public void event(ChatEvent event) {
		if(event.isCommand()) return;
		User user = User.get(((UserConnection) event.getSender()).getName());
		if(user == null || !user.isAuthorized()) return;
		if(checkFlood(event, user))return;
		if(adminChat(event, user))return;
		if(checkMute(event, user))return;
		event.setMessage(Chat.applyMat(event.getMessage()));
		if(antiFlood(event, user))return;
	}

	private boolean adminChat(ChatEvent event, User user){
		if(user.hasRank(Rank.BUILDER) && event.getMessage().startsWith("%")){
			event.setCancelled(true);
			String message = "§c§o%staff" + user.getRank().getNameColor() + " " + user.name + "§7§o: §f§o" + event.getMessage().substring(1);
			for (User receiver : User.getAll())
				if (receiver.hasRank(Rank.BUILDER)) receiver.msg(message);
			return true;
		}
		return false;
	}

	private boolean checkFlood(ChatEvent event, User user){
		Integer integer = messages.get(event.getReceiver().getAddress().getHostName());
		if(integer == null) integer = 0;
		integer = integer + 1;
		if(integer > 3){
			event.setCancelled(true);
			user.msg("Не флуди");
			return true;
		}else if(integer > 10){
			event.getSender().disconnect(new TextComponent("Не флуди"));
			return true;
		}
		messages.put(event.getReceiver().getAddress().getHostName(), integer);
		return false;
	}

	private boolean checkMute(ChatEvent event, User user){
		Mute mute = user.getActiveMute();
		if (mute != null){
			if (mute.getUntil() < System.currentTimeMillis()){
				Mute.clear(user.name);
				return false;
			}
			event.setCancelled(true);
			mute.sendChatDisallowMessage(user.getHandle());
			return true;
		}
		return false;
	}

	private boolean antiFlood(ChatEvent event, User user){
		String message = event.getMessage();
		if(user.getLast().equals(message) && user.getLastLast().equals(message)){
			Mute.mute(user.getName(), "флуд", 30, "Антифлуд");
			user.setLast("");
			event.setCancelled(true);
			return true;
		}
		user.setLast(message);
		return false;
	}
}
