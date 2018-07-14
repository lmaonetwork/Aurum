package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.Title;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.CommandProcessor;
import pro.delfik.proxy.command.NotEnoughArgumentsException;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.data.PlayerDataManager;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.ArrayUtils;
import pro.delfik.util.Converter;
import pro.delfik.util.U;
import pro.delfik.vk.LongPoll;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

public class CommandAurum extends Command {
	public CommandAurum() {
		super("aurum", Rank.KURATOR, "Ты няшка ^^");
	}
	
	
	public static final HashMap<String, CommandProcessor> functions = new HashMap<>();
	
	static {
		functions.put("send", CommandAurum::send);
		functions.put("setrank", CommandAurum::setrank);
		functions.put("sqlquery", CommandAurum::sqlquery);
		functions.put("sqlupdate", CommandAurum::sqlupdate);
		functions.put("vimeban", CommandAurum::vimeban);
		functions.put("echo", CommandAurum::echo);
		functions.put("advban", CommandAurum::advban);
		functions.put("info", CommandAurum::info);
		functions.put("ping", CommandAurum::ping);
		functions.put("resetpassword", CommandAurum::resetPassword);
		functions.put("vk", CommandAurum::vk);
		functions.put("title", CommandAurum::title);
	}
	
	private static Object[] title(CommandSender sender, Command command, String[] args) {
		Title title = new Title();
		title.setText(Converter.mergeArray(args, 0, " ").replace('&', '§'));
		title.setAction(Title.Action.TITLE);
		((ProxiedPlayer) sender).unsafe().sendPacket(title);
		return new Object[0];
	}
	
	
	private static Object[] vk(CommandSender sender, Command command, String[] args) {
		if (args.length == 0) {
			msg(sender, "§aСервер LongPoll: §f" + LongPoll.getServer());
			msg(sender, "§aПоследний таймштамп: §f" + LongPoll.getTs());
			msg(sender, "§aТекущий ключ: §f" + LongPoll.getKey());
			msg(sender, "§aПоследний пир: §f" + LongPoll.lastPeer);
			return new Object[0];
		}
		String msg = Converter.mergeArray(args, 0, " ");
		LongPoll.msg(msg, LongPoll.lastPeer);
		return new Object[] {"§aСообщение успешно отправлено пиру §f" + LongPoll.lastPeer};
	}
	
	private static Object[] resetPassword(CommandSender commandSender, Command command, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		Person p = Person.get(args[0]);
		if (p != null) p.setPassword("");
		else {
			int i;
			i = Database.sendUpdate("UPDATE Users SET passhash = '' WHERE name = '" + args[0] + "'");
			if (i == 0) return new Object[] {"prefix", "§eУ игрока §f" + args[0] + "§e итак нет пароля."};
		}
		return new Object[] {"prefix", "§aПароль игрока §e", p == null ? args[0] : p, "§a сброшен."};
	}
	
	private static Object[] ping(CommandSender commandSender, Command command, String[] strings) {
		requireArgs(strings, 1, "[Сервер]");
		Proxy.ifServerOffline(requireServer(strings[0]), () -> {
					msg(commandSender, "§cОффлайн.");},
				(ping) -> {
					msg(commandSender, "§aОнлайн: §e" + ping.toString());});
		return new Object[0];
	}
	
	private static Object[] info(CommandSender commandSender, Command command, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		ProxiedPlayer p = requirePlayer(args[0]);
		return new Object[] {"§aUUID: §f" + p.getUniqueId().toString()};
	}
	
	private static Object[] advban(CommandSender commandSender, Command command, String[] args) {
		requireArgs(args, 2, "[Игрок] [Причина]");
		ProxiedPlayer p = requirePlayer(args[0]);
		String uuid = p.getUniqueId().toString();
		Bans.advban(args[0], uuid, Converter.mergeArray(args, 1, " "));
		return new Object[0];
	}
	
