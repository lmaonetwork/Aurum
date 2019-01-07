package pro.delfik.proxy.stats;

import implario.net.packet.PacketTopUpdate;
import implario.util.ArrayUtils;
import implario.util.Byteable;
import implario.util.ServerType;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.user.User;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.module.Unloadable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Top implements Byteable, Unloadable, Registeable {
    private GameStats objects[];

    private final Class<? extends GameStats> clazz;
    private final ServerType type;

    public Top(int size, ServerType type, Class<? extends GameStats> clazz){
        objects = new GameStats[size];
        this.type = type;
        this.clazz = clazz;
        Aurum.register(this);
        tops.put(type, this);
    }

    public void update(PacketTopUpdate update) {
        GameStats stats = Byteable.toByteable(update.getUpdate(), clazz);
        stats.setName(update.getNick());
        GameStats read = read(update.getNick());
        User u = User.get(stats.getName());
        if (u != null) {
        	int earned = stats.earnedCoins();
        	u.earn(earned);
        	u.msg("§eЗачислено §f" + earned + "§e соларита.");
		}
        if(read != null) stats.add(read);
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

    @Override
    public void unload(){
        List<String> list = new ArrayList<>();
        for(GameStats object : objects)
            list.add(object == null ? "a" : object.getName());
        DataIO.write(getPath(), list);
    }

    @Override
    public void register(){
        objects = new GameStats[objects.length];
        List<String> list = DataIO.read(getPath());
        if(list == null)return;
        for(String str : list) {
            if(str.equals("a"))continue;
            GameStats stats = read(str);
            if(stats == null)continue;
            updateTop(stats);
        }
    }

    public GameStats read(String nick){
        GameStats stats = DataIO.readByteable(getPath(nick), clazz);
        if(stats == null)return null;
        stats.setName(nick);
        return stats;
    }

    public void remove(String nick){
        DataIO.remove(getPath(nick));
    }

    public ServerType getType() {
        return type;
    }

    public String[] generateTop(){
        String result[] = new String[objects.length];
        for(int i = 0; i < result.length; i++)
            result[i] = objects[i] + "";
        return result;
    }

    private String getPath(String nick){
        return User.getPath(nick) + type.name() + "_top";
    }

    private String getPath(){
        return "top/" + type.name();
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

    private static Map<ServerType, Top> tops = new HashMap<>();

    public static Top get(ServerType type){
        return tops.get(type);
    }

    public static Iterable<Top> iterable(){
        return tops.values();
    }

    public static void init(){
        new Top(15, ServerType.SF, SFStats.class);
		new Top(15, ServerType.SPLEEF, SpleefStats.class);
    }
}
