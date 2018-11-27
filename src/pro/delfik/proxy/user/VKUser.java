package pro.delfik.proxy.user;

import implario.util.Rank;
import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class VKUser extends AUser{
    private final List<String> cache = new ArrayList<>();
    private final String name;

    public VKUser(int id){
        name = id + "\u3333";
        User.put(name, this);
    }

    @Override
    public void msg(Object... o) {
        for(Object object : o)
            cache.add(object.toString());
    }

    @Override
    public CommandSender getSender() {
        return new VKSender();
    }

    @Override
    public Rank getRank() {
        return Rank.DEV;
    }

    @Override
    public void unload() {
        User.remove(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

    private class VKSender extends ASender {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void sendMessage(String s) {
            cache.add(s);
        }
    }

    public List<String> getCache() {
        return cache;
    }
}
