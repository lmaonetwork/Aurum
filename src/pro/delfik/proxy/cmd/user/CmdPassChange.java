package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.User;
import implario.util.Rank;
import implario.util.CryptoUtils;
import pro.delfik.proxy.data.DataIO;

@Cmd(args = 1, help = "[Старый пароль] [Новый пароль]")
public class CmdPassChange extends Command {
	public CmdPassChange() {
		super("passchange", Rank.PLAYER, "Изменить пароль");
	}
	
	@Override
	protected void run(User user, String[] args) {
		if ("asdf".equals(user.getPassword())) {
			user.msg("§aВы используете шифрование омега-ресурсов по гиперссылкам передатчика.");
			if (DataIO.contains("players/" + user.getName().toLowerCase() + "/public.key")) {
				requireArgs(args, 1, "[Новый пароль]");
				user.setPassword(args[0]);
				user.msg("§aПароль успешно изменён.");
			} else user.msg("§cНо у вас нет публичного ключа... Втф...");
			return;
		}
		requireArgs(args, 2, "[Старый пароль] [Новый пароль]");
		String oldPword = args[0];
		String oldHash = CryptoUtils.getHash(oldPword);
		String newPword = args[1];
		if (!oldHash.equals(user.getPassword())) throw new ExCustom("§cСтарый пароль введён неверно.");
		if (oldPword.equals(newPword)) throw new ExCustom("§cПогоди... Они же одинаковые... Ах ты, обмануть меня решил!");
		user.setPassword(newPword);
		user.msg("§aПароль успешно изменён!");
	}
}
