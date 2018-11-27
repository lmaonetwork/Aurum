package pro.delfik.proxy.module;

import implario.util.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Logger;
import pro.delfik.util.U;

import java.util.Date;

public class Mute implements Byteable {
	private static final String path = "/mute";

	public static void unmute(String player, String moderator) {
		ProxiedPlayer moder = Proxy.getPlayer(moderator);
		Server server = moder == null ? null : moder.getServer();
		U.bc(server, "§7[§e" + moderator + "§7]§a Игрок §e" + player + " §aснова может общаться с вами.");
		User user = User.get(player);
		if (user != null) user.clearMute(moderator);
		Logger.log("Unmute", moderator + " unmuted " + player + " time " + new Date().toString());
		clear(player);
	}

	public static void clear(String nick) {
		DataIO.remove(getPath(nick));
	}
	
	public static void mute(String player, String reason, int minutes, String moderator) {
		Mute mute = new Mute(moderator, System.currentTimeMillis() + minutes * 60000, reason);
		Logger.log("Mute", moderator + " mute " + player + " time " + new Date().toString() +
				" reason: \"" + reason + "\" minutes " + minutes);
		ProxiedPlayer p = Proxy.getPlayer(player);
		Server server = p == null ? null : p.getServer();
		U.bc(server, "§7[§e" + moderator + "§7]§6 Игроку §e" + player + " §6запрещено разговаривать §eна" +
				representTime(minutes * 60000) + "§6 Причина:§e " + reason);
		User user = User.get(player);
		if (user != null) user.mute(mute);
		else mute.write(player);
	}

	public static boolean muted(String nick){
		User user = User.get(nick);
		if(user != null)return user.getActiveMute() != null && user.getActiveMute().getUntil() > System.currentTimeMillis();
		return DataIO.contains(getPath(nick));
	}
	
	public static Mute get(String nick) {
		return DataIO.readByteable(getPath(nick), Mute.class);
	}

	private final String moderator;
	private final long until;
	private final String reason;

	public Mute(ByteUnzip unzip){
		this.moderator = unzip.getString();
		this.until = unzip.getLong();
		this.reason = unzip.getString();
	}

	private Mute(String moderator, long until, String reason) {
		this.moderator = moderator;
		this.until = until;
		this.reason = reason;
	}

	public String getModerator() {
		return moderator;
	}

	public long getUntil() {
		return until;
	}

	public String getReason() {
		return reason;
	}

	public void sendChatDisallowMessage(ProxiedPlayer p) {
		denyMessage(p, reason, until - System.currentTimeMillis(), moderator);
	}

	public void write(String player){
		DataIO.writeByteable(getPath(player), this);
	}

	@Override
	public ByteZip toByteZip() {
		return new ByteZip().add(moderator).add(until).add(reason);
	}
	
	private static void denyMessage(ProxiedPlayer p, String reason, long remainMillis, String moderator) {
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

	private static String getPath(String nick){
		return User.getPath(nick) + path;
	}
}
