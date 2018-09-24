package pro.delfik.proxy.cmd.moder;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.modules.BanIP;
import pro.delfik.proxy.User;
import implario.util.Rank;

@Cmd(args = 1, help = "[Игрок]")
public class CmdUnbanIP extends Command{
	public CmdUnbanIP(){
		super("unban-ip", Rank.MODER, "Разблокировать IP");
	}

	@Override
	protected void run(User user, String args[]) {
		boolean ip = args[0].contains(".");
		BanIP ban = ip ? BanIP.get(args[0]) : BanIP.getByName(args[0]);
		if (ban == null) throw new ExCustom((ip ? "§eIP-адрес §f" : "§eИгрок §f") + args[0] + "§e не заблокирован.");
		else BanIP.unbanIP(args[0], user.getName());
	}
}
