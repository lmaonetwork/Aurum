package __google_.net.client;

import __google_.crypt.Crypt;
import __google_.net.CSSystem;

import java.io.IOException;
import java.net.Socket;

public class Connector extends CSSystem implements NetClient {
	private final Client client;

	public Connector(Socket socket, Client client) throws IOException {
		super(socket);
		this.client = client;
	}

	@Override
	public Crypt crypt() {
		return client.getCrypt();
	}

	@Override
	public void setCrypt(Crypt crypt) {
		client.setCrypt(crypt);
	}
}
