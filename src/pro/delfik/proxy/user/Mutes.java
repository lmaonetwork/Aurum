package pro.delfik.proxy.user;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Database;
import pro.delfik.util.ByteUnzip;
import pro.delfik.util.ByteZip;
import pro.delfik.util.Byteable;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Mutes {
	public static final String path = "/mute";

	public static void unmute(String player, String moderator) {
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§a Игрок §e" + player + " §aснова может общаться с вами.");
			User user = User.get(player);
			if (user != null) {
				user.clearMute();
				user.msg("§aТы снова можешь писать в чат. Поблагодари §f" + moderator + "§a за размут.");
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		clear(player);
	}

	public static void clear(String nick) {
		Database.sendUpdate("DELETE FROM Mutes WHERE player = '" + nick + "'");
		DataIO.remove(nick + path);
	}
	
	public static void mute(String player, String reason, int minutes, String moderator) {
		MuteInfo mute = new MuteInfo(player, moderator, System.currentTimeMillis() + minutes * 60000, reason);
		try {
			ProxiedPlayer p = Proxy.getPlayer(player);
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = p == null ? moder == null ? null : moder.getServer() : p.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§6 Игроку §e" + player + " §6запрещено разговаривать §eна" +
								 representTime(minutes * 60000) + "§6 Причина:§e " + reason);
			User user = User.get(player);
			if (user != null) user.mute(mute);
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри муте ирока §e" + player + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		DataIO.writeBytes(player + path, mute.zip().build());
	}

	public static boolean muted(String nick){
		return DataIO.contains(nick + path);
	}
	
	public static Mutes.MuteInfo get(String playername) {
		byte mute[] = DataIO.readBytes(playername + path);
		if(mute != null)return new MuteInfo(new ByteUnzip(mute));
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
	
	public static class MuteInfo implements Byteable{
		public final String player;
		public final String moderator;
		public final long until;
		public final String reason;

		public MuteInfo(ByteUnzip unzip){
			this.player = unzip.getString();
			this.moderator = unzip.getString();
			this.until = unzip.getLong();
			this.reason = unzip.getString();
		}
		
		public MuteInfo(String player, String moderator, long until, String reason) {
			this.player = player;
			this.moderator = moderator;
			this.until = until;
			this.reason = reason;
		}
		
		public void sendChatDisallowMessage(ProxiedPlayer p) {
			denyMessage(p, reason, until - System.currentTimeMillis(), moderator);
		}

		@Override
		public ByteZip zip() {
			return new ByteZip().add(player).add(moderator).add(until).add(reason);
		}
	}
	
	public static void denyMessage(ProxiedPlayer p, String reason, long remainMillis, String moderator) {
		U.msg(p,"§eВы не можете писать в чат, так как модератор §f" + moderator + "§e выдал вам мут.");
		U.msg(p,"§eНе нужно придумывать особо изощрённые методы пыток для него, просто повторите ",
				U.link("§e§nправила§e,", "http://lmaonetwork.ru/index.php?/topic/18-pravila-servera-i-foruma/"),
				"§e чтобы больше не совершать глупых ошибок.");
		String time = representTime(remainMillis);
		if (time == null) U.msg(p, "§aЗаписи о вашем муте стираются из базы данных. Подождите секунду...");
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
}
