package pro.delfik.proxy.cmd.user;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import implario.util.Rank;

public class CmdPing extends Command{

	public CmdPing() {
		super("ping", Rank.PLAYER, "Проверить пинг игрока");
	}

	@Override
	protected void run(User user, String args[]) {
		ProxiedPlayer victim = args.length == 0 ? user.getHandle() : requirePlayer(args[0]);
		user.msg("§a[§f", victim, "§a] Пинг до прокси - §f" + victim.getPing() + " §aмс.");
		user.msg("§a[§f", victim, "§a] Пинг до @" + victim.getServer().getInfo().getName() + " - §fнеизвестен§a.");
	}
}
