package pro.delfik.proxy.cmd.kurator;

import implario.net.packet.PacketGC;
import implario.util.Converter;
import implario.util.CryptoUtils;
import implario.util.Rank;
import implario.util.UserInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Title;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.User;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.CommandProcessor;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.cmd.ex.ExNotEnoughArguments;
import pro.delfik.proxy.cmd.user.CmdVK;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.modules.Ban;
import pro.delfik.proxy.modules.Chat;
import pro.delfik.proxy.modules.SfTop;
import pro.delfik.util.Logger;
import pro.delfik.util.U;
import pro.delfik.vk.LongPoll;
import pro.delfik.vk.VK;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static implario.util.StringUtils.random;

public class CmdAurum extends Command {
	public CmdAurum() {
		super("aurum", Rank.KURATOR, "Ты няшка ^^");
	}


	public static final HashMap<String, CommandProcessor> functions = new HashMap<>();

	static {
		functions.put("send", CmdAurum::send);
		functions.put("setrank", CmdAurum::setrank);
		functions.put("vimeban", CmdAurum::vimeban);
		functions.put("echo", CmdAurum::echo);
		functions.put("info", CmdAurum::info);
		functions.put("ping", CmdAurum::ping);
		functions.put("resetpassword", CmdAurum::resetPassword);
		functions.put("vk", CmdAurum::vk);
		functions.put("title", CmdAurum::title);
		functions.put("allowedips", CmdAurum::allowedips);
		functions.put("pageattachrequests", CmdAurum::pageAttachRequests);
		functions.put("vkupdate", CmdAurum::vkupdate);
		functions.put("sftop", CmdAurum::sftop);
		functions.put("serverlist", CmdAurum::serverlist);
		functions.put("gc", CmdAurum::gc);
		functions.put("memory", CmdAurum::memory);
		functions.put("mat", CmdAurum::mat);
		functions.put("ip", CmdAurum::ip);
		functions.put("readuser", CmdAurum::readuser);
		functions.put("detachip", CmdAurum::detachip);
		functions.put("ban", CmdAurum::ban);
		functions.put("rui", CmdAurum::readuserinfo);
		functions.put("wui", CmdAurum::writeuserinfo);
		functions.put("sudo", CmdAurum::sudo);
		functions.put("history", CmdAurum::history);
		functions.put("flush", CmdAurum::flush);
		functions.put("lp", CmdAurum::longpoll);
	}

	private static String longpoll(CommandSender commandSender, Command command, String[] strings) {
		requireArgs(strings, 2, "[Метод] [Аргументы]...");
		return VK.query(strings[0], Converter.mergeArray(strings, 1, "&"));
	}

	private static String flush(CommandSender sender, Command command, String[] args){
		requireRank(sender, Rank.ADMIN);
		Logger.flush();
		return "чото записалось";
	}

	private static String history(CommandSender sender, Command command, String[] args){
		requireRank(sender, Rank.ADMIN);
		requireArgs(args, 2, "[Игрок] [Тип]");
		List<String> lines = DataIO.read("log");
		args[0] = args[0].toLowerCase();
		args[1] = args[1].toLowerCase();
		for(String line : lines)
			if(line.toLowerCase().contains(args[0]) && line.contains(args[1]))sender.sendMessage(line);
		return "";
	}

	private static String sudo(CommandSender sender, Command command, String[] args) {
		requireRank(sender, Rank.CEKPET);
		requireArgs(args, 2, "[Игрок] [Сообщение]");
		User u = requirePerson(args[0]);
		String message = Converter.mergeArray(args, 1, " ");
		u.getHandle().chat(message);
		return null;
	}

