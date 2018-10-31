package pro.delfik.vk.cmd;

import pro.delfik.proxy.modules.Chat;
import pro.delfik.vk.module.Admin;

public class CmdAurum extends Cmd{
    @Override
    public String execute(String args[], int id) {
        if(!Admin.is(id))return "";
        if(args.length == 0)return "Need more args";
        String key = args[0];
        if(key.equalsIgnoreCase("addMat")){
            Chat.addMat(args[1]);
            return "Added mat " + args[1];
        }else if(key.equalsIgnoreCase("removeMat")){
            Chat.remMat(args[1]);
            return "Removed mat " + args[1];
        }else if(key.equalsIgnoreCase("checkMat")){
            return Chat.applyMat(args[1]);
        }
        return "Subcommand not found";
    }
}
