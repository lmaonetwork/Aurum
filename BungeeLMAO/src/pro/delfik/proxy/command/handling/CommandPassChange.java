package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.CustomException;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;

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
		if (!oldHash.equals(p.getPassword())) throw new CustomException("§cСтарый пароль введён неверно.");
		if (oldPword.equals(newPword)) throw new CustomException("§cПогоди... Они же одинаковые... Ах ты, обмануть меня решил!");
		p.setPassword(newPword);
		msg(sender, "§aПароль успешно изменён!");
	}
}
