package pro.delfik.vk.cmd;

import net.md_5.bungee.api.config.ServerInfo;
import pro.delfik.proxy.Proxy;

public class CmdOnline extends Cmd{
    @Override
    public String execute(String args[], int id) {
        ServerInfo infos[] = Proxy.i().getServers().values().toArray(new ServerInfo[]{});
        String result = "Общий онлайн: " + Proxy.i().getPlayers().size();
        for(int i = 0; i < infos.length; i++){
            ServerInfo info = infos[i];
            result = result + "\r\n" + info.getName() + ": " + info.getPlayers().size();
        }
        return result;
    }
}