	private static Object[] echo(CommandSender commandSender, Command command, String[] strings) {
		return new Object[] {U.color(Converter.mergeArray(strings, 0, " "))};
	}
	
	private static Object[] vimeban(CommandSender commandSender, Command command, String[] strings) {
		ProxiedPlayer p = ((ProxiedPlayer) commandSender);
		String r = Converter.mergeArray(strings, 1, " ");
		p.disconnect(new TextComponent("§7* * * * * * * * * * * * * * * * *\n§cВы были забанены\n\n§cПричина: §e" + r
		+ "\n§cВремя бана: §eнавсегда\n§cВас забанил: §e" + strings[0] + "\n§7* * * * * * * * * * * * * * * * *"));
		return new Object[0];
	}
	
	private static Object[] send(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 2, "[Игрок] [Сервер]");
		ProxiedPlayer target = requirePlayer(args[0]);
		ServerInfo server = requireServer(args[1]);
		target.connect(server);
		msg(target, "prefix", "§6Вы были телепортированы на сервер §e", server, "§6 игроком §e" + sender);
		return new Object[]{"prefix", "§aИгрок ", target, "§a отправлен на сервер ", server};
	}
	
	private static Object[] setrank(CommandSender sender, Command command, String[] args) {
		requireArgs(args, 2, "[Игрок] [Ранг]");
		Rank rank = requireRank(args[1]);
		if (sender instanceof ProxiedPlayer && !Person.get(sender).hasRank(rank))
			return new Object[] {"prefix", "§cВы не можете выдавать ранги выше собственного."};
		PlayerDataManager.setRank(args[0], rank);
		return new Object[] {"prefix", "§aИгроку §f" + args[0] + "§a был выдан ранг §f" + rank.represent()};
	}
	
	private static Object[] sqlquery(CommandSender sender, Command command, String[] args) {
		try {
			requireRank(sender, Rank.ADMIN);
			Database.Result res = Database.sendQuery(ArrayUtils.toString(args));
			ResultSet result = res.set;
			ResultSetMetaData metadata = result.getMetaData();
			int columnCount = metadata.getColumnCount();
			StringBuilder stringBuilder = new StringBuilder("§fКолонки: §e");
			for (int i = 1; i <= columnCount; i++) stringBuilder.append(metadata.getColumnName(i)).append("§f, §e");
			msg(sender, stringBuilder.toString());
			int r = 0;
			while (result.next()) {
				StringBuilder row = new StringBuilder("§e").append(r).append(". §a");
				r++;
				for (int i = 1; i <= columnCount; i++) {
					row.append(result.getString(i)).append("§f, §a");
				}
				msg(sender, row.toString());
			}
			res.st.close();
			return new Object[]{"prefix", "§aЗапрос к базе данных успешно отправлен."};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static Object[] sqlupdate(CommandSender commandSender, Command command, String[] args) {
		requireRank(commandSender, Rank.ADMIN);
		return new Object[] {"prefix", "§aОбновлено §e" + Database.sendUpdate(ArrayUtils.toString(args)) + "§a записей."};
	}
	
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		if (args.length == 0) msg(sender, "§c/aurum [", Converter.merge(functions.keySet(), s -> s, "§c, §f"), "§c]");
		else {
			String[] a = new String[args.length - 1];
			System.arraycopy(args, 1, a, 0, a.length);
			CommandProcessor function = functions.get(args[0].toLowerCase());
			if (function == null) msg(sender, "prefix", "§cПодкомана §f/aurum " + args[0] + "§c не найдена.");
			else try {
				Object[] os = function.process(sender, this, a);
				if (os != null && os.length != 0) msg(sender, os);
			} catch (NotEnoughArgumentsException e) {
				throw new NotEnoughArgumentsException(args[0] + " " + e.getMessage());
			}
		}
	}
	
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return Converter.tabComplete(functions.keySet(), s -> s, arg);
		else return super.tabComplete(sender, arg, number);
	}
}
