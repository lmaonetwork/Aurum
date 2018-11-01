package pro.delfik.vk.cmd;

import implario.util.Rank;
import implario.util.UserInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.User;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.modules.Chat;
import pro.delfik.util.Logger;
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
        }else if(key.equalsIgnoreCase("setRank")){
            try {
                Rank rank = Command.requireRank(args[1]);
                Logger.log("Perm", "vk_" + id + " set " + rank + " " + args[0]);
                User u = User.get(args[0]);
                if (u != null) {
                    u.setRank(rank);
                    return "§aИгроку §f" + args[0] + "§a был выдан ранг §f" + rank.represent();
                }
                UserInfo info = User.read(args[0]);
                if (info == null) return "§cИгрок §e" + args[0] + "§c не зарегистрирован.";
                info.rank = rank;
                User.save(info);
                return "§aОффлайн-игроку §f" + args[0] + "§a был выдан ранг §f" + rank.represent();
            }catch (Throwable ex){
                return "Error";
            }
        }
        return "Subcommand not found";
    }
}
