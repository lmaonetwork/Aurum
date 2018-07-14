package pro.delfik.proxy.command;

public class ServerNotFoundException extends RuntimeException {
	private final String server;
	public ServerNotFoundException(String arg) {
		this.server = arg;
	}
	
	public String getServer() {
		return server;
	}
}
