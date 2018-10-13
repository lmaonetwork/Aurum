package __google_.net.client;

import java.io.IOException;
import java.net.Socket;

public interface NetClientCreator {
	NetClient create(Socket socket, Client client) throws IOException;
}
