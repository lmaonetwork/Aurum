package pro.delfik.proxy.cmd.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.util.Rank;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

public class CommandAlert extends Command {
	
	public CommandAlert() {
		super("alert", Rank.KURATOR, "Отправить сообщение на все сервера");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 1, "[Сообщение]");
		Proxy.i().broadcast("§e§l[§d§lВнимание§e§l] §e" + U.color(Converter.mergeArray(args, 0, " ")));
	}
}
