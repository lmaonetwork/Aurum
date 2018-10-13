package __google_.net.server.exec;

import __google_.util.FileIO;
import __google_.net.Response;
import __google_.net.server.NetServer;
import __google_.util.ByteUnzip;

public class ExecReadFile implements Exec{
	@Override
	public void accept(NetServer server) {
		server.setResponse(new Response(0, FileIO.readBytes(
				new ByteUnzip(server.response().getContent()).getString())));
	}
}
