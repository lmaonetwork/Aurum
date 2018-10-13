package __google_.net.server;

import __google_.crypt.Crypt;

import java.io.IOException;
import java.net.Socket;

public interface NetServerCreator{
    NetServer create(Socket socket, Crypt crypt, Server server) throws IOException;
}