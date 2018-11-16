package pro.delfik.proxy.stats;

import implario.net.Packet;
import implario.net.packet.PacketCreateTop;
import implario.util.Scheduler;
import pro.delfik.proxy.data.Server;

public class StatsThread implements Runnable{
    @Override
    public void run(){
        while (true){
            execute();
            Scheduler.sleep(120_000);
        }
    }

    public static void execute(){
        for(Top top : Top.iterable()) {
            Packet packet = new PacketCreateTop(top.generateTop());
            for(Server server : Server.getServers()) {
                if (server.getType() == top.getType()) server.send(packet);
            }
        }
    }
}
