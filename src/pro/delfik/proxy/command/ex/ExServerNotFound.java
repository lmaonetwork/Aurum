package pro.delfik.proxy.command.ex;

public class ExServerNotFound extends ExCustom {
	public ExServerNotFound(String server) {
		super("§сСервер с названием §f" + server + "§c не найден.");
	}
}
