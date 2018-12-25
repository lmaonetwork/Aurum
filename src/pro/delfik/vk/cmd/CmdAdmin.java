package pro.delfik.vk.cmd;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import pro.delfik.vk.VK;
import pro.delfik.proxy.module.Admin;

import java.util.Arrays;
import java.util.Collections;

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
            StringBuilder builder = new StringBuilder();
            Admin.iterable().forEach((i) -> builder.append(i).append(", "));
            return builder.substring(0, builder.length() - 2) + " Lol";
        }
        return "Subcommand not found";
    }
}
