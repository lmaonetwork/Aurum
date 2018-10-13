package __google_.net.server.exec;

import __google_.crypt.async.SignedRSA;
import __google_.net.Response;
import __google_.net.server.NetServer;

public class ExecRSA implements Exec{
    private final Response signed;

    public ExecRSA(SignedRSA signed){
        this.signed = new Response(0, signed.toBytes());
    }

    @Override
    public void accept(NetServer server) {
        server.setResponse(signed);
    }
}
