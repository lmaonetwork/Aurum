package pro.delfik.proxy.data;

import __google_.net.Response;
import __google_.net.server.NetServer;
import __google_.net.server.exec.Exec;
import __google_.util.ByteUnzip;
import pro.delfik.proxy.User;

import java.util.Arrays;

public class ExecLog implements Exec {
    @Override
    public void accept(NetServer server) {
        System.out.println("log.accept() вызван.");
        ByteUnzip unzip = new ByteUnzip(server.response().getContent());

        String nick = unzip.getString();
        byte decoded[] = unzip.getBytes();
        byte privated[] = ExecStartLog.bytes.get(nick);
        System.out.println(nick + " - " + decoded.length + " - " + privated.length);
        if(privated != null && Arrays.equals(decoded, privated)){
            User.allowedIP.add(nick.toLowerCase());
            ExecStartLog.bytes.remove(nick);
            server.setResponse(new Response(0));
            System.out.println("OK!");
        }
    }
}
