package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.net.packet.PacketWrite;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.CustomException;
import pro.delfik.proxy.command.NotEnoughArgumentsException;
import pro.delfik.proxy.connection.Server;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.util.Rank;

public class CommandUpdate extends Command{
	public CommandUpdate() {
		super("update", Rank.DEV, "");//TODO
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		if(args.length == 0)throw new NotEnoughArgumentsException("");//TODO
		String file = DataIO.readFile("plugins/" + args[0]);
		if(file == null)throw new CustomException("Файла нету");//TODO
		String server = args.length == 2 ? args[1] : ((ProxiedPlayer)sender).getServer().getInfo().getName();
		PacketWrite write = new PacketWrite("plugins/" + args[0], file);
		if(server.equals("all")){
			for(Server serv : Server.getServers())
				serv.send(write);
			sender.sendMessage("ололо обновы на всё");//TODO
			return;
		}
		Server serv = Server.get(server);
		if(serv == null)throw new CustomException("Серва нет");//TODO
		serv.send(write);
		sender.sendMessage("ололо обновы на серв");//TODO
	}
}
