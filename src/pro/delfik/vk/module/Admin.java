package pro.delfik.vk.module;

import implario.util.ByteUnzip;
import implario.util.ByteZip;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.module.Unloadable;

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
        byte read[] = DataIO.readBytes("config/vk_admins");
        if(read == null)return;
        ByteUnzip unzip = new ByteUnzip(read);
        int admins = unzip.getInt();
        for(int i = 0; i < admins; i++)
            ids.add(unzip.getInt());
    }

    @Override
    public void unload() {
        ByteZip zip = new ByteZip().add(ids.size());
        ids.forEach(zip::add);
        DataIO.writeBytes("config/vk_admins", zip.build());
    }
}
