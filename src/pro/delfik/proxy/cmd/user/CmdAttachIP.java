package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;

public class CmdAttachIP extends Command {
	
	public CmdAttachIP() {
		super("attachip", Rank.PLAYER, "Привязать IP-адрес к аккаунту.");
	}
	
	@Override
	protected void run(User user, String[] args) {
		if (CmdVK.getPage(user.getName()) == -1) {
			user.msg("§cК вашему аккаунту не привязана страница ВКонтакте.");
			user.msg("§cДля использования привязки по IP необходимо привязать ВК.");
			user.msg("§cЭто делается при помощи команды §e/vk [ID страницы]");
			return;
		}
		boolean b = user.setIPBound(!user.isIPBound());
		if (b) {
			user.msg("§aIP-адрес успешно привязан.");
			user.msg("§aТеперь вы сможете войти на сервер только с вашего IP.");
			user.msg("§aКогда ваш IP изменится, просто напишите боту ВКонтакте следующую команду:");
			user.msg("§f§lipchange");
			user.msg("§aЗатем у вас будет минута, чтобы войти в аккаунт и ввести пароль.");
			user.msg("§aБольше вам не нужно авторизовываться!");
		} else {
			user.msg("§6IP-адрес отвязан. Теперь кто угодно может заходить на ваш аккаунт.");
			user.msg( "§6Теперь при входе снова требуется вводить пароль.");
		}
	}
}
