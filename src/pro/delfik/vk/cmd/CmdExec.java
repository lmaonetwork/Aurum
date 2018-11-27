package pro.delfik.vk.cmd;

import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.module.Admin;
import pro.delfik.proxy.user.VKUser;

public class CmdExec implements Command{
    @Override
    public String exec(String args[], int id) {
        if(!Admin.is(id))return "";
        VKUser user = new VKUser(id);
        user.getCache().add(BungeeCord.getInstance().getPluginManager().dispatchCommand(user.getSender(), String.join(" ", args)) + "");
        user.unload();
        return String.join("\n", user.getCache().toArray(new String[]{}));
    }
}
