package pro.delfik.proxy.data;

import __google_.crypt.async.RSA;
import __google_.net.Response;
import __google_.net.server.NetServer;
import __google_.net.server.exec.Exec;
import __google_.util.ByteUnzip;
import pro.delfik.util.TimedHashMap;

import java.security.SecureRandom;
import java.util.Map;

public class ExecStartLog implements Exec {
    static Map<String, byte[]> bytes = new TimedHashMap<>(10);

    @Override
    public void accept(NetServer server) {
        ByteUnzip unzip = new ByteUnzip(server.response().getContent());
        SecureRandom random = new SecureRandom();
        String nick = unzip.getString();
        byte rsa[] = DataIO.readBytes("players/" + nick.toLowerCase() + "/public.key");
        if(rsa == null)return;
        RSA key = new RSA(rsa);
        byte generated[] = new byte[128];
        random.nextBytes(generated);
        bytes.put(nick, generated);
        server.setResponse(new Response(0, key.encodeByte(generated)));
    }
}
