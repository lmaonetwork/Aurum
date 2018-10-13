package __google_.net;

import __google_.crypt.Crypt;
import __google_.util.Exceptions;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public interface NetWorker {

	/*
	 * Write in memory NetWorker
	 */
	void read() throws IOException;

	void close() throws IOException;

	default void closeOutException(){
		Exceptions.runThrowsEx(this::close);
	}

	void write() throws IOException;

	Socket socket();

	Response response();

	void setResponse(Response response);

	Flags flags();

	void setFlags(Flags flags);

	boolean connected();

	Crypt crypt();

	void setCrypt(Crypt crypt);

	boolean onlyEncrypt();

	void onlyEncrypt(boolean onlyEncrypt);

	void postWrite(Consumer<NetWorker> consumer);
}
