package pro.delfik.proxy.cmd.moder;

import implario.util.Rank;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.module.Ban;
import pro.delfik.proxy.user.User;

@Cmd(args = 1, help = "[Игрок]")
public class CmdCheater extends Command{
	public CmdCheater(){
		super("cheater", Rank.RECRUIT, "Заблокировать игрока за читы", "cheat");
	}

	@Override
	protected void run(User user, String args[]) {
		requireArgs(args, 1, "[Игрок]");
		Ban.ban(args[0], "Читы", 0, user.getName());
	}

}
