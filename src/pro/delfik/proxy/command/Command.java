package pro.delfik.proxy.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.ConsoleCommandSender;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.ex.ExCustom;
import pro.delfik.proxy.command.ex.ExNotEnoughArguments;
import pro.delfik.proxy.command.ex.ExNotEnoughPermissions;
import pro.delfik.proxy.command.ex.ExUserNotFound;
import pro.delfik.proxy.command.ex.ExServerNotFound;
import pro.delfik.proxy.command.handling.Authorization;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public abstract class Command extends net.md_5.bungee.api.plugin.Command implements TabExecutor {
	private final String line;
	private final String description;
	private final Rank required;
	
	public Command(String name, Rank required, String description, String... aliases) {
		super(name, null, aliases);
		this.line = name;
		this.required = required;
		this.description = description;
		Proxy.registerCommand(this);
	}
	
	@Override
	public final void execute(CommandSender sender, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			run(sender, args);
			return;
		}
		User user = User.get(sender);
		if (!(this instanceof Authorization)) {
			if (!user.isAuthorized()) {
				U.msg(sender, "§eДля использования команд вам необходимо авторизоваться.");
				U.msg(sender, U.run("§eНе знаете, что это такое? §nНажмите сюда§e.", "§e> §fГайд по авторизации §e<", "/guide a"));
				return;
			}
			if (!user.hasRank(getRequiredRank())) {
				U.msg(sender, "§cКоманда §e/" + getCommand() + "§c доступна со статуса §e" + getRequiredRank().represent());
				return;
			}
		}
		try {
			run(sender, args);
		} catch (ClassCastException ex) {
			U.msg(sender, "§cGo away evil console :c");
		} catch (Throwable t) {
			Throwable cause = t;
			while (cause.getCause() != null) cause = cause.getCause();
			if (cause instanceof ExCustom) {
				((ExCustom)cause).execute(sender, line);
				return;
			}
			if (cause instanceof NumberFormatException) {
				U.msg(sender, "§с'§f" + cause.getMessage() + "§c' не является допустимым числом.");
				return;
			}
			t.printStackTrace();
			U.msg(sender, "§cПри выполнении команды произошла неизвестная ошибка.");
			U.msg(sender, "§cПожалуйтса, отправьте скрин сообщения администрации:");
			U.msg(sender, "§cCommand: §e/" + getCommand() + " " + String.join(" ", args));
			U.msg(sender,"§cException: §e" + cause.getClass().getName());
			U.msg(sender,"§cTimestamp: §e" + new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy").format(new Date()));
			U.msg(sender,"§cLine: §e" + cause.getStackTrace()[0].getClassName() + " - line " + cause.getStackTrace()[0].getLineNumber());
		}
	}


	protected abstract void run(CommandSender sender, String[] args);
	
	protected String getCommand() {
		return line;
	}
	
	public Rank getRequiredRank() {
		return required == null ? Rank.PLAYER : required;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static void msg(CommandSender sender, Object... o) {
		U.msg(sender, o);
	}
	
	public static void requireArgs(String[] args, int required, String usage) {
		if (args.length < required) throw new ExNotEnoughArguments(usage);
	}

	public static void requireRank(CommandSender sender, Rank rank) {
		if (!User.get(sender).hasRank(rank)) throw new ExNotEnoughPermissions(rank);
	}

	public static User requirePerson(String arg) {
		User u = User.get(arg);
		if (u == null) throw new ExUserNotFound(arg); else return u;
	}

	public static ProxiedPlayer requirePlayer(String arg) {
		ProxiedPlayer p = Proxy.getPlayer(arg);
		if (p == null) throw new ExUserNotFound(arg); else return p;
	}
	
	public static ServerInfo requireServer(String arg) {
		ServerInfo server = Proxy.getServer(arg);
		if (server == null) throw new ExServerNotFound(arg); else return server;
	}
	
	public static Rank requireRank(String arg) {
		return Rank.decode(arg.toUpperCase());
	}
	
	public static int requireInt(String arg) {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException e) {
			throw new NumberFormatException(arg);
		}
	}
	
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (sender instanceof ProxiedPlayer)
			return Converter.tabComplete(((ProxiedPlayer) sender).getServer().getInfo().getPlayers(), ProxiedPlayer::getName, arg);
		else
			return Converter.tabComplete(User.getAll(), User::getName, arg);
	}
	
	@Override
	public final Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
		int argnumber = strings.length - 1;
		if (argnumber < 0) return new HashSet<>();
		String currentArg = strings[argnumber];
		return tabComplete(commandSender, currentArg, argnumber);
	}
}
