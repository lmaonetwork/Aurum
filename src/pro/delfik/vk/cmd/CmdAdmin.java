package pro.delfik.vk.cmd;

import pro.delfik.vk.VK;
import pro.delfik.proxy.module.Admin;

public class CmdAdmin extends Cmd {
    @Override
    public String execute(String args[], int id) {
        if(!Admin.is(id))return "";
        if(args.length == 0)return "Need more args";
        String key = args[0];
        if(args.length == 2 && key.equalsIgnoreCase("add")){
            int admin = VK.getID(args[1]);
            if(admin == -1)return "Incorrect arg";
            Admin.add(admin);
            return "Add admin id " + admin + " link " + args[1];
        }else if(args.length == 2 && key.equalsIgnoreCase("remove")){
            int admin = VK.getID(args[1]);
            if(admin == -1)return "Incorrect arg";
            Admin.remove(admin);
            return "Remove admin id " + admin + " link " + args[1];
        }else if(key.equalsIgnoreCase("is")){
            int admin = VK.getID(args[1]);
            if(admin == -1)return "Incorrect arg";
            return Admin.is(admin) ? "True" : "False";
        }else if(key.equalsIgnoreCase("list")){
            String result = "";
            for(int i : Admin.iterable())
                result = result + ", " + i;
            result = result.substring(2);
            return result;
        }
        return "Subcommand not found";
    }
}
