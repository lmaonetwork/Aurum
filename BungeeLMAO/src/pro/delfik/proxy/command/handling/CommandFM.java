package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

public class CommandFM extends Command{
	public CommandFM(String command, String description, String... aliases){
		super(command, Rank.PLAYER, description, aliases);
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		String command = getCommand();
		if(command.equals("osk")){
			sender.
		}
	}
}
