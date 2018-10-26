package pro.delfik.proxy.cmd.moder;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.modules.Ban;
import pro.delfik.proxy.User;
import implario.util.Converter;
import implario.util.Rank;

import java.util.Arrays;
import java.util.HashSet;

@Cmd(args = 2, help = "[Игрок] {Время} [Причина]")
public class CmdBan extends Command{
	public CmdBan(){
		super("ban", Rank.MODER, "Заблокировать игрока");
	}

	@Override
	protected void run(User user, String args[]) {
		requireArgs(args, 2, "[Игрок] {Время} [Причина]");
		if (args[0].length() == 0) throw new ExCustom("§cПроверьте количество пробелов в этом месте: §e/ban__" + args[1] + "...");
		int time = 0;
		boolean skipTime = false;
		try {
			time = Integer.parseInt(args[1]);
		} catch (NumberFormatException ignored) {
			skipTime = true;
		}
		Ban.ban(args[0], Converter.mergeArray(args, skipTime ? 1 : 2, " "), time, user.getName());
	}

	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return super.tabComplete(sender, arg, number);
		if (number == 1) return Arrays.asList("60", "120", "720", "1440", "2880", "10080");
		else return new HashSet<>();
	}
}
