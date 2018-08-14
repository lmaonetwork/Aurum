package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.User;
import pro.delfik.util.CryptoUtils;
import pro.delfik.util.Rank;

@Cmd(args = 1, help = "[Пароль]", auth = true)
public class CmdLogin extends Command{
	public CmdLogin(){
		super("login", Rank.PLAYER, "Авторизация на сервере", "l");
	}

	@Override
	protected void run(User user, String args[]) {
		if (user.isAuthorized()) throw new ExCustom("§eТы уже авторизован.");
		if(user.getPassword().equals(""))
			throw new ExCustom("§eДля первого входа в игру сперва нужно зарегистрироваться: /reg [Пароль]");
		requireArgs(args, 0, "[Пароль]");
		String input = args[0];
		String pass = user.getPassword();
		if(!(pass.equals(input) || pass.equals(CryptoUtils.getHash(input)))){
			user.kick("§cВведён неверный пароль.");
			return;
		}
		user.authorize();
		user.msg("§aАвторизация прошла успешно!");
	}
}
