package pro.delfik.proxy.ev;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.user.Mute;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;
import pro.delfik.util.Scheduler;
import pro.delfik.util.StringUtils;

import java.util.HashMap;

public class EvChat implements Listener{
	private static volatile HashMap<String, Integer> messages = new HashMap<>();
	private static final char[] dontChar = "\"'!?.,()@#№$%:^*&123457890-_=+~`[]{}\\|/<".toCharArray();
	private static final HashMap<Character, Character> map = new HashMap<>();
	private static final String[] nyas;

	public static Char mat = new Char();

	static {
		String read = DataIO.readFile("config/mat");
		if (read != null) mat = new Char(read);
	}

	public static void unload(){
		DataIO.writeFile("config/mat", mat.toString());
	}

	public EvChat() {
		Scheduler.addTask(new Scheduler.RunTask(10, () -> messages = new HashMap<>()));
	}

	public static String remake(String in) {
		StringBuilder result = new StringBuilder();
		char last = 4920;
		for(char c : in.toCharArray()) {
			if (last != c && !StringUtils.contains(dontChar, c)) {
				last = c;
				result.append(c);
			}
		}
		return remak(result.toString());
	}

	private static String remak(String in) {
		StringBuilder buf = new StringBuilder(in.length());
		for(char c : in.toCharArray()) {
			Character l = map.get(c);
			if (l == null) l = c;
			buf.append(l);
		}
		return buf.toString();
	}

	@EventHandler
	public void event(ChatEvent event) {
		if(event.isCommand()) return;
		User user = User.get(((UserConnection) event.getSender()).getName());
		if(user == null || !user.isAuthorized()) return;
		if(checkFlood(event, user))return;
		if(adminChat(event, user))return;
		if(checkMute(event, user))return;
		event.setMessage(applyMat(event.getMessage()));
		if(antiFlood(event, user))return;
	}

	private boolean adminChat(ChatEvent event, User user){
		if(user.hasRank(Rank.BUILDER) && event.getMessage().startsWith("%")){
			event.setCancelled(true);
			String message = "§c§o%A% " + user.getRank().getNameColor() + user.name + "§7§o: §f§o" + event.getMessage().substring(1);
			for (User receiver : User.getAll())
				if (receiver.hasRank(Rank.BUILDER))
					receiver.msg(message);
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

	public static String applyMat(String message) {
		StringBuilder sb = new StringBuilder();
		String last = "";
		String[] var3 = message.split(" ");
		int var4 = var3.length;

		for(int var5 = 0; var5 < var4; ++var5) {
			String result = var3[var5];
			result = result.replaceAll("[^A-Za-z0-9А-я\"'!?.,()@#№$%:\\^\\*\\&\\-\\_=+~`\\[\\]\\{\\}\\|\\/<>;Ёё]", "");
			String remake = remake(result).toLowerCase();
			if (remake.length() != 0) {
				if (mat.contains(remake + " ")) {
					sb.append(last);
					sb.append(' ');
					sb.append(toNya());
					last = "";
					continue;
				}

				if (mat.contains(remake(last).toLowerCase() + remake + " ")) {
					sb.append(toNya());
					last = "";
					continue;
				}
			}

			sb.append(last);
			sb.append(' ');
			last = result;
		}

		sb.append(last);
		return remakeToReal(sb.toString()).substring(1);
	}

	private static String toNya() {
		return '*' + nyas[(int)(Math.random() * (double)nyas.length)] + "*";
	}

	private static String remakeToReal(String s) {
		StringBuilder sb = new StringBuilder();
		char last = 4920;
		char lastLast = 4920;
		char[] var4 = s.toCharArray();
		int var5 = var4.length;

		for(int var6 = 0; var6 < var5; ++var6) {
			char c = var4[var6];
			if (c != last || last != lastLast) {
				lastLast = last;
				last = c;
				sb.append(c);
			}
		}

		return sb.toString();
	}

	static {
		map.put('6', 'б');
		map.put('k', 'к');
		map.put('a', 'а');
		map.put('o', 'о');
		map.put('s', 'с');
		map.put('c', 'с');
		map.put('b', 'ь');
		map.put('y', 'у');
		map.put('p', 'р');
		map.put('g', 'д');
		map.put('m', 'м');
		map.put('z', 'з');
		map.put('r', 'р');
		map.put('d', 'д');
		map.put('e', 'е');
		map.put('t', 'т');
		map.put('x', 'х');
		map.put('l', 'л');
		map.put('n', 'п');
		nyas = new String[]{"мяу", "meow", "меов", "ня", "nya", "горрила", "арангутанг"};
	}
	public static class Char {
		private final HashMap<Character, Char> map = new HashMap();

		public Char() {
		}

		public Char(String input) {
			if (input.length() != 0) {
				String local = "";
				char ch = '?';
				String[] var4 = input.split("\n");
				int var5 = var4.length;

				for(int var6 = 0; var6 < var5; ++var6) {
					String s = var4[var6];
					if (s.length() > 0) {
						char c = s.charAt(0);
						if (c != '?') {
							if (ch != '?') {
								this.map.put(ch, new Char(local));
								local = "";
							}

							ch = c;
						} else {
							local = local + s.substring(1, s.length()) + "\n";
						}
					}
				}

				if (ch != '?') {
					this.map.put(ch, new Char(local));
				}

			}
		}

		public Char add(String s) {
			char c = s.charAt(0);
			Char magic = (Char)this.map.get(c);
			if (magic == null) {
				magic = new Char();
			}

			if (s.length() > 1) {
				magic.add(s.substring(1, s.length()));
			}

			this.map.put(c, magic);
			return this;
		}

		public boolean remove(String s) {
			if (this.map.size() == 0) {
				return true;
			} else {
				Char c = (Char)this.map.get(s.charAt(0));
				if (c == null) {
					return false;
				} else {
					if (c.remove(s.substring(1, s.length()))) {
						this.map.remove(s.charAt(0));
					}

					return this.map.size() == 0;
				}
			}
		}

		public boolean contains(String s) {
			if (s.length() == 0) {
				return true;
			} else {
				char c = s.charAt(0);
				Char magic = (Char)this.map.get(c);
				return magic == null ? false : magic.contains(s.substring(1, s.length()));
			}
		}

		public String toString() {
			if (this.map.size() == 0) {
				return "";
			} else {
				StringBuilder s = new StringBuilder();
				Character[] keys = (Character[])this.map.keySet().toArray(new Character[0]);
				Char[] values = (Char[])this.map.values().toArray(new Char[0]);
				int size = keys.length;

				for(int i = 0; i < size; ++i) {
					s.append("\n" + keys[i]);
					String[] var6 = values[i].toString().split("\n");
					int var7 = var6.length;

					for(int var8 = 0; var8 < var7; ++var8) {
						String a = var6[var8];
						s.append("?" + a + '\n');
					}
				}

				return s.toString();
			}
		}
	}

}
