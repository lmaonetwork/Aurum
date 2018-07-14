package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

public class CommandPing extends Command{

	public CommandPing() {
		super("ping", Rank.PLAYER, "Проверить пинг игрока");
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		ProxiedPlayer victim = args.length == 0 ? ((ProxiedPlayer) sender) : requirePlayer(args[0]);
		msg(sender, "§a[§f", victim, "§a] Пинг до прокси - §f" + victim.getPing() + " §aмс.");
		msg(sender, "§a[§f", victim, "§a] Пинг до @" + victim.getServer().getInfo().getName() + " - §fнеизвестен§a.");
	}
}
