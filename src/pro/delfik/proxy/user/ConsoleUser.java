package pro.delfik.proxy.user;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.module.Registeable;

public class ConsoleUser extends AUser implements Registeable {
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
}
