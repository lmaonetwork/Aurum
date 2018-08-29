package pro.delfik.proxy.cmd.ex;

import implario.util.Rank;

public class ExNotEnoughPermissions extends ExCustom {
	public ExNotEnoughPermissions(Rank required) {
		super("§cДля этого действия необходим статус §e" + required);
	}
}
