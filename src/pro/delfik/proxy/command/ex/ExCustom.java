package pro.delfik.proxy.command.ex;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.util.U;

public class ExCustom extends RuntimeException {
    public ExCustom(String message){
        super(message);
    }

    public void execute(CommandSender sender, String command){
        U.msg(sender, getMessage());
    }
}
