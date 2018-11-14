package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.modules.SfTop;
import pro.delfik.proxy.User;
import implario.util.Rank;
import implario.util.Converter;
import pro.delfik.proxy.stats.GameStats;
import pro.delfik.proxy.stats.Top;

@Cmd(args = 1, help = "[Игра]")
public class CmdStats extends Command{
	public CmdStats() {
		super("stats", Rank.PLAYER, "Просмотр статистики");
	}

	@Override
	protected void run(User user, String args[]) {
		if (user.getServer().startsWith("UHC_")) {
			user.getHandle().chat("/est");
			return;
		}
		Top top = Top.get(args[0].toUpperCase());
		if(top == null)throw new ExCustom("Игра не найдена");
		if(args.length == 2 && args[1].equals("top")){
			for(String str : top.generateTop().split("\n"))
				user.msg(str);
			return;
		}
		GameStats stats = top.read(user.getName());
		if(stats == null)throw new ExCustom("Статистика не найдена");
		for(String line : stats.toReadableString())
			user.msg(line);
	}
}
