package __google_.net.server;

import __google_.crypt.Crypt;
import __google_.crypt.async.RSA;
import __google_.crypt.async.SignedRSA;
import __google_.net.server.exec.Exec;
import __google_.net.server.exec.ExecAES;
import __google_.net.server.exec.ExecRSA;
import __google_.util.Exceptions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread{
    private final Map<Byte, Exec> map = new HashMap<>();

    private final int port;
    private final NetServerCreator worker;
    private Crypt crypt;
    private ServerSocket listn;
    private boolean close = false;

    public Server(int port, Crypt crypt, NetServerCreator worker){
        this.port = port;
        this.crypt = crypt;
        this.worker = worker;
        start();
    }

    public Server(int port, Crypt crypt){
        this(port, crypt, Listener::new);
    }

    public Server(int port){
        this(port, null);
    }

    @Override
    public void run(){
        try{
            listn = new ServerSocket(port);
            while (!close){
                Socket socket = listn.accept();
                if(socket == null) continue;
                try{
                    worker.create(socket, crypt, this);
                }catch (IOException ex){
                    //Client not connected
                    ex.printStackTrace();
                }
            }
        }catch (IOException ex){
            if(!close)//Closed not with method close()
                ex.printStackTrace();
            //Server closed
        }
    }

    public void close(){
        close = true;
        Exceptions.runThrowsEx(listn::close);
    }

    public void addExec(int type, Exec exec){
        map.put((byte)type, exec);
    }

    public Exec getExec(int type){
        return map.get((byte)type);
    }

    public void setCertificate(SignedRSA signed, RSA rsa){
        addExec(126, new ExecRSA(signed));
        addExec(125, new ExecAES());
        crypt = rsa;
    }
}
