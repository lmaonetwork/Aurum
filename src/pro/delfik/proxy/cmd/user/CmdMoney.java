package pro.delfik.proxy.cmd.user;

import implario.util.Rank;
import implario.util.StringUtils;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;

public class CmdMoney extends Command {

	public CmdMoney() {
		super("money", Rank.PLAYER, "Проверка баланса", "balance");
	}

	@Override
	protected void run(User user, String[] args) {
		long money = user.getMoney();
		user.msg("§aВаш баланс: §e" + money + " соларит" + StringUtils.plural(money, "", "а", "а") + "§a.");
	}

}
