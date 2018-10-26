package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Cmd;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.User;
import implario.util.Converter;
import implario.util.Rank;

@Cmd(args = 2, help = "[Игрок] [Сообщение]")
public class CmdTell extends Command{
	public CmdTell(){
		super("tell", Rank.PLAYER, "Личное сообщение игроку", "t", "w", "pm", "msg", "m");
	}

	@Override
	protected void run(User user, String[] args) {
		User dest = requirePerson(args[0]);
		String msg = Converter.mergeArray(args, 1, " ");
		user.sendPM(dest, msg);
	}
}
