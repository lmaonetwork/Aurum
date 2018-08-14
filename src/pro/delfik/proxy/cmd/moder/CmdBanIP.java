package pro.delfik.proxy.cmd.moder;

import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.BansIP;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Converter;
import pro.delfik.util.Rank;

public class CmdBanIP extends Command{
	public CmdBanIP(){
		super("ban-ip", Rank.MODER, "Заблокировать IP");
	}

	@Override
	protected void run(User user, String args[]) {
		requireArgs(args, 2, "[Игрок|IP-адрес] [Причина]");
		if (args[0].length() == 0) throw new ExCustom("§cПроверьте количество пробелов в этом месте: §e/ban__" + args[1] + "...");
		String toBan = args[0];
		String reason = Converter.mergeArray(args, 1, " ");
		if (toBan.contains(".")) {
			BansIP.banIP(toBan, reason, user.getName());
			user.msg("§6IP-адрес забанен. Обратите внимание, что если на сервере был игрок");
			user.msg("§6С таким IP, то он не будет автоматически кикнут.");
			user.msg("§6Для этого есть §e/ban-ip [Ник игрока]§6.");
		}
		else BansIP.banPlayer(args[0], Converter.mergeArray(args, 1, " "), user.getName());
	}
}
