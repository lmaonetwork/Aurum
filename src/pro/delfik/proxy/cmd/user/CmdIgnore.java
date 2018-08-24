package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Converter;
import pro.delfik.util.Rank;
import pro.delfik.util.U;

@Cmd(args = 1, help = "[Игрок]")
public class CmdIgnore extends Command {
	public CmdIgnore() {
		super("ignore", Rank.PLAYER, "Запретить игроку отправлять вам ЛС");
	}

	@Override
	protected void run(User user, String[] args) {
		String victim = args[0].toLowerCase();
		if (victim.equals("@a")) {
			user.msg("§aПриватные сообщения " + Converter.representBoolean(!user.togglePM()));
			return;
		}
		if (user.isIgnoring(victim)){
			user.unignore(victim);
			user.msg(U.run("§aТеперь §e" + victim + "§a снова может общаться с вами. (§f§nОтменить§a)", "§f>> §c§lОтмена §f<<", "/ignore " + args[0]));
		}else {
			user.ignore(victim);
			user.msg(U.run("§aВы запретили игроку §e" + victim + "§a отправлять вам ЛС. (§f§nОтменить§a)", "§f>> §c§lОтмена §f<<", "/ignore " + args[0]));
		}
	}
}
