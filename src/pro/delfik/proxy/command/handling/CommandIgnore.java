package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.util.Rank;
import pro.delfik.util.U;

public class CommandIgnore extends Command {

	public CommandIgnore() {
		super("ignore", Rank.PLAYER, "Запретить игроку отправлять вам ЛС");
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		Person p = Person.get(sender);
		String victim = args[0].toLowerCase();
		if (p.getIgnoredPlayers().remove(victim))
			msg(sender, U.run("§aТеперь §e" + victim + "§a снова может общаться с вами. (§f§nОтменить§a)", "§f>> §c§lОтмена §f<<", "/ignore " + args[0]));
		else {
			p.getIgnoredPlayers().add(victim);
			msg(sender, U.run("§aВы запретили игроку §e" + victim + "§a отправлять вам ЛС. (§f§nОтменить§a)", "§f>> §c§lОтмена §f<<", "/ignore " + args[0]));
		}
	}
}
