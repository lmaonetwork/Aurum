package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

public class CommandHub extends Command {
	
	public CommandHub() {
		super("hub", Rank.PLAYER, "Телепортация в лобби.", "h");
	}
	@Override
	protected void run(CommandSender sender, String[] args) {
		((ProxiedPlayer) sender).connect(Proxy.getServer("LOBBY_1"));
		msg(sender, "§aДобро пожаловать в @§fLOBBY_1§a.");
	}
}
