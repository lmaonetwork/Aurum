package pro.delfik.proxy.command;

import pro.delfik.proxy.permissions.Rank;

public class NotEnoughPermissionsException extends RuntimeException {
	public NotEnoughPermissionsException(Rank required) {
		super("§cДля этого действия необходим статус §e" + required);
	}
}
