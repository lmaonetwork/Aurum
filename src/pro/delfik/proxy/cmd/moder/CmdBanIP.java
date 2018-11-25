package pro.delfik.proxy.cmd.moder;

import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.BanIP;
import pro.delfik.proxy.User;
import implario.util.Converter;
import implario.util.Rank;

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
			BanIP.banIP(toBan, reason, user.getName());
			user.msg("§6IP-адрес забанен. Обратите внимание, что если на сервере был игрок");
			user.msg("§6С таким IP, то он не будет автоматически кикнут.");
			user.msg("§6Для этого есть §e/ban-ip [Ник игрока]§6.");
		}
		else {
			User u = requirePerson(args[0]);
			BanIP ban = BanIP.banIP(u.getIP(), Converter.mergeArray(args, 1, " "), user.getName());
			u.getHandle().disconnect(ban.kickMessage(u.getName()));
		}
	}
}
