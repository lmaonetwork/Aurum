package __google_.net.server.exec;

import __google_.util.FileIO;
import __google_.net.Response;
import __google_.net.server.NetServer;
import __google_.util.ByteUnzip;

public class ExecWriteFile implements Exec{
	@Override
	public void accept(NetServer server) {
		ByteUnzip unzip = new ByteUnzip(server.response().getContent());
		FileIO.writeBytes(unzip.getString(), unzip.getBytes());
		server.setResponse(new Response(0));
	}
}
