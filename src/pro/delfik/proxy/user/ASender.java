package pro.delfik.proxy.user;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Collection;

public abstract class ASender implements CommandSender {
    @Override
    public void sendMessages(String... strings) {
        for(String str : strings)
            sendMessage(str);
    }

    @Override
    public void sendMessage(BaseComponent... message) {
        sendMessage(BaseComponent.toLegacyText(message));
    }

    @Override
    public void sendMessage(BaseComponent message) {
        sendMessage(message.toLegacyText());
    }

    @Override
    public Collection<String> getGroups() {
        return null;
    }

    @Override
    public void addGroups(String... strings) {

    }

    @Override
    public void removeGroups(String... strings) {

    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public void setPermission(String s, boolean b) {

    }

    @Override
    public Collection<String> getPermissions() {
        return null;
    }
}
