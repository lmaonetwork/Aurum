package pro.delfik.proxy.user;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.ex.ExUserNotFound;
import pro.delfik.proxy.data.Database;
import pro.delfik.util.U;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BansIP {
	public static void unbanNickname(String player, String moderator) {
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			Bans.unban(player, moderator);
//			U.bc(server, "§7[§e" + moderator + "§7]§a Игрок §e" + player + " §aразбанен.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		clearNick(player);
	}
	public static void unbanIP(String address, String moderator) {
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§a IP-адрес §e" + address + " §aразбанен.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
		clearIP(address);
	}
	
	public static void clearNick(String nick) {
		Database.sendUpdate("DELETE FROM BanIP WHERE name = '" + nick + "'");
	}
	
	public static void clearIP(String ip) {
		Database.sendUpdate("DELETE FROM BanIP WHERE ip = '" + ip + "'");
	}
	
	public static void banPlayer(String player, String reason, String moderator) {
		ProxiedPlayer p = Proxy.getPlayer(player);
		if (p == null) throw new ExUserNotFound(player);
		final String ip = p.getAddress().getHostName();
		try {
			Bans.ban(player, reason, 0, moderator);
			U.bc(p.getServer(), "§7[§e" + moderator + "§7]§c Игрок §e" + player + " §cзабанен §eпо IP§c. Причина:§e " + reason);
			if (p != null) p.disconnect(kickMessage(player, ip, reason, moderator));
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри бане ирока §e" + player + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		Database.sendUpdate("INSERT INTO BanIP (name, ip, moderator, reason) " +
									"VALUES ('" + player + "', '" + ip + "', '" + moderator + "', '" + reason + "')");
	}
	public static void banIP(String ip, String reason, String moderator) {
		try {
			ProxiedPlayer moder = Proxy.getPlayer(moderator);
			Server server = moder == null ? null : moder.getServer();
			U.bc(server, "§7[§e" + moderator + "§7]§c IP-адрес §e" + ip + " §cбыл забанен. Причина:§e " + reason);
		} catch (Throwable t) {
			Proxy.i().broadcast("§c§lПри бане IP-адреса §e" + ip + "§c§l произошла ошибка.");
			t.printStackTrace();
		}
		Database.sendUpdate("INSERT INTO BanIP (ip, moderator, reason) " +
									"VALUES ('" + ip + "', '" + moderator + "', '" + reason + "')");
	}
	
	public static BanIPInfo getByName(String playername) {
		Database.Result r = null;
		try {
			r = Database.sendQuery("SELECT ip, moderator, reason FROM BanIP WHERE name = '" + playername + "'");
			ResultSet res = r.set;
			if (!res.next()) return null;
			return new BanIPInfo(playername, res.getString("ip"), res.getString("moderator"), res.getString("reason"));
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
	public static BanIPInfo getByAddress(String ip_address) {
		Database.Result r = null;
		try {
			r = Database.sendQuery("SELECT name, moderator, reason FROM BanIP WHERE ip = '" + ip_address + "'");
			ResultSet res = r.set;
			if (!res.next()) return null;
			return new BanIPInfo(res.getString("name"), ip_address, res.getString("moderator"), res.getString("reason"));
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
	
	public static class BanIPInfo {
		public final String player;
		public final String ip;
		public final String moderator;
		public final String reason;
		
		public BanIPInfo(String player, String ip, String moderator, String reason) {
			this.player = player;
			this.ip = ip;
			this.moderator = moderator;
			this.reason = reason;
		}
	}
	
	public static BaseComponent kickMessage(String player, String ip, String reason, String moderator) {
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
