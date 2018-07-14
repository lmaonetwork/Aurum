package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;

public class Mutes extends Command {
	
	public Mutes(boolean unmute) {
		super(unmute ? "unmute" : "mute", Rank.MODER, unmute ? "Разрешить игроку писать в чат" : "Запретить игроку писать в чат");
	}
	
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		if (getCommand().equalsIgnoreCase("unmute")) {
			requireArgs(args, 1, "[Игрок]");
			Mutes.MuteInfo i = get(args[0]);
			if (i == null) {
				msg(sender, "§eИгрок §f" + args[0] + "§e не замучен.");
				return;
			}
			unmute(args[0], sender.getName());
		} else {
			requireArgs(args, 3, "[Игрок] [Время] [Причина]");
			int time = requireInt(args[1]);
			mute(args[0], Converter.mergeArray(args, 2, " "), time, sender.getName());
		}
	}
	
	public static void unmute(String player, String moderator) {
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§a Игрок §e" + player + " §aснова может общаться с вами.");
			Person p = Person.get(player);
			if (p != null) {
				p.clearMute();
				p.msg("§aТы снова можешь писать в чат. Поблагодари §f" + moderator + "§a за размут.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		clear(player);
	}
	public static void clear(String nick) {
		try {
			Database.sendUpdate("DELETE FROM Mutes WHERE player = '" + nick + "'");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void mute(String player, String reason, int minutes, String moderator) {
		long start = System.currentTimeMillis();
		long until =  minutes != 0 ? start + (minutes * 60000) : 0;
		try {
			ProxiedPlayer p = Proxy.getPlayer(player);
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = p == null ? moder == null ? null : moder.getServer() : p.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§c Игроку §e" + player + " §cзапрещено разговаривать §eна" +
								 representTime(minutes * 60000) + "§c Причина:§e " + reason);
			Person person = Person.get(player);
			if (person != null) person.mute(new MuteInfo(player, moderator, System.currentTimeMillis() + minutes * 60000, reason));
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри муте ирока §e" + player + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		try {
			Database.sendUpdate("INSERT INTO Mutes (player, moderator, until, reason) " +
										"VALUES ('" + player + "', '" + moderator + "', " + until + ", '" + reason + "')" +
										"ON DUPLICATE KEY UPDATE moderator = '" + moderator + "', until = " + until + ", reason = '" + reason + "'");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Mutes.MuteInfo get(String playername) {
		Database.Result r = null;
		try {
			r = Database.sendQuery("SELECT * FROM Mutes WHERE player = '" + playername + "'");
			ResultSet res = r.set;
			if (!res.next()) return null;
			return new Mutes.MuteInfo(playername, res.getString("moderator"), res.getLong("until"), res.getString("reason"));
		} catch (SQLException e) {
			return null;
		} finally {
			try {
				if (r != null) r.st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	public static class MuteInfo {
		public final String player;
		public final String moderator;
		public final long until;
		public final String reason;
		
		public MuteInfo(String player, String moderator, long until, String reason) {
			this.player = player;
			this.moderator = moderator;
			this.until = until;
			this.reason = reason;
		}
		
		public void sendChatDisallowMessage(ProxiedPlayer p) {
			denyMessage(p, reason, until - System.currentTimeMillis(), moderator);
		}
	}
	
	public static void denyMessage(ProxiedPlayer p, String reason, long remainMillis, String moderator) {
		U.msg(p,"§eВы не можете писать в чат, так модератор §f" + moderator + "§e выдал вам мут.");
		U.msg(p,"§eНе нужно придумывать особо изощрённые методы пыток для него, просто повторите ",
				U.link("§e§nправила§e,", "http://lmaonetwork.ru/index.php?/topic/18-pravila-servera-i-foruma/"),
				"§e чтобы больше не совершать глупых ошибок.");
		String time = representTime(remainMillis);
		if (time.equals("")) U.msg(p, "§aЗаписи о вашем муте стираются из базы данных. Подождите секунду...");
		else U.msg(p,"§6Вам осталось молчать ещё" + time + "§6. Причина: §e" + reason);
	}
	
	private static String representTime(long millis) {
		if (millis == 0) return " вечность";
		
		int seconds = (int) (millis / 1000);
		int minutes = seconds / 60;
		int hours = minutes / 60;
		int days = hours / 24;
		hours %= 24;
		minutes %= 60;
		seconds %= 60;
		
		String result =
				(days == 0 ? "" : (" " + days + Converter.plural(days, " день", " дня", " дней"))) +
				(hours == 0 ? "" : (" " + hours + Converter.plural(hours, " час", " часа", " часов"))) +
				(minutes == 0 ? "" : (" " + minutes + Converter.plural(minutes, " минуту", " минуты", " минут"))) +
				(seconds == 0 ? "" : (" " + seconds + Converter.plural(seconds, " секунду", " секунды", " секунд")));
		return result.equals("") ? null : result;
	}
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return super.tabComplete(sender, arg, number);
		if (number == 1) return Arrays.asList("30", "60", "120", "240", "480", "1440", "2880");
		else return new HashSet<>();
	}
}
