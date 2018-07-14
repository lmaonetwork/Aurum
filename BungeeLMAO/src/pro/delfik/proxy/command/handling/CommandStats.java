package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.games.SfTop;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.U;

public class CommandStats extends Command{
	public CommandStats() {
		super("stats", Rank.PLAYER, "");//TODO
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		SfTop top = SfTop.getPerson(sender.getName());
		U.msg(sender, top.getBeds());//TODO
		U.msg(sender, top.getDeaths());
		U.msg(sender, top.getGames());
		U.msg(sender, top.getWins());
	}
}
