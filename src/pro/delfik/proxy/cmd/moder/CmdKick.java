package pro.delfik.proxy.cmd.moder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.module.Kick;
import pro.delfik.proxy.user.User;
import implario.util.Converter;
import implario.util.Rank;

import java.util.Collections;

@Cmd(args = 2, help = "[Игрок] [Причина]")
public class CmdKick extends Command{
	public CmdKick() {
		super("kick", Rank.MODER, "Отключить игрока от сервера.");
	}

	@Override
	protected void run(User user, String args[]) {
		ProxiedPlayer player = requirePlayer(args[0]);
		String reason = Converter.mergeArray(args, 1, " ");
		Kick.kick(player, user.getName(), reason);
	}

	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return super.tabComplete(sender, arg, number);
		else return Collections.emptySet();
	}
}
