package pro.delfik.proxy.command;

import pro.delfik.util.Rank;

public class NotEnoughPermissionsException extends RuntimeException {
	public NotEnoughPermissionsException(Rank required) {
		super("§cДля этого действия необходим статус §e" + required);
	}
}
