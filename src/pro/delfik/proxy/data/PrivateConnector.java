package pro.delfik.proxy.data;

import __google_.net.Response;
import __google_.net.client.Client;
import __google_.net.server.Server;
import __google_.util.FileIO;
import implario.net.Connector;
import implario.net.NetListener;
import implario.net.Packet;
import implario.net.packet.PacketInit;
import implario.util.ByteUnzip;
import implario.util.ByteZip;
import implario.util.Coder;
import implario.util.Scheduler;
import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.ev.added.PacketEvent;

import java.io.IOException;
import java.net.Socket;

public class PrivateConnector implements NetListener {
    private static Connector listener;

    public static void init(){
        try {
            listener = new Connector(new Socket("localhost",
                    Coder.toInt(FileIO.read("/Minecraft/_GLOBAL/config.txt")
                            .split("\n")[0])),
                    new PrivateConnector());
            listener.write(new Response(0, new PacketInit("PROXY_1").zip()));
        }catch (Exception ex){
            throw new IllegalArgumentException(ex);
        }
    }

    public static void sendPacket(String server, Packet packet){
        if(!listener.connected()){
            Scheduler.sleep(1000);
            init();
        }
        try {
            listener.write(new Response(3, new ByteZip().add(server).add(packet.zip()).build()));
        }catch (Exception ex){
            Scheduler.sleep(1000);
            init();
            sendPacket(server, packet);
        }
    }

    public static void close(){
        listener.close();
    }

    private String server;

    @Override
    public void accept(Response response, Connector connector) {
        ByteUnzip unzip = new ByteUnzip(response.getContent());
        String server = unzip.getString();
        Packet packet = Packet.getPacket(unzip.getBytes());
        if(packet instanceof PacketInit){
            this.server = ((PacketInit) packet).getServer();
            new pro.delfik.proxy.data.Server(server);
        }
        BungeeCord.getInstance().getScheduler().runAsync(Aurum.instance, () -> BungeeCord.getInstance()
                .pluginManager.callEvent(new PacketEvent(server, packet)));
    }

    @Override
    public void closed() {
        pro.delfik.proxy.data.Server.removeServer(server);
    }
}
