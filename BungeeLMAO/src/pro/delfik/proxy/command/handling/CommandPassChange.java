package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.CryptoUtils;

public class CommandPassChange extends Command {
	
	public CommandPassChange() {
		super("passchange", Rank.PLAYER, "Изменить пароль");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 2, "[Старый пароль] [Новый пароль]");
		Person p = Person.get(sender);
		String oldPword = args[0];
		String oldHash = CryptoUtils.getHash(oldPword);
		String newPword = args[1];
		if (!oldHash.equals(p.getPassword())) {
			msg(sender, "§cСтарый пароль введён неверно.");
			return;
		}
		if (oldPword.equals(newPword)) {
			msg(sender, "§cПогоди... Они же одинаковые... Ах ты, обмануть меня решил!");
			return;
		}
		p.setPassword(newPword);
		msg(sender, "§aПароль успешно изменён!");
	}
}
