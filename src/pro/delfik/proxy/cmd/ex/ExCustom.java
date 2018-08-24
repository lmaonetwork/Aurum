package pro.delfik.proxy.cmd.ex;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.util.U;

public class ExCustom extends RuntimeException {
    public ExCustom(String message){
        super(message);
    }

    public void execute(CommandSender sender, String command){
        if (getMessage() != null) U.msg(sender, getMessage());
    }
}
