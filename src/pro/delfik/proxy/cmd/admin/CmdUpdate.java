package pro.delfik.proxy.cmd.admin;

import pro.delfik.net.packet.PacketWrite;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.cmd.ex.ExServerNotFound;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.user.User;
import pro.delfik.util.FileConverter;
import pro.delfik.util.Rank;

public class CmdUpdate extends Command {
	public CmdUpdate() {
		super("update", Rank.ADMIN, "Обновление плагинов");
	}
	
	@Override
	protected void run(User user, String args[]) {
		requireArgs(args, 1, "[Файл]");
		byte file[] = FileConverter.read(DataIO.getFile("plugins/" + args[0]));
		if (file == null) throw new ExCustom("Файл §f" + args[0] + " §cне найден.");
		String server = args.length == 2 ? args[1] : user.getServerInfo().getName();
		PacketWrite write = new PacketWrite("plugins/" + args[0], file);
		if (server.equals("all")) {
			for (Server serv : Server.getServers()) {
				serv.send(write);
				user.msg("§aФайл §f" + args[0] + "§a был отправлен на §f" + serv.getServer());
			}
			return;
		}
		Server serv = Server.get(server);
		if (serv == null) throw new ExServerNotFound(args[1]);
		serv.send(write);
		user.msg("§aФайл §f" + args[0] + "§a был отправлен на §f" + serv.getServer());
	}
}
