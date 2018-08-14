package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;
import pro.delfik.util.CryptoUtils;

@Cmd(args = 2, help = "[Старый пароль] [Новый пароль]")
public class CmdPassChange extends Command {
	public CmdPassChange() {
		super("passchange", Rank.PLAYER, "Изменить пароль");
	}
	
	@Override
	protected void run(User user, String[] args) {
		String oldPword = args[0];
		String oldHash = CryptoUtils.getHash(oldPword);
		String newPword = args[1];
		if (!oldHash.equals(user.getPassword())) throw new ExCustom("§cСтарый пароль введён неверно.");
		if (oldPword.equals(newPword)) throw new ExCustom("§cПогоди... Они же одинаковые... Ах ты, обмануть меня решил!");
		user.setPassword(newPword);
		user.msg("§aПароль успешно изменён!");
	}
}
