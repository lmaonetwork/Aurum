package __google_.net.server.exec;

import __google_.crypt.async.RSA;
import __google_.crypt.sync.AES;
import __google_.net.Flags;
import __google_.net.server.NetServer;

public class ExecAES implements Exec{
    @Override
    public void accept(NetServer server) {
        if(!(server.crypt() instanceof RSA) || !server.flags().isCrypt())return;
        server.setCrypt(new AES(server.response().getContent()));
        server.setFlags(new Flags(false));
        server.postWrite((s) -> s.onlyEncrypt(true));
    }
}
