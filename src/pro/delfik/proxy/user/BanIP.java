package pro.delfik.proxy.user;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.util.ByteUnzip;
import pro.delfik.util.ByteZip;
import pro.delfik.util.Byteable;
import pro.delfik.util.U;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BanIP implements Byteable {

	private final String moderator, reason;

	public BanIP(String reason, String moderator) {
		this.reason = reason;
		this.moderator = moderator;
	}
	public BanIP(ByteUnzip unzip) {
		reason = unzip.getString();
		moderator = unzip.getString();
	}
	@Override
	public ByteZip zip() {
		return new ByteZip().add(reason).add(moderator);
	}

	public static void unbanIP(String address, String moderator) {
		DataIO.remove("bans-ip/" + address + ".txt");
		if (moderator == null) return;
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§a IP-адрес §e" + address + " §aразбанен.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	

	public static BanIP banIP(String ip, String reason, String moderator) {
		BanIP ban = new BanIP(reason, moderator);
		DataIO.writeByteable("bans-ip/" + ip + ".txt", ban);
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§c IP-адрес §e" + ip + " §cбыл забанен. Причина:§e " + reason);
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри бане IP-адреса §e" + ip + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		return ban;
	}
	
	public static BanIP getByName(String playername) {
		User u = User.get(playername);
		if (u == null) u = User.load(playername);
		return get(u.getLastIP());
	}

	public static BanIP get(String ip) {
		return DataIO.readByteable("bans-ip/" + ip + ".txt", BanIP.class);
	}


	public BaseComponent kickMessage(String ip) {
		return U.constructComponent(
				"§7* * * * * * * * * * * * * * * * * * *\n",
				"§cК сожалению, IP-адрес §e" + ip + "§c забанен.\n",
				"§cВас забанил: §e" + moderator + '\n',
				"§cПричина: §e" + reason + '\n',
				"§cЕсли вы не согласны с выданным баном или не знаете, почему забанены,\n" +
						"§cСмело подавайте жалобу на ", U.link("§c§nфорум (клик)§c.", "http://lmaonetwork.ru"),
				"\n§7* * * * * * * * * * * * * * * * * * *"
		);
	}
	
	private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
	
	public static String readableTime(long time) {
		return format.format(new Date(time));
	}
}
