package pro.delfik.proxy.user;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.Database;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bans {
	public static void unban(String player, String moderator) {
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§a Игрок §e" + player + " §aразбанен.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		clear(player);
	}

	public static void clear(String nick) {
		Database.sendUpdate("DELETE FROM Bans WHERE name = '" + nick + "'");
	}
	
	public static void ban(String player, String reason, int minutes, String moderator) {
		long start = System.currentTimeMillis();
		long until = minutes != 0 ? start + (minutes * 60000) : 0;
		try {
			ProxiedPlayer p = Proxy.getPlayer(player);
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = p == null ? moder == null ? null : moder.getServer() : p.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§c Игрок §e" + player + " §cзабанен §eна" +
					representTime(minutes) + "§c Причина:§e " + reason);
			if (p != null) p.disconnect(kickMessage(player, reason, start, until, moderator));
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри бане ирока §e" + player + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		Database.sendUpdate("INSERT INTO Bans (name, moderator, time, until, reason) " +
				"VALUES ('" + player + "', '" + moderator + "', " + start + ", " + until + ", '" + reason + "')" +
				"ON DUPLICATE KEY UPDATE moderator = '" + moderator + "', time = " + start + ", until = " + until + ", reason = '" + reason + "'");
	}
	
	public static BanInfo get(String playername) {
		Database.Result r = null;
		try {
			r = Database.sendQuery("SELECT * FROM Bans WHERE name = '" + playername + "'");
			ResultSet res = r.set;
			if (!res.next()) return null;
			return new BanInfo(playername, res.getString("moderator"), res.getLong("time"),
					res.getLong("until"), res.getString("reason"));
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

	public static class BanInfo {
		public final String player;
		public final String moderator;
		public final long time;
		public final long until;
		public final String reason;
		
		public BanInfo(String player, String moderator, long time, long until, String reason) {
			this.player = player;
			this.moderator = moderator;
			this.time = time;
			this.until = until;
			this.reason = reason;
		}
	}
	
	public static BaseComponent kickMessage(String player, String reason, long start, long until, String moderator) {
		return U.constructComponent(
				"§7* * * * * * * * * * * * * * * * * * *\n",
				"§cК сожалению, аккаунт §e" + player + "§c забанен.\n",
				"§cВас забанил: §e" + moderator + '\n',
				"§cПричина: §e" + reason + '\n',
				"§cДата блокировки: §e" + readableTime(start) + '\n',
				until == 0 ? "§cВы заблокированы §eнавсегда§c. Ваши друзья наверняка сожалеют об этом.\n" :
						"§cДата снятия блокировки: §e" + readableTime(until) + '\n',
				"§aЕсли вы не согласны с выданным баном или не знаете, почему забанены,\n" +
						"§aСмело подавайте жалобу на ", U.link("§a§nфорум (клик)§a.", "http://lmaonetwork.ru"),
				"\n§7* * * * * * * * * * * * * * * * * * *"
		);
	}
	
	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
	public static String readableTime(long time) {
		return format.format(new Date(time));
	}

	private static String representTime(int minutes) {
		if (minutes == 0) return "всегда";
		int days = minutes / 1440;
		int hours = (minutes % 1440) / 60;
		minutes %= 60;
		return (days == 0 ? "" : (" " + days + Converter.plural(days, " день", " дня", " дней"))) +
				(hours == 0 ? "" : (" " + hours + Converter.plural(hours, " час", " часа", " часов"))) +
				(minutes == 0 ? "" : (" " + minutes + Converter.plural(minutes, " минуту", " минуты", " минут")));
	}
}
