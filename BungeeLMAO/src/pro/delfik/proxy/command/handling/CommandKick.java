package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

import java.util.Collections;

public class CommandKick extends Command {
	public CommandKick() {
		super("kick", Rank.MODER, "Отключить игрока от сервера.");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 2, "[Игрок] [Причина]");
		ProxiedPlayer p = requirePlayer(args[0]);
		String reason = Converter.mergeArray(args, 1, " ");
		kick(p, sender.getName(), reason);
	}
	
	public static void kick(ProxiedPlayer p, String moderator, String reason) {
		p.disconnect(new TextComponent("§cВы были кикнуты с сервера модератором §e" + moderator + "\n§cПричина: §e" + reason));
	}
	
	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return super.tabComplete(sender, arg, number);
		else return Collections.emptySet();
	}
}
