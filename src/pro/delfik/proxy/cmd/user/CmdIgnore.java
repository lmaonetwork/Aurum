package pro.delfik.proxy.cmd.user;

import implario.util.Converter;
import implario.util.Rank;
import net.md_5.bungee.api.chat.TextComponent;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.User;
import pro.delfik.util.U;

import java.util.List;

public class CmdIgnore extends Command {
	public CmdIgnore() {
		super("ignore", Rank.PLAYER, "Запретить игроку отправлять вам ЛС");
	}

	@Override
	protected void run(User user, String[] args) {
		if (args.length == 0) {
			List<TextComponent> btns = Converter.transform(user.getIgnoredPlayers(), s -> U.run(s + "§e, ", "§f>> §a§lУдалить §f<<", "ignore " + s + " -s"));
			U.msg(user.getSender(), btns.toArray());
			return;
		}

		String victim = args[0].toLowerCase();
		if (victim.equals("@a")) {
			user.msg("§aПриватные сообщения " + Converter.representBoolean(!user.togglePM()) + "ы");
			return;
		}
		if (user.isIgnoring(victim)){
			user.unignore(victim);
			if (args.length == 2 && args[1].equals("-s")) return;
			user.msg(U.run("§aТеперь §e" + victim + "§a снова может общаться с вами. (§f§nОтменить§a)", "§f>> §c§lОтмена §f<<", "/ignore " + args[0]));
		}else {
			requirePerson(args[0]);
			if (user.getIgnoredPlayers().size() > 39) throw new ExCustom("§cПревышен лимит на игнорируемых игроков (§f40 игроков§c).");
			user.ignore(victim);
			user.msg(U.run("§aВы запретили игроку §e" + victim + "§a отправлять вам ЛС. (§f§nОтменить§a)", "§f>> §c§lОтмена §f<<", "/ignore " + args[0]));
		}
	}
}
