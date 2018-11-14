package pro.delfik.proxy.stats;

import implario.net.packet.PacketTopUpdate;
import implario.util.ArrayUtils;
import implario.util.Byteable;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.User;
import pro.delfik.proxy.data.DataIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Top implements Byteable{
    private GameStats objects[];

    private final Class<? extends GameStats> clazz;
    private final String name;

    public Top(int size, String name, Class<? extends GameStats> clazz){
        objects = new GameStats[size];
        this.name = name;
        this.clazz = clazz;
        Aurum.addUnload(this::unload);
        tops.put(name, this);
        List<String> list = DataIO.read(getPath());
        if(list == null)return;
        for(String str : list) {
            if(str.equals("a"))continue;
            GameStats stats = read(str);
            if(stats == null)continue;
            updateTop(stats);
        }
    }

    public void update(PacketTopUpdate update) {
        GameStats stats = Byteable.toByteable(update.getUpdate(), clazz);
        stats.setName(update.getNick());
        GameStats read = read(update.getNick());
        if(read != null)stats.add(read);
        save(stats);
        updateTop(stats);
    }

    private void updateTop(GameStats stats){
        int index = indexOf(stats.getName());
        if (index != -1) {
            if (index != 0) {
                while (index != 0) {
                    GameStats p = objects[index - 1];
                    if (stats.criteria() <= p.criteria()) {
                        objects[index] = stats;
                        break;
                    }
                    objects[index] = p;
                    objects[index - 1] = stats;
                    --index;
                }
            } else {
                objects[index] = stats;
            }
        } else {
            for (int i = 0; i < objects.length; ++i) {
                GameStats toper = objects[i];
                if (toper == null) {
                    objects[i] = stats;
                    return;
                }

                if (stats.criteria() > toper.criteria()) {
                    addTop(stats, i);
                    return;
                }
            }
        }
    }

    public void save(GameStats object){
        DataIO.writeByteable(getPath(object.getName()), object);
    }

    public void unload(){
        List<String> list = new ArrayList<>();
        for(GameStats object : objects)
            list.add(object == null ? "a" : object.getName());
        DataIO.write(getPath(), list);
    }

    public GameStats read(String nick){
        GameStats stats = DataIO.readByteable(getPath(nick), clazz);
        if(stats == null)return null;
        stats.setName(nick);
        return stats;
    }

    public String generateTop(){
        StringBuilder buffer = new StringBuilder();
        for(GameStats stats : objects)
            buffer.append(stats == null ? "null" : stats.toString()).append('\n');
        return buffer.toString();
    }

    private String getPath(String nick){
        return User.getPath(nick) + name + "_top";
    }

    private String getPath(){
        return "top/" + name;
    }

    private int indexOf(String indexOf) {
        for(int i = 0; i < objects.length; i++) {
            GameStats object = objects[i];
            if(object == null || object.getName() == null)continue;
            if(object.getName().equalsIgnoreCase(indexOf))return i;
        }
        return -1;
    }

    private void addTop(GameStats player, int top) {
        objects = (GameStats[]) ArrayUtils.arrayShift(objects, top, player, new GameStats[objects.length]);
    }

    private static Map<String, Top> tops = new HashMap<>();

    public static Top get(String name){
        return tops.get(name);
    }

    static{
        new Top(15, "SF", SFStats.class);
    }
}
