package pro.delfik.proxy.cmd.user;

import implario.util.Rank;
import pro.delfik.proxy.User;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.util.TimedHashMap;

// ToDo: Вернуть к жизни привязку страниц к VK.
public class CmdVK extends Command {
	
	public CmdVK() {
		super("vk", Rank.PLAYER, "Привязка страницы ВК к игровому аккаунту");
	}
	
	@Override
	protected void run(User user, String[] args) {
		user.msg("§eВ разработке.");
//		if (args.length == 0 || args[0].length() == 0) {
//			user.msg("§6Привязка страницы VK к игровому аккаунту: §e/vk [sID]");
//			user.msg("§6sID - часть адреса страницы после \"§ehttps://vk.com/§6\".");
//			user.msg("§6Если ваш адрес - §ehttps://vk.com/id0§6, то ваш sID - §eid0");
//			return;
//		}
//		user.msg("§aПроверка страницы...");
//		Proxy.i().getScheduler().runAsync(Aurum.instance, () -> {
//			int id = VK.getUserID(args[0]);
//			if (id == -1) {
//				user.msg("§cСтраница §f" + args[0] + "§c не найдена. (" + VK.query("users.get", "user_ids=" + args[0]) + ")");
//				return;
//			}
//			PageAttachRequest request = new PageAttachRequest(user.getName(), id);
//			user.msg("§f");
//			user.msg("§aЗапрос на привязку страницы §fВКонтакте§a создан.");
//			user.msg("§aУ вас есть §f5 минут§a на его подтверждение.");
//			user.msg("§aДля подтверждения привязки вам необходимо отправить сообщение");
//			user.msg("§f§lconfirm " + request.getCode());
//			user.msg("§aВ ЛС нашей группы: §f", U.link("§nvk.com/lmaonetwork§f (Клик)", "https://vk.com/lmaonetwork"));
//			user.msg("§f");
//		});
	}
	
	public static int getPage(String playername) {
		return -1;
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
//			Database.sendUpdate("INSERT INTO VKPages (nickname, link) VALUES ('" + player + "', '" + page + "') ON DUPLICATE KEY UPDATE link = '" + page + "'");
//			byCode.remove(code);
//			ProxiedPlayer p = Proxy.getPlayer(player);
//			if (p != null) msg(p, "§aСтраница §fvk.com/id" + page + "§a успешно привязана к аккаунту.");
		}
	}
}
