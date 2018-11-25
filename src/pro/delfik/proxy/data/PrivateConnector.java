package pro.delfik.proxy.data;

import __google_.net.Response;
import __google_.net.server.Server;
import __google_.util.FileIO;
import implario.net.Connector;
import implario.net.NetListener;
import implario.net.Packet;
import implario.net.packet.PacketInit;
import implario.util.Coder;
import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.ev.added.PacketEvent;

public class PrivateConnector implements NetListener {
    private static Server listener;

    public static void init(){
        listener = new Server(Coder.toInt(FileIO.read("/Minecraft/_GLOBAL/config.txt").split("\n")[0]),
                null, (socket, a, b) -> new Connector(socket, new PrivateConnector()));

    }

    public static void close(){
        listener.close();
    }

    private String server;

    @Override
    public void accept(Response response, Connector connector) {
        Packet packet = Packet.getPacket(response.getContent());
        if(packet instanceof PacketInit){
            this.server = ((PacketInit) packet).getServer();
            new pro.delfik.proxy.data.Server(server, connector);
        }
        BungeeCord.getInstance().getScheduler().runAsync(Aurum.instance, () -> BungeeCord.getInstance()
                .pluginManager.callEvent(new PacketEvent(server, packet)));
    }

    @Override
    public void closed() {
        pro.delfik.proxy.data.Server.removeServer(server);
    }
}
