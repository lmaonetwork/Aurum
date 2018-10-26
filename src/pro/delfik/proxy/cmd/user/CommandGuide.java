package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.User;
import implario.util.Rank;

@Cmd(args = 1, help = "[a]")
public class CommandGuide extends Command{
	public CommandGuide() {
		super("guide", Rank.PLAYER, "Туториалы по разным вещам");
	}

	@Override
	protected void run(User user, String[] args) {
		if (args[0].charAt(0) == 'a') {
			user.msg("§eЗдесь будет гайд."); // TODO: Написать гайд.
		}
	}
}
