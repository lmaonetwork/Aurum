package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.permissions.Rank;

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
		tryAttachPage(sender, args[0]);
	}
	
	private static void tryAttachPage(CommandSender sender, String arg) {
	
	}
	
	
	private static boolean completeAttachingPage(String player, String page) {
		try {
			Database.Result r = Database.sendQuery("SELECT link FROM VKPages WHERE name = '" + player + "' LIMIT 1");
			try {
				ResultSet set = r.set;
				if (!set.next()) return false;
				String existingPage = set.getString("link");
				if (page.equalsIgnoreCase(existingPage)) return false;
				else {
					Database.sendUpdate("INSERT INTO VKPages (name, link) VALUES ('" + player + "', '" + page + "') ON DUPLICATE KEY UPDATE link = '" + page + "'");
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
