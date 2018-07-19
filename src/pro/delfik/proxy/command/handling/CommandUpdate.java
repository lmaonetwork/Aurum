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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CommandUpdate extends Command{
	public CommandUpdate() {
		super("update", Rank.DEV, "");//TODO
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		if(args.length == 0)throw new NotEnoughArgumentsException("");//TODO
		StringBuilder buffer = new StringBuilder();
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File("Core/plugins/" + args[0])));
			while (true){
				int i = in.read();
				if(i == -1)break;
				buffer.append((char)i);
			}
			in.close();
		}catch (IOException ex){
			throw new CustomException("LolKek");
		}
		if(buffer.length() == 0)throw new CustomException("Файла нету");//TODO
		String server = args.length == 2 ? args[1] : ((ProxiedPlayer)sender).getServer().getInfo().getName();
		sender.sendMessage(buffer.length() + "");
		PacketWrite write = new PacketWrite("plugins/" + args[0], buffer.toString());
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
