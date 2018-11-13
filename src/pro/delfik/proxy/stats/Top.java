package pro.delfik.proxy.stats;

import implario.net.packet.PacketTopUpdate;
import implario.util.ArrayUtils;
import implario.util.Byteable;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.User;
import pro.delfik.proxy.data.DataIO;

import java.util.ArrayList;
import java.util.List;

public class Top implements Byteable{
    private GameStats objects[];

    private final Class<? extends GameStats> clazz;
    private final String name;

    public Top(int size, String name, Class<? extends GameStats> clazz){
        objects = new GameStats[size];
        this.name = name;
        this.clazz = clazz;
        Aurum.addUnload(this::unload);
        List<String> list = DataIO.read(getPath());
        if(list == null)return;
        for(int i = 0; i < objects.length; i++)
            objects[i] = read(list.get(i));
    }

    public void update(PacketTopUpdate update) {
        GameStats stats = Byteable.toByteable(update.getUpdate(), clazz);
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
        return DataIO.readByteable(getPath(nick), clazz);
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
            if(object == null)continue;
            if(object.getName().equalsIgnoreCase(indexOf))return i;
        }
        return -1;
    }

    private void addTop(GameStats player, int top) {
        objects = (GameStats[]) ArrayUtils.arrayShift(objects, top, player, new GameStats[objects.length]);
    }
}
