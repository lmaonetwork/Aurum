package pro.delfik.proxy.command.handling;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.AurumPlugin;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.U;

import java.util.concurrent.TimeUnit;

public class CommandEnd extends Command {
	public CommandEnd() {
		super("end", Rank.ADMIN, "Остановить прокси");
	}
	@Override
	protected void run(CommandSender sender, String[] args) {
		if (args.length == 1 && args[0].equals("-f")) {
			msg(sender, "§cПринудительная остановка...");
			BungeeCord.getInstance().stop("§cСервер принудительно остановлен.");
			return;
		}
		String reason = Converter.mergeArray(args, 0, " ");
		U.bc(null, "§c§lСервер будет перезагружен через 5 секунд!");
		Proxy.i().getScheduler().schedule(AurumPlugin.instance, () ->
				Proxy.i().stop(reason == null ? "§dСервер перезагружается.\n§dЭто займёт некоторое время." : reason),
		5, TimeUnit.SECONDS);
	}
}
