package __google_.net.client;

import __google_.net.Flags;
import __google_.net.NetWorker;
import __google_.net.Response;

import java.io.IOException;

public interface NetClient extends NetWorker {
	default Response apply(Response response, Flags flags) throws IOException{
		setFlags(flags);
		setResponse(response);
		write();
		read();
		return response();
	}
}
