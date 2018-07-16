package pro.delfik.proxy.command;

public class ServerNotFoundException extends RuntimeException {
	public ServerNotFoundException(String server) {
		super("§сСервер с названием §f" + server + "§c не найден.");
	}
}
