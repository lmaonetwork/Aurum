package pro.delfik.proxy.module;

import implario.util.ByteUnzip;
import implario.util.ByteZip;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.module.Unloadable;
import pro.delfik.vk.VK;

import java.util.ArrayList;
import java.util.List;

public class Admin implements Registeable, Unloadable {
    private static List<Integer> ids = new ArrayList<>();

    public static boolean is(int id){
        return ids.contains(id);
    }

    public static void add(int id){
        ids.add(id);
    }

    public static void remove(int id){
        ids.remove((Object)id);
    }

    public static Iterable<Integer> iterable(){
        return ids;
    }

    @Override
    public void register() {
        add(VK.getID("6ooogle"));
    }

    @Override
    public void unload() {
        ByteZip zip = new ByteZip().add(ids.size());
        ids.forEach(zip::add);
        DataIO.writeBytes("config/vk_admins", zip.build());
    }
}
