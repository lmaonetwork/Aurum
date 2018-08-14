package pro.delfik.proxy.cmd.moder;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.Mutes;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;

@Cmd(args = 1, help = "[Игрок]")
public class CmdUnmute extends Command{
	public CmdUnmute(){
		super("unmute", Rank.MODER, "Разрешить игроку писать в чат");
	}

	@Override
	protected void run(User user, String[] args) {
		if (Mutes.get(args[0]) == null) throw new ExCustom("§eИгрок §f" + args[0] + "§e не замучен.");
		Mutes.unmute(args[0], user.getName());
	}
}
