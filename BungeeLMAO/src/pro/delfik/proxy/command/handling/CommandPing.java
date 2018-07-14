package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Rank;

public class CommandPing extends Command{

    public CommandPing(){
        super("ping", Rank.PLAYER, "TODO");//TODO
    }

    @Override
    protected void run(CommandSender sender, String[] args) {
        msg(sender, ((ProxiedPlayer)sender).getPing());//TODO
    }
}
