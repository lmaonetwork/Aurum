package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.User;
import implario.util.Rank;

public class CmdHub extends Command {
	public CmdHub() {
		super("hub", Rank.PLAYER, "Телепортация в лобби.", "h");
	}

	@Override
	protected void run(User user, String args[]) {
		user.getHandle().connect(Proxy.getServer("LOBBY_1"));
		user.msg("§aДобро пожаловать в @§fLOBBY_1§a.");
	}
}
