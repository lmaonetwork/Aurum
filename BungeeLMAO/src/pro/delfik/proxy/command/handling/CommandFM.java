package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

public class CommandFM extends Command{
	public CommandFM(String command, String description, String... aliases){
		super(command, Rank.PLAYER, description, aliases);
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		String command = getCommand();
		if(command.equals("osk")){
			((ProxiedPlayer)sender).chat("/mute " + args[0] + " 120 1234");//TODO
		}else if(command.equals("flud")){
			((ProxiedPlayer)sender).chat("/mute " + args[0] + " 30 1234");
		}else if(command.equals("mat")){
			((ProxiedPlayer)sender).chat("/mute " + args[0] + " 60 1234");
		}
	}
}
