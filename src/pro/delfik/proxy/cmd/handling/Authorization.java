package pro.delfik.proxy.cmd.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.data.PlayerDataManager;
import pro.delfik.proxy.user.User;
import pro.delfik.proxy.user.UserInfo;
import pro.delfik.util.Rank;
import pro.delfik.util.CryptoUtils;
import pro.delfik.util.TimedList;

import java.util.ArrayList;
import java.util.HashSet;

public class Authorization extends Command {
	
	public static final TimedList<String> allowedIPs = new TimedList<>(60);
	
	public Authorization(String name, String description, String... aliases) {
		super(name, Rank.PLAYER, description, aliases);
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		User u = User.get(sender);
		if (u.isAuthorized()) throw new ExCustom("§eТы уже авторизован.");
		if (getCommand().equals("login")){
			if(u.getPassword().equals(""))
				throw new ExCustom("§eДля первого входа в игру сперва нужно зарегистрироваться: /reg [Пароль]");
			requireArgs(args, 0, "[Пароль]");
			String input = args[0];
			String pass = u.getPassword();
			if(!(pass.equals(input) || pass.equals(CryptoUtils.getHash(input)))){
				u.kick("§cВведён неверный пароль.");
				return;
			}
			u.authorize();
			msg(sender, "§aАвторизация прошла успешно!");
		} else if (getCommand().equals("register")){
			requireArgs(args, 0, "[Пароль]");
			String input = args[0];
			String pass = u.getPassword();
			if(!pass.equals(""))
				throw new ExCustom("Вы уже зарегистрированы, пожалуйста, авторизуйтесь командой §e/login [Пароль]§c.");
			if(input.length() < 3) throw new ExCustom("§cДлина пароля не может быть меньше трёх символов.");
			u.setPassword(input);
			registerNewPlayer(sender.getName(), u.getPassword());

			u.authorize();
			msg(sender, "§aВы успешно зарегистрировались!");
		}
	}

	private static void registerNewPlayer(String name, String passhash) {
		PlayerDataManager.save(new UserInfo(name, passhash, 0, Rank.PLAYER, 0L, "", false, new ArrayList<>(), false, new ArrayList<>()));
	}

	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		return new HashSet<>();
	}
}
