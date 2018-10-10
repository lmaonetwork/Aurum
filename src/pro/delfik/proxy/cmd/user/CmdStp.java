package pro.delfik.proxy.cmd.user;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.User;
import implario.util.Rank;
import implario.util.Converter;

import java.util.Collections;

@Cmd(args = 1, help = "[Сервер]")
public class CmdStp extends Command {
	public CmdStp() {
		super("stp", Rank.PLAYER, "Телепортироваться на другой сервер");
	}
	
	@Override
	protected void run(User user, String[] args) {
		ServerInfo i = requireServer(args[0]);
		if ((args[0].startsWith("TEST_") || args[0].startsWith("B_")) && !user.hasRank(Rank.BUILDER)){
			user.msg("§cТебе нельзя заходить на сервер @§f" + args[0] + "§c.");
			user.msg("§cТребуемый ранг - " + Rank.BUILDER.represent());
			return;
		}
		if (args[0].startsWith("BW_") && !user.hasRank(Rank.TESTER)){
			user.msg("§cТебе нельзя заходить на сервер @§f" + args[0] + "§c.");
			user.msg("§cТребуемый ранг - " + Rank.TESTER.represent());
			return;
		}
		user.getHandle().connect(i);
		user.msg("§aВы были телепортированы на сервер §f" + i);
	}
	
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return Converter.tabComplete(Proxy.getServers().keySet(), s -> s, arg);
		else return Collections.emptySet();
	}
}
