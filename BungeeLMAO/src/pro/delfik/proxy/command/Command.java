package pro.delfik.proxy.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.ConsoleCommandSender;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.handling.Authorization;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;
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
	public final void execute(CommandSender commandSender, String[] strings) {
		if (commandSender instanceof ConsoleCommandSender) {
			run(commandSender, strings);
			return;
		}
		Person user = Person.get(commandSender);
		if (!(this instanceof Authorization)) {
			if (!user.isAuthorized()) {
				U.msg(commandSender, "§eДля использования команд вам необходимо авторизоваться.");
				U.msg(commandSender, U.run("§eНе знаете, что это такое? §nНажмите сюда§e.", "§e> §fГайд по авторизации §e<", "guide a"));
				return;
			}
			if (!user.hasRank(getRequiredRank())) {
				U.msg(commandSender, "§cКоманда §e/" + getCommand() + "§c доступна со статуса §e" + getRequiredRank().represent());
				return;
			}
		}
		try {
			run(commandSender, strings);
		} catch (ClassCastException ex) {
			U.msg(commandSender, "§cGo away evil console :c");
		} catch (Throwable t) {
			Throwable cause = t;
			while (cause.getCause() != null) cause = cause.getCause();
			if (cause instanceof NotEnoughArgumentsException) {
				U.msg(commandSender, "§cНедостаточно аргументов.");
				U.msg(commandSender, "§cИспользование: §e/" + getCommand() + ' ' + ((NotEnoughArgumentsException) cause).getUsage());
				return;
			}
			if (cause instanceof NotEnoughPermissionsException) {
				U.msg(commandSender, "§cДля этого действия необходим статус §e" + ((NotEnoughPermissionsException) cause).getRequiredRank().represent());
				return;
			}
			if (cause instanceof PersonNotFoundException) {
				U.msg(commandSender, "§cИгрок с ником §f" + ((PersonNotFoundException) cause).getPersonname() + "§c не найден.");
				return;
			}
			if (cause instanceof ServerNotFoundException) {
				U.msg(commandSender, "§сСервер с названием §f" + ((ServerNotFoundException) cause).getServer() + "§c не найден.");
				return;
			}
			if (cause instanceof NumberFormatException) {
				U.msg(commandSender, "§с'§f" + cause.getMessage() + "§c' не является допустимым числом.");
				return;
			}
			t.printStackTrace();
			U.msg(commandSender,"§cПри выполнении команды произошла неизвестная ошибка.");
			U.msg(commandSender,"§cПожалуйтса, отправьте скрин сообщения администрации:");
			U.msg(commandSender,"§cCommand: §e/" + getCommand() + " " + String.join(" ", strings));
			U.msg(commandSender,"§cException: §e" + cause.getClass().getName());
			U.msg(commandSender,"§cTimestamp: §e" + new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy").format(new Date()));
			U.msg(commandSender,"§cLine: §e" + cause.getStackTrace()[0].getClassName() + " - line " + cause.getStackTrace()[0].getLineNumber());
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
		if (args.length < required) throw new NotEnoughArgumentsException(usage);
	}
	public static void requireRank(CommandSender sender, Rank rank) {
		if (!Person.get(sender).hasRank(rank)) throw new NotEnoughPermissionsException(rank);
	}
	public static Person requirePerson(String arg) {
		Person u = Person.get(arg);
		if (u == null) throw new PersonNotFoundException(arg); else return u;
	}
	public static ProxiedPlayer requirePlayer(String arg) {
		ProxiedPlayer p = Proxy.getPlayer(arg);
		if (p == null) throw new PersonNotFoundException(arg); else return p;
	}
	
	public static ServerInfo requireServer(String arg) {
		ServerInfo server = Proxy.getServer(arg);
		if (server == null) throw new ServerNotFoundException(arg); else return server;
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
			return Converter.tabComplete(Person.getAll(), Person::getName, arg);
	}
	
	@Override
	public final Iterable<String> onTabComplete(CommandSender commandSender, String[] strings) {
		int argnumber = strings.length - 1;
		if (argnumber < 0) return new HashSet<>();
		String currentArg = strings[argnumber];
		return tabComplete(commandSender, currentArg, argnumber);
	}
}
