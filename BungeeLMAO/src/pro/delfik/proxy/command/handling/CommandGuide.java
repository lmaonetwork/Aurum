package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.util.Rank;

public class CommandGuide extends Command{
	public CommandGuide() {
		super("guide", Rank.PLAYER, "Туториалы по разным вещам");
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		if (args.length == 0) {
			msg(sender, "§cИспользование: §f/guide a");
			return;
		}
		if (args[0].charAt(0) == 'a') {
			msg(sender, "§eЗдесь будет гайд."); // TODO: Написать гайд.
		}
	}
}
