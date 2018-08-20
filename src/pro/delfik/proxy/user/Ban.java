package pro.delfik.proxy.user;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.util.ByteUnzip;
import pro.delfik.util.ByteZip;
import pro.delfik.util.Byteable;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ban implements Byteable {

	public final String reason, moderator;
	public final long start, until;

	protected Ban(String reason, String moderator, long start, long until) {
		this.reason = reason;
		this.moderator = moderator;
		this.start = start;
		this.until = until;
	}
	public Ban(ByteUnzip unzip) {
		reason = unzip.getString();
		moderator = unzip.getString();
		start = unzip.getLong();
		until = unzip.getLong();
	}

	public static void unban(String player, String moderator) {
		DataIO.remove(User.getPath(player) + "ban.txt");
		if (moderator == null) return;
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§a Игрок §e" + player + " §aразбанен.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public static void ban(String player, String reason, int minutes, String moderator) {
		long start = System.currentTimeMillis();
		long until = minutes != 0 ? start + (minutes * 60000) : 0;
		if (reason == null) reason = "Не указана.";
		Ban ban = new Ban(reason, moderator, start, until);
		try {
			ProxiedPlayer p = Proxy.getPlayer(player);
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = p == null ? moder == null ? null : moder.getServer() : p.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§c Игрок §e" + player + " §cзабанен §eна" +
					representTime(minutes) + "§c Причина:§e " + reason);
			if (p != null) p.disconnect(ban.kickMessage(player));
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри бане ирока §e" + player + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		DataIO.writeByteable(User.getPath(player) + "ban", ban);
	}
	
	public static Ban get(String playername) {
		return DataIO.readByteable(User.getPath(playername) + "ban", Ban.class);
	}

	@Override
	public ByteZip zip() {
		return new ByteZip().add(reason).add(moderator).add(start).add(until);
	}

	public BaseComponent kickMessage(String player) {
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
