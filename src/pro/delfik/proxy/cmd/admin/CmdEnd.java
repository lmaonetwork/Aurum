package pro.delfik.proxy.cmd.admin;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import pro.delfik.util.Rank;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.util.concurrent.TimeUnit;

public class CmdEnd extends Command {
	public CmdEnd() {
		super("end", Rank.ADMIN, "Остановить прокси");
	}

	@Override
	protected void run(User user, String args[]) {
		if (args.length == 1 && args[0].equals("-f")) {
			user.msg("§cПринудительная остановка...");
			BungeeCord.getInstance().stop("§cСервер принудительно остановлен.");
			return;
		}
		String reason = Converter.mergeArray(args, 0, " ");
		U.bc(null, "§c§lСервер будет перезагружен через 5 секунд!");
		Proxy.i().getScheduler().schedule(Aurum.instance, () ->
				Proxy.i().stop(reason == null ? "§dСервер перезагружается.\n§dЭто займёт некоторое время." : reason),
		5, TimeUnit.SECONDS);
	}
}
