package __google_.net.client;

import __google_.crypt.Crypt;
import __google_.crypt.async.SignedRSA;
import __google_.crypt.sync.AES;
import __google_.net.Flags;
import __google_.net.Response;
import __google_.util.Byteable;
import __google_.util.Exceptions;

import java.net.Socket;

public class Client {
    private NetClient instance;

    private final String host;
    private final int port;
    private final NetClientCreator worker;
    private Crypt crypt;

    public Client(String host, int port, Crypt crypt, NetClientCreator worker){
        this.host = host;
        this.port = port;
        this.worker = worker;
        this.crypt = crypt;
    }

    public Client(String host, int port, Crypt crypt){
        this(host, port, crypt, Connector::new);
    }

    public Client(String host, int port){
        this(host, port, null);
    }

    public void close(){
        if(instance != null)instance.closeOutException();
    }

    public void connect() {
        instance = Exceptions.getThrowsEx(() -> worker.create(new Socket(host, port), this), false);
    }

    public synchronized Response apply(Response response, Flags flags){
        return Exceptions.getThrowsEx(() -> instance.apply(response, flags), false);
    }

    public Response apply(Response response){
        return apply(response, new Flags());
    }

    public void getCertificate(boolean always){
        Response key = apply(new Response(126), new Flags(false));
        SignedRSA signed = Byteable.toByteable(key.getContent(), SignedRSA.class);
        if(!always && !signed.checkCertificate() && !signed.existsHost(host))throw new IllegalArgumentException("Certificate not secure");
        crypt = signed.getRSA();
        AES local = new AES(32);
        System.out.println("Send AES key");
        apply(new Response(125, local.getByteKey()));
        crypt = local;
        instance.onlyEncrypt(true);
    }

    public void getCertificate(){
        getCertificate(false);
    }

    public boolean connected(){
        return instance != null && instance.connected();
    }

    public Crypt getCrypt() {
        return crypt;
    }

    public void setCrypt(Crypt crypt){
        this.crypt = crypt;
    }
}
