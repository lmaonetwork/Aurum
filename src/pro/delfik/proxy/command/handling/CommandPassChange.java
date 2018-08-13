package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.ex.ExCustom;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;
import pro.delfik.util.CryptoUtils;

public class CommandPassChange extends Command {
	
	public CommandPassChange() {
		super("passchange", Rank.PLAYER, "Изменить пароль");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 2, "[Старый пароль] [Новый пароль]");
		User p = User.get(sender);
		String oldPword = args[0];
		String oldHash = CryptoUtils.getHash(oldPword);
		String newPword = args[1];
		if (!oldHash.equals(p.getPassword())) throw new ExCustom("§cСтарый пароль введён неверно.");
		if (oldPword.equals(newPword)) throw new ExCustom("§cПогоди... Они же одинаковые... Ах ты, обмануть меня решил!");
		p.setPassword(newPword);
		msg(sender, "§aПароль успешно изменён!");
	}
}
