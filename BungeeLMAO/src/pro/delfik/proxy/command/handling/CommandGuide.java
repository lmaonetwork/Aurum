package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

public class CommandGuide extends Command{
	public CommandGuide() {
		super("guide", Rank.PLAYER, "");//TODO
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		//Ну выводи
	}
}
