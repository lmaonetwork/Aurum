package pro.delfik.proxy.cmd.ex;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.util.U;

public class ExNotEnoughArguments extends ExCustom {
	public ExNotEnoughArguments(String usage) {
		super(usage);
	}

	@Override
	public void execute(CommandSender sender, String command) {
		U.msg(sender, "§cНедостаточно аргументов.");
		U.msg(sender, "§cИспользование: §e/" + command + " " + getMessage());
	}
}
