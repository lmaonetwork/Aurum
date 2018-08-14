package pro.delfik.proxy.cmd.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.data.Database;
import pro.delfik.util.Rank;
import pro.delfik.util.TimedHashMap;
import pro.delfik.util.U;
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
		Proxy.i().getScheduler().runAsync(Aurum.instance, () -> {
			int id = VK.getUserID(args[0]);
			if (id == -1) {
				msg(sender, "§cСтраница §f" + args[0] + "§c не найдена. (" + VK.query("users.get", "user_ids=" + args[0]) + ")");
				return;
			}
			PageAttachRequest request = new PageAttachRequest(sender.getName(), id);
			msg(sender, "§f");
			msg(sender, "§aЗапрос на привязку страницы §fВКонтакте§a создан.");
			msg(sender, "§aУ вас есть §f5 минут§a на его подтверждение.");
			msg(sender, "§aДля подтверждения привязки вам необходимо отправить сообщение");
			msg(sender, "§f§lconfirm " + request.getCode());
			msg(sender, "§aВ ЛС нашей группы: §f", U.link("§nvk.com/lmaonetwork§f (Клик)", "https://vk.com/lmaonetwork"));
			msg(sender, "§f");
		});
	}
	
	public static int getPage(String playername) {
		Database.Result result = Database.sendQuery("SELECT link FROM VKPages WHERE nickname = '" + playername + "'");
		try {
			ResultSet set = result.set;
			if (!set.next()) return -1;
			return set.getInt("link");
		} catch (SQLException ex) {
			return -1;
		} finally {
			try {
				result.st.close();
			} catch (SQLException ignored) {}
		}
	}
	
	public static class PageAttachRequest {
		public static final TimedHashMap<Integer, PageAttachRequest> byCode = new TimedHashMap<>(300);
		
		private final String player;
		private final int page;
		private final int code;
		
		public PageAttachRequest(String player, int page) {
			this.player = player;
			this.page = page;
			code = (int) (Math.random() * 90000 + 10000);
			byCode.put(code, this);
		}
		
		public String getPlayer() {
			return player;
		}
		
		public int getCode() {
			return code;
		}
		
		public int getPageID() {
			return page;
		}
		
		public void confirm() {
			Database.sendUpdate("INSERT INTO VKPages (nickname, link) VALUES ('" + player + "', '" + page + "') ON DUPLICATE KEY UPDATE link = '" + page + "'");
			byCode.remove(code);
			ProxiedPlayer p = Proxy.getPlayer(player);
			if (p != null) msg(p, "§aСтраница §fvk.com/id" + page + "§a успешно привязана к аккаунту.");
		}
	}
}
