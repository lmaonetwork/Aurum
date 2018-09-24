package pro.delfik.proxy.cmd.user;

import implario.util.Rank;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.User;

@Cmd(args = 1, help = "[Пароль]", auth = true)
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
		user.authorize();
		user.msg("§aВы успешно зарегистрировались!");
	}

}
