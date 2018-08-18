package pro.delfik.proxy.cmd.moder;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.BanIP;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;

@Cmd(args = 1, help = "[Игрок]")
public class CmdUnbanIP extends Command{
	public CmdUnbanIP(){
		super("unban-ip", Rank.MODER, "Разблокировать IP");
	}

	@Override
	protected void run(User user, String args[]) {
		boolean ip = args[0].contains(".");
		if ((ip ? BanIP.getByAddress(args[0]) : BanIP.getByName(args[0])) == null)
			throw new ExCustom((ip ? "§eIP-адрес §f" : "§eИгрок §f") + args[0] + "§e не заблокирован.");
		if (ip) BanIP.unbanIP(args[0], user.getName());
		else BanIP.unbanNickname(args[0], user.getName());
	}
}