	private static String readuserinfo(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 1, "[Ник]");
		long start = System.currentTimeMillis();
		UserInfo i = User.read(args[0]);
		msg(sender, "§aUserInfo§d" + UserInfo.Version.values()[i.version] + " §a-§e" + i.name + "§a-§e" + i.rank.getNameColor() + i.rank.getName().charAt(0));
		msg(sender, "§aPasswd: '§e" + i.passhash.substring(0, 20) + "§a...'");
		msg(sender, "§aIP: '§e" + i.lastIP + "§a'");
		msg(sender, "§aPMdisabled: §e" + i.pmDisabled + "§a; IPattached: §e" + i.ipAttached + "§a; DarkTheme: §e" + i.darkTheme);
		msg(sender, "§aIgnored: §e" + Converter.merge(i.ignored, s -> s, "§a.§e"));
		msg(sender, "§aFriends: §e" + Converter.merge(i.friends, s -> s, "§a.§e"));
		msg(sender, "§aMoney: §e" + i.money + "§a; Online: §e" + i.online);
		return "§dОперация успешно выполнена за §f" + (System.currentTimeMillis() - start) + "§ds.";
	}

	private static String writeuserinfo(CommandSender sender, Command cmd, String[] args) {
		requireArgs(args, 1, "[Ник]");
		Random r = new Random();
		UserInfo i = new UserInfo(args[0], CryptoUtils.getHash(random(10)), Rank.random(), Math.abs(r.nextInt()),
				"54.32.40.112", Math.abs(r.nextInt()), false, false, Converter.asList(random(8), random(8)),
				Converter.asList(random(8), random(8)), r.nextBoolean());
		User.save(i);
		msg(sender, "§7UserInfo§d" + UserInfo.Version.values()[i.version] + "§7-§e" + i.name + "§7-§e" + i.rank.getNameColor() + i.rank.getName().charAt(0));
		msg(sender, "§7Passwd: '§e" + i.passhash.substring(0, 20) + "§7...'");
		msg(sender, "§7IP: '§e" + i.lastIP + "§7'");
		msg(sender, "§7PMdisabled: §e" + i.pmDisabled + "§7; IPattached: §e" + i.ipAttached + "§7; DarkTheme: §e" + i.darkTheme);
		msg(sender, "§7Ignored: §e" + Converter.merge(i.ignored, s -> s, "§7.§e"));
		msg(sender, "§7Friends: §e" + Converter.merge(i.friends, s -> s, "§7.§e"));
		msg(sender, "§7Money: §e" + i.money + "§7; Online: §e" + i.online);
		return "§aЗапись прошла успешно.";
	}

	private static String ban(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 2, "[Модератор] [Игрок] (Время) (Причина)");
		int minutes = 0;
		String reason = "Не указана.";
		if (args.length > 2) {
			try {
				minutes = Integer.parseInt(args[2]);
				if (args.length > 3) {
					reason = Converter.mergeArray(args, 3, " ");
				}
			} catch (NumberFormatException ex) {
				reason = Converter.mergeArray(args, 2, " ");
			}
		}
		Ban.ban(args[1], reason, minutes, args[0]);
		return null;
	}

	private static String detachip(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		User p = User.loadOffline(args[0]);
		p.setIPBound(false);
		User.unload(args[0]);
		return "§aС игрока §f" + args[0] + "§a снята привязка IP-адреса.";
	}

	private static String readuser(CommandSender sender, Command command, String[] strings) {
		requireArgs(strings, 1, "[Ник]");
		User u = DataIO.readByteable(User.getPath(strings[0]) + "player.txt", User.class);
		String s = "§a[§f" + u.getName() + "§a: §f" + u.getRank() + "§a-§f" + u.getOnline() +
						   "§a]\n§a[pass=§f" + u.getPassword().substring(0, 30) +
						   "...§a]\n§a[ignore=§f" + Converter.merge(u.getIgnoredPlayers(), (st) -> st, "§a-§f") + "§a]";
		User.unload(strings[0]);
		return s;
	}

	private static String ip(CommandSender sender, Command command, String[] strings){
		return ((ProxiedPlayer)sender).getAddress().getHostName();
	}

	private static String mat(CommandSender sender, Command command, String[] strings){
		if(strings.length == 2){
			Chat.remMat(strings[0]);
			return "Удалён мат " + strings[0];
		}
		Chat.addMat(strings[0] + " ");
		return "Добавлен мат " + strings[0];
	}

	private static String serverlist(CommandSender sender, Command command, String[] strings) {
		return Converter.merge(Server.getServers(), Server::getServer, "§e, §f");
	}

	private static String sftop(CommandSender sender, Command command, String[] strings) {
		requireArgs(strings, 1, "[Игрок]");
		SfTop.checkTop(SfTop.getPerson(strings[0]));
		return "§aПроверка отправлена.";
	}

	private static String vkupdate(CommandSender sender, Command command, String[] strings) {
		try {
			LongPoll.requestLongPollServer();
			return "§aСервер LongPoll успешно обновлён.";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String allowedips(CommandSender sender, Command command, String[] strings) {
		if (strings.length == 1) return "§aВход для §f" + strings[0] + "§a разрешён. (§f" + User.allowedIP.add(strings[0].toLowerCase()) + "§a).";
		return "§e" + Converter.merge(User.allowedIP, s -> s, "§f, §e");
	}
	private static String pageAttachRequests(CommandSender sender, Command command, String[] strings) {
		return "§e" + Converter.merge(CmdVK.PageAttachRequest.byCode.values(),
				s -> s.getPlayer() + "-" + s.getPageID() + " (§7" + s.getCode() + "§e)", "§f, §e");
	}


	private static String title(CommandSender sender, Command command, String[] args) {
		Title title = new Title();
		title.setText(Converter.mergeArray(args, 0, " ").replace('&', '§'));
		title.setAction(Title.Action.TITLE);
		((ProxiedPlayer) sender).unsafe().sendPacket(title);
		return null;
	}


	private static String vk(CommandSender sender, Command command, String[] args) {
		if (args.length == 0) {
			msg(sender, "§aСервер LongPoll: §f" + LongPoll.getServer());
			msg(sender, "§aПоследний таймштамп: §f" + LongPoll.getTs());
			msg(sender, "§aТекущий ключ: §f" + LongPoll.getKey());
			msg(sender, "§aПоследний пир: §f" + LongPoll.lastPeer);
			return null;
		}
		String msg = Converter.mergeArray(args, 0, " ");
		LongPoll.msg(msg, LongPoll.lastPeer);
		return "§aСообщение успешно отправлено пиру §f" + LongPoll.lastPeer;
	}

	private static String resetPassword(CommandSender commandSender, Command command, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		User p = User.get(args[0]);
		if (p != null) p.setPassword("");
		else {
			UserInfo i = User.read(args[0]);
			if (i == null) return "§cИгрок §e" + args[0] + "§c не зарегистрирован.";
			i.passhash = "";
			User.save(i);
		}
		return "§aПароль игрока §e" + args[0] + "§a сброшен.";
	}

	private static String ping(CommandSender commandSender, Command command, String[] strings) {
		requireArgs(strings, 1, "[Сервер]");
		Proxy.ifServerOffline(requireServer(strings[0]), () -> msg(commandSender, "§cОффлайн."),
				ping -> msg(commandSender, "§aОнлайн: §e" + ping.toString()));
		return null;
	}

	private static String info(CommandSender commandSender, Command command, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		ProxiedPlayer p = requirePlayer(args[0]);
		msg(commandSender, "§aIP-адрес:§f " + p.getAddress().getAddress().getHostAddress());
		return "§aUUID: §f" + p.getUniqueId().toString();
	}

	private static String echo(CommandSender commandSender, Command command, String[] strings) {
		return U.color(Converter.mergeArray(strings, 0, " "));
	}

	private static String vimeban(CommandSender commandSender, Command command, String[] strings) {
		ProxiedPlayer p = ((ProxiedPlayer) commandSender);
		String r = Converter.mergeArray(strings, 1, " ");
		p.disconnect(new TextComponent("§7* * * * * * * * * * * * * * * * *\n§cВы были забанены\n\n§cПричина: §e" + r
		+ "\n§cВремя бана: §eнавсегда\n§cВас забанил: §e" + strings[0] + "\n§7* * * * * * * * * * * * * * * * *"));
		return null;
	}

	private static String send(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 2, "[Игрок|@Сервер|@a] [Сервер] (-e)");
		ServerInfo server = requireServer(args[1]);
		if (args[0].equals("@a")) {
			for (ProxiedPlayer p : Proxy.getPlayers()) {
				msg(p, "prefix", "§6Вы были телепортированы на сервер §e", server, "§6 игроком §e" + sender);
				p.connect(server);
			}
			msg(sender, "§aВсе онлайн-игроки отправлены на сервер ", server);
			return null;
		}
		if (args[0].startsWith("@")) {
			ServerInfo from = requireServer(args[0].substring(1));
			for (ProxiedPlayer p : from.getPlayers()) {
				if (p == sender && args.length > 2 && args[2].equals("-e")) continue;
				msg(p, "prefix", "§6Вы были телепортированы на сервер §e", server, "§6 игроком §e" + sender);
				p.connect(server);
			}
			msg(sender, "§aВсе игроки сервера ", from, "§a отправлены на сервер ", server);
			return null;
		}
		ProxiedPlayer target = requirePlayer(args[0]);
		target.connect(server);
		msg(target, "prefix", "§6Вы были телепортированы на сервер §e", server, "§6 игроком §e" + sender);
		msg(sender, "§aИгрок ", target, "§a был отправлен на сервер ", server);
		return null;
	}

	private static String setrank(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 2, "[Игрок] [Ранг]");
		Rank rank = requireRank(args[1]);
		if (sender instanceof ProxiedPlayer && !User.get(sender).hasRank(rank))
			return "§cВы не можете выдавать ранги выше собственного.";
		Logger.log("Perm", sender.getName() + " set " + rank + " " + args[0]);
		User u = User.get(args[0]);
		if (u != null) {
			u.setRank(rank);
			return "§aИгроку §f" + args[0] + "§a был выдан ранг §f" + rank.represent();
		}
		UserInfo info = User.read(args[0]);
		if (info == null) return "§cИгрок §e" + args[0] + "§c не зарегистрирован.";
		info.rank = rank;
		User.save(info);
		return "§aОффлайн-игроку §f" + args[0] + "§a был выдан ранг §f" + rank.represent();
	}

	private static String gc(CommandSender commandSender, Command command, String[] args){
		requireRank(commandSender, Rank.DEV);
		boolean rl = false;
		if(args.length == 2)rl = true;
		PacketGC gc = new PacketGC(rl);
		for(Server server : Server.getServers())
			server.send(gc);
		Runtime.getRuntime().gc();
		return "§aНа всех серверах запущена очистка мусора";
	}

	private static String memory(CommandSender commandSender, Command command, String[] args){
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) + " занятой оперативной памяти";
	}

	@Override
	protected void run(User user, String args[]) {
		if(args.length == 0)
			throw new ExCustom("§c/aurum [§f" + Converter.merge(functions.keySet(), s -> s, "§c, §f") + "§c]");
		String[] a = new String[args.length - 1];
		System.arraycopy(args, 1, a, 0, a.length);
		CommandProcessor function = functions.get(args[0].toLowerCase());
		if(function == null) throw new ExCustom("prefix" + "§cПодкомана §f/aurum " + args[0] + "§c не найдена.");
		else try{
			String os = function.process(user.getSender(), this, a);
			if(os != null && os.length() != 0) user.msg(os);
		}catch (ExNotEnoughArguments e){
			throw new ExNotEnoughArguments(args[0] + " " + e.getMessage());
		}
	}

	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return Converter.tabComplete(functions.keySet(), s -> s, arg);
		else return super.tabComplete(sender, arg, number);
	}
}
