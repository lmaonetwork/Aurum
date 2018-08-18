package pro.delfik.proxy.cmd.moder;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.Ban;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;

@Cmd(args = 1, help = "[Игрок]")
public class CmdUnban extends Command{
	public CmdUnban(){
		super("unban", Rank.MODER, "Разблокировать игрока");
	}

	@Override
	protected void run(User user, String args[]) {
		if (Ban.get(args[0]) == null) throw new ExCustom("§eИгрок §f" + args[0] + "§e не заблокирован.");
		Ban.unban(args[0], user.getName());
	}


}
