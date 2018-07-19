package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.net.packet.PacketWrite;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.CustomException;
import pro.delfik.proxy.command.ServerNotFoundException;
import pro.delfik.proxy.connection.Server;
import pro.delfik.util.Rank;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CommandUpdate extends Command {
	public CommandUpdate() {
		super("update", Rank.DEV, "Обновление плагинов");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 1, "[Файл]");
		StringBuilder buffer = new StringBuilder();
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File("Core/plugins/" + args[0])));
			while (true) {
				int i = in.read();
				if (i == -1) break;
				buffer.append((char) i);
			}
			in.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		if (buffer.length() == 0) throw new CustomException("Файл §f" + args[0] + " §cне найден.");
		String server = args.length == 2 ? args[1] : ((ProxiedPlayer) sender).getServer().getInfo().getName();
		PacketWrite write = new PacketWrite("plugins/" + args[0], buffer.toString());
		if (server.equals("all")) {
			for (Server serv : Server.getServers()) {
				serv.send(write);
				msg(sender, "§aФайл §f" + args[0] + "§a был отправлен на §f" + serv.getServer());
			}
			return;
		}
		Server serv = Server.get(server);
		if (serv == null) throw new ServerNotFoundException(args[1]);
		serv.send(write);
		msg(sender, "§aФайл §f" + args[0] + "§a был отправлен на §f" + serv.getServer());
	}
}
