package pro.delfik.proxy.user;

import net.md_5.bungee.api.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VKUser extends AUser{
    private static final Pattern pattern = Pattern.compile("§.");

    private final List<String> cache = new ArrayList<>();
    private final String name;

    public VKUser(int id){
        name = id + "\u3333";
        User.put(name, this);
    }

    @Override
    public CommandSender getSender() {
        return new VKSender();
    }

    @Override
    public String getName() {
        return name;
    }

    private class VKSender extends ASender {
        @Override
        public String getName() {
            return name;
        }

        @Override
        public void sendMessage(String s) {
            cache.add(pattern.matcher(s).replaceAll(""));
        }
    }

    public List<String> getCache() {
        return cache;
    }
}
