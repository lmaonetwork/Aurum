package pro.delfik.proxy.stats;

import implario.net.Packet;
import implario.net.packet.PacketCreateTop;
import implario.util.Exceptions;
import implario.util.Scheduler;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.data.Server;
import pro.delfik.proxy.module.Registeable;

import java.security.cert.Extension;

public class StatsThread implements Runnable, Registeable {
    @Override
    public void register() {
        Proxy.i().getScheduler().runAsync(Aurum.instance, this);
    }

    @Override
    public void run(){
        while (true){
            Exceptions.runThrowsEx(StatsThread::execute);
            Scheduler.sleep(120_000);
        }
    }

    public static void execute(){
        Exceptions.runThrowsEx(StatsThread::updateTop);
    }

    private static void updateTop(){
        Top.iterable().forEach(StatsThread::updateTop);
    }

    private static void updateTop(Top top){
        Packet packet = new PacketCreateTop(top.generateTop());
        for(Server server : Server.getServers()) {
            if (server.getType() == top.getType()) server.send(packet);
        }
    }
}
