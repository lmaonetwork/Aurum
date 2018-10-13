package pro.delfik.proxy.data;

import __google_.crypt.async.RSA;
import __google_.net.Response;
import __google_.net.server.NetServer;
import __google_.net.server.exec.Exec;
import __google_.util.ByteUnzip;

public class ExecReg implements Exec {
    @Override
    public void accept(NetServer server) {
        ByteUnzip unzip = new ByteUnzip(server.response().getContent());
        String nick = unzip.getString();
        byte rsaKey[] = unzip.getBytes();
        if(rsaKey.length != 294 || new RSA(rsaKey) == null || DataIO.getFile("players/" + nick).exists())return;
        DataIO.writeBytes("players/" + nick + "/public.key", rsaKey);
        server.setResponse(new Response(0));
    }
}
