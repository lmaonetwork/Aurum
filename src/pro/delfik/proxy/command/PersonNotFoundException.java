package pro.delfik.proxy.command;

public class PersonNotFoundException extends CustomException {
	public PersonNotFoundException(String username) {
		super("§cИгрок с ником §f" + username + "§c не найден.");
	}
}
