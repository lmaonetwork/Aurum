package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.AurumPlugin;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.data.Database;
import pro.delfik.util.Rank;
import pro.delfik.vk.LongPoll;
import pro.delfik.vk.VK;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandVK extends Command {
	
	public CommandVK() {
		super("vk", Rank.PLAYER, "Привязка страницы ВК к игровому аккаунту");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		if (args.length == 0 || args[0].length() == 0) {
			msg(sender, "§6Привязка страницы VK к игровому аккаунту: §e/vk [sID]");
			msg(sender, "§6sID - часть адреса страницы после \"§ehttps://vk.com/§6\".");
			msg(sender, "§6Если ваш адрес - §ehttps://vk.com/id0§6, то ваш sID - §eid0");
			return;
		}
		msg(sender, "§aПроверка страницы...");
		Proxy.i().getScheduler().runAsync(AurumPlugin.instance, () -> {
			int id = VK.getUserID(args[0]);
			if (id == -1) {
				msg(sender, "§cСтраница §f" + args[0] + "§c не найдена.");
				return;
			}
			completeAttachingPage(sender.getName(), id);
		});
	}
	
	private static void tryAttachPage(CommandSender sender, String arg) {
	
	}
	
	
	private static boolean completeAttachingPage(String player, int id) {
		try {
			Database.Result r = Database.sendQuery("SELECT link FROM VKPages WHERE name = '" + player + "' LIMIT 1");
			try {
				ResultSet set = r.set;
				if (!set.next()) return false;
				int existingPage = set.getInt("link");
				if (id == existingPage) return false;
				else {
					Database.sendUpdate("INSERT INTO VKPages (name, link) VALUES ('" + player + "', '" + id + "') ON DUPLICATE KEY UPDATE link = '" + id + "'");
					return true;
				}
			} finally {
				r.st.close();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
