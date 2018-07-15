package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;

import java.util.Collections;

public class CommandStp extends Command {
	public CommandStp() {
		super("stp", Rank.PLAYER, "Телепортироваться на другой сервер");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 1, "[Сервер]");
		ProxiedPlayer p = (ProxiedPlayer) sender;
		ServerInfo i = requireServer(args[0]);
		if ((args[0].startsWith("TEST_") || args[0].startsWith("B_")) && !Person.get(sender).hasRank(Rank.BUILDER)){
			msg(sender, "§cТебе нельзя заходить на сервер @§f" + args[0] + "§c.");
			msg(sender, "§cТребуемый ранг - " + Rank.BUILDER.represent());
			return;
		}
		p.connect(i);
		msg(sender, "§aВы были телепортированы на сервер §f" + i);
	}
	
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return Converter.tabComplete(Proxy.getServers().keySet(), s -> s, arg);
		else return Collections.emptySet();
	}
}
