package pro.delfik.proxy.command.ex;

import pro.delfik.util.Rank;

public class ExNotEnoughPermissions extends ExCustom {
	public ExNotEnoughPermissions(Rank required) {
		super("§cДля этого действия необходим статус §e" + required);
	}
}
