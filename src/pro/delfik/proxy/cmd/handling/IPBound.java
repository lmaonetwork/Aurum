package pro.delfik.proxy.cmd.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;
import pro.delfik.util.U;

public class IPBound extends Command {
	
	public IPBound() {
		super("attachip", Rank.PLAYER, "Привязать IP-адрес к аккаунту.");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		User p = requirePerson(sender.getName());
		if (CommandVK.getPage(p.getName()) == -1) {
			U.msg(sender, "§cК вашему аккаунту не привязана страница ВКонтакте.");
			U.msg(sender, "§cДля использования привязки по IP необходимо привязать ВК.");
			U.msg(sender, "§cЭто делается при помощи команды §e/vk [ID страницы]");
			return;
		}
		boolean b = p.setIPBound(!p.isIPBound());
		if (b) {
			U.msg(sender, "§aIP-адрес успешно привязан.");
			U.msg(sender, "§aТеперь вы сможете войти на сервер только с вашего IP.");
			U.msg(sender, "§aКогда ваш IP изменится, просто напишите боту ВКонтакте следующую команду:");
			U.msg(sender, "§f§lipchange");
			U.msg(sender, "§aЗатем у вас будет минута, чтобы войти в аккаунт и ввести пароль.");
			U.msg(sender, "§aБольше вам не нужно авторизовываться!");
		} else {
			U.msg(sender, "§6IP-адрес отвязан. Теперь кто угодно может заходить на ваш аккаунт.");
			U.msg(sender, "§6Теперь при входе снова требуется вводить пароль.");
		}
	}
}
