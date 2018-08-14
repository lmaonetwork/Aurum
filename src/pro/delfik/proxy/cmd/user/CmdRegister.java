package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.data.PlayerDataManager;
import pro.delfik.proxy.user.User;
import pro.delfik.proxy.user.UserInfo;
import pro.delfik.util.Rank;

import java.util.ArrayList;

@Cmd(args = 1, help = "[Пароль]")
public class CmdRegister extends Command{
	public CmdRegister(){
		super("register", Rank.PLAYER, "Регистрация на сервере", "p", "reg");
	}

	@Override
	protected void run(User user, String[] args) {
		if (user.isAuthorized()) throw new ExCustom("§eТы уже авторизован.");
		String input = args[0];
		String pass = user.getPassword();
		if(!pass.equals(""))
			throw new ExCustom("Вы уже зарегистрированы, пожалуйста, авторизуйтесь командой §e/login [Пароль]§c.");
		if(input.length() < 3) throw new ExCustom("§cДлина пароля не может быть меньше трёх символов.");
		user.setPassword(input);
		registerNewPlayer(user.getName(), user.getPassword());

		user.authorize();
		user.msg("§aВы успешно зарегистрировались!");
	}

	private static void registerNewPlayer(String name, String passhash) {
		PlayerDataManager.save(new UserInfo(name, passhash, 0, Rank.PLAYER, 0L, "", false, new ArrayList<>(), false, new ArrayList<>()));
	}
}
