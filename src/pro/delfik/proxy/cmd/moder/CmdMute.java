package pro.delfik.proxy.cmd.moder;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.module.Mute;
import pro.delfik.proxy.user.User;
import implario.util.Converter;
import implario.util.Rank;

import java.util.Arrays;
import java.util.HashSet;

@Cmd(args = 3, help = "[Игрок] [Время] [Причина]")
public class CmdMute extends Command{
	public CmdMute(){
		super("mute", Rank.MODER, "Запретить игроку писать в чат");
	}

	@Override
	protected void run(User user, String[] args) {
		Mute.mute(args[0], Converter.mergeArray(args, 2, " "), requireInt(args[1]), user.getName());
	}

	@Override
	protected Iterable<String> tabComplete(CommandSender sender, String arg, int number) {
		if (number == 0) return super.tabComplete(sender, arg, number);
		if (number == 1) return Arrays.asList("30", "60", "120", "240", "480", "1440", "2880");
		else return new HashSet<>();
	}
}
