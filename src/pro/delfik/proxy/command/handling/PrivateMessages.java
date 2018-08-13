package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.ex.ExCustom;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Converter;
import pro.delfik.util.Rank;
import pro.delfik.util.U;

public class PrivateMessages extends Command {
	public PrivateMessages(boolean reply) {
		super(reply ? "reply" : "tell", Rank.PLAYER, reply ? "Ответ на последнее личное сообщение" : "Личное сообщение игроку",
				reply ? new String[] {"r"} : new String[] {"t", "w", "pm", "msg", "m"});
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		User p = User.get(sender);
		User dest;
		String msg;
		if (getCommand().charAt(0) == 'r') {
			requireArgs(args, 1, "[Сообщение]");
			if (p.lastWriter == null) throw new ExCustom("§cВы ещё никому не написали.");
			dest = User.get(p.lastWriter);
			if (dest == null) throw new ExCustom("§6Игрок, с которым вы общались, вышел с сервера.");
			msg = Converter.mergeArray(args, 0, " ");
			dest.lastWriter = p.name;
		} else {
			requireArgs(args, 2, "[Игрок] [Сообщение]");
			dest = requirePerson(args[0]);
			p.lastWriter = dest.name;
			dest.lastWriter = p.name;
			msg = Converter.mergeArray(args, 1, " ");
		}
		if (dest.isIgnoring(p.getName())) {
			U.msg(sender, "§cВы находитесь в чёрном списке у игрока §e", dest, "§c.");
			return;
		}
		if (p.isIgnoring(dest.getName())) throw new ExCustom("§cВы не можете писать игроку, который находится у вас в игноре.");
		dest.msg("§e[§f", sender, "§e -> §fВы§e] " + msg);
		p.msg("§e[§fВы §e-> §f", dest, "§e] " + msg);
	}
}
