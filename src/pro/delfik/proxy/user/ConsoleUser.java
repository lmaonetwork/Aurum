package pro.delfik.proxy.user;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.module.Registeable;

public class ConsoleUser extends AUser implements Registeable {
    
    private String lastPenPal;
    
    @Override
    public void register() {
        User.put(getName(), this);
    }

    @Override
    public String getName() {
        return Proxy.getConsole().getName();
    }

    @Override
    public CommandSender getSender() {
        return Proxy.getConsole();
    }
    
    public void sendPM(User dest, String msg) {
        lastPenPal = dest.getName();
        msg("§e[§fВы §e-> §f" + dest.getName() + "§e] " + msg);
        dest.recievePM(this, msg);
    }
    
    @Override
    public void recievePM(User user, String msg) {
        if (!user.getName().equals(lastPenPal)) {
            user.msg("§eКонсоль усердно трудится, чтобы сервер работал, не отвлекай её!");
            return;
        }
        lastPenPal = user.getName();
        msg("§e[§f" + user.getName() + "§e -> §fВы§e] " + msg);
    }
    
    @Override
    public String getLastPenPal() {
        return lastPenPal;
    }
}
