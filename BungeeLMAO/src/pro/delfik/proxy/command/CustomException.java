package pro.delfik.proxy.command;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.util.U;

public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }

    public void execute(CommandSender sender, String command){
        U.msg(sender, getMessage());
    }
}
