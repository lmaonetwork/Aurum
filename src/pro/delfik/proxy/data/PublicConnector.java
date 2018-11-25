package pro.delfik.proxy.data;

import __google_.crypt.async.RSA;
import __google_.crypt.async.SignedRSA;
import __google_.net.server.Server;
import __google_.util.Byteable;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.module.Unloadable;

public class PublicConnector implements Registeable, Unloadable {
    private static Server server;

    @Override
    public void register(){
        server = new Server(1424);
        server.setCertificate(Byteable.toByteable(DataIO.readBytes("config/signed.certificate"), SignedRSA.class),
                                Byteable.toByteable(DataIO.readBytes("config/rsa.key"), RSA.class));
        server.addExec(0, new ExecLog());
        server.addExec(2, new ExecStartLog());
        server.addExec(1, new ExecReg());
    }

    @Override
    public void unload(){
        server.close();
    }
}
