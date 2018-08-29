package pro.delfik.proxy.cmd.kurator;

import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import implario.util.Rank;
import implario.util.Converter;
import pro.delfik.util.U;

@Cmd(args = 1, help = "[Сообщение]")
public class CmdAlert extends Command {
	
	public CmdAlert() {
		super("alert", Rank.KURATOR, "Отправить сообщение на все сервера");
	}
	
	@Override
	protected void run(User user, String[] args) {
		Proxy.i().broadcast("§e§l[§d§lВнимание§e§l] §e" + U.color(Converter.mergeArray(args, 0, " ")));
	}
}
