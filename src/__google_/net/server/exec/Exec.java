package __google_.net.server.exec;

import __google_.net.server.NetServer;

import java.io.IOException;

public interface Exec {
	void accept(NetServer netServer) throws IOException;
}

