package pro.delfik.proxy.cmd.ex;

public class ExUserNotFound extends ExCustom {
	public ExUserNotFound(String username) {
		super("§cИгрок с ником §f" + username + "§c не найден.");
	}
}
