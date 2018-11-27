package pro.delfik.proxy.cmd.user;

import implario.util.Rank;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;

public class CmdTheme extends Command {
	public CmdTheme() {
		super("theme", Rank.PLAYER, "Изменить тему");
	}

	@Override
	protected void run(User user, String[] args) {
		user.msg(user.toggleDarkTheme());
	}
}
