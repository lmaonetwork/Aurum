package pro.delfik.proxy.ev;

import implario.util.Rank;
import implario.util.Scheduler;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.user.User;
import pro.delfik.proxy.module.Chat;
import pro.delfik.proxy.module.Mute;

import java.util.HashMap;

public class EvChat implements Listener {

	private static volatile HashMap<String, Integer> messages = new HashMap<>();
	public static final String[] wittyComments = {
			"Во имя правосудия!", "Слава Арстоцке!", "Орбистан превыше всего.", "#implario.", "Один, два, три.", "Lorem ipsum dolor sit amet",
			"&7&oСообщение удалено.", "42", "66&f6", "Сто двадцать три", "CXXI&fII", "3 * 41", "Десятое число Лукаса.", "2^7 - 5", "0x7b", "0o173", "0b011&f11011",
			"(int) '{'", "Я был 1, только 2 капитана, 3 мушкетёра...", "Ein zwei drei", "Uno dos tres", "One two three", "Раз два три", "Связь", "Арарарарарар",
			"Адзін два тры", "Illuminati confirmed!"
	};

	public EvChat() {
		Scheduler.addTask(new Scheduler.RunTask(10, () -> messages = new HashMap<>()));
	}

	@EventHandler
	public void event(ChatEvent event) {
		if (event.isCommand()) return;
		User user = User.get(((UserConnection) event.getSender()).getName());
		if (user == null || !user.isAuthorized()) return;
		addWittyComment(event);
		if (checkFlood(event, user)) return;
		if (adminChat(event, user)) return;
		if (checkMute(event, user)) return;
		event.setMessage(Chat.applyMat(event.getMessage()));
		if (antiFlood(event, user)) return;
	}

	private void addWittyComment(ChatEvent event) {
		if (!"123".equals(event.getMessage())) return;
		event.setMessage(wittyComments[(int) (wittyComments.length * Math.random())]);
	}

	private boolean adminChat(ChatEvent event, User user) {
		if (user.hasRank(Rank.BUILDER) && event.getMessage().startsWith("%")) {
			event.setCancelled(true);
			String message = "§c§o%staff" + user.getRank().getNameColor() + " " + user.getName() + "§7§o: §f§o" + event.getMessage().substring(1);
			for (User receiver : User.getAll())
				if (receiver.hasRank(Rank.BUILDER)) receiver.msg(message);
			return true;
		}
		return false;
	}

	private boolean checkFlood(ChatEvent event, User user) {
		Integer integer = messages.get(event.getReceiver().getAddress().getHostName());
		if (integer == null) integer = 0;
		integer = integer + 1;
		if (integer > 3) {
			event.setCancelled(true);
			user.msg("Не флуди");
			return true;
		}
		if (integer > 10) {
			event.getSender().disconnect(new TextComponent("Не флуди"));
			return true;
		}
		messages.put(event.getReceiver().getAddress().getHostName(), integer);
		return false;
	}

	private boolean checkMute(ChatEvent event, User user) {
		Mute mute = user.getActiveMute();
		if (mute != null) {
			if (mute.getUntil() < System.currentTimeMillis()) {
				Mute.clear(user.getName());
				return false;
			}
			event.setCancelled(true);
			mute.sendChatDisallowMessage(user.getHandle());
			return true;
		}
		return false;
	}

	private boolean antiFlood(ChatEvent event, User user) {
		String message = event.getMessage();
		if (user.getLast().equals(message) && user.getLastLast().equals(message)) {
			Mute.mute(user.getName(), "флуд", 30, "Антифлуд");
			user.setLast("");
			event.setCancelled(true);
			return true;
		}
		user.setLast(message);
		return false;
	}

}
