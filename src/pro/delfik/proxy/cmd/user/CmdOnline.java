package pro.delfik.proxy.cmd.user;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.User;
import implario.util.Converter;

import java.util.Collection;
import java.util.Collections;

public class CmdOnline extends Command {
	public CmdOnline() {
		super("online", null, "Мониторинг онлайна.");
	}
	
	@Override
	protected void run(User user, String args[]) {
		Proxy.getServers().forEach((name, server) -> {
			Collection c = server.getPlayers();
			user.msg(server, " §e" + (c.isEmpty() ? "§7§oсервер пуст." : Converter.merge(server.getPlayers(), p -> {return p.getDisplayName();}, "§f, §e")));
		});
	}
	
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		return Collections.emptySet();
	}
}
