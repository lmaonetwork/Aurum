package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.CustomException;
import pro.delfik.proxy.data.PlayerDataManager;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.PersonInfo;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.CryptoUtils;

import java.sql.SQLException;
import java.util.HashSet;

public class Authorization extends Command {
	
	public Authorization(String name, String description, String... aliases) {
		super(name, null, description, aliases);
	}
	
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		Person u = Person.get(sender);
		if (u.isAuthorized()) {
			msg(sender, "§eТы уже авторизован.");
		} else if (getCommand().equals("login")) {
			if (u.getPassword().equals("")) {
				msg(sender, "§eДля первого входа в игру сперва нужно зарегистрироваться: /reg [Пароль]");
			} else {
				requireArgs(args, 0, "[Пароль]");
				String input = args[0];
				auth: {
					String pass = u.getPassword();
					if (pass.equals(input)) break auth;
					if (pass.equals(CryptoUtils.getHash(input))) break auth;
					u.kick("§cВведён неверный пароль.");
					return;
				}
				u.authorize();
				msg(sender, "§aАвторизация прошла успешно!");
			}
		} else if (getCommand().equals("register")){
			requireArgs(args, 0, "[Пароль]");
			String input = args[0];
			String pass = u.getPassword();
			if(!pass.equals(""))
				throw new CustomException("Вы уже зарегистрированы, пожалуйста, авторизуйтесь командой §e/login [Пароль]§c.");
			if(input.length() < 3) throw new CustomException("§cДлина пароля не может быть меньше трёх символов.");
			u.setPassword(input);
			registerNewPlayer(sender.getName(), u.getPassword());

			u.authorize();
			msg(sender, "§aВы успешно зарегистрировались!");
		}
	}
	
	public static boolean registerNewPlayer(String name, String passhash) {
		PlayerDataManager.save(new PersonInfo(name, passhash, 0, Rank.PLAYER, 0L, ""));
		return true;
	}
	
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		return new HashSet<>();
	}
}
