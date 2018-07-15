package pro.delfik.proxy.command;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.util.U;

public class NotEnoughArgumentsException extends CustomException {
	public NotEnoughArgumentsException(String usage) {
		super(usage);
	}

	@Override
	public void execute(CommandSender sender, String command) {
		U.msg(sender, "§cНедостаточно аргументов.");
		U.msg(sender, "§cИспользование: §e/" + command + " " + getMessage());
	}
}
