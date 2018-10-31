package pro.delfik.vk;

import pro.delfik.vk.cmd.CmdAdmin;
import pro.delfik.vk.cmd.CmdAurum;
import pro.delfik.vk.cmd.CmdOnline;
import pro.delfik.vk.cmd.Command;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler {
	private static final Map<String, Command> cmds = new HashMap<>();

	static{
		cmds.put("\uD83C\uDF6A", (args, id) -> "Вкусная печенька, спасибо!");
		cmds.put("!онлайн", new CmdOnline());
		cmds.put("!admin", new CmdAdmin());
		cmds.put("!id", (args, id) -> "Your id " + id);
		cmds.put("!aurum", new CmdAurum());
	}

	public static String handle(String text, int from_id, long peer_id) {
		String split[] = text.split(" ");
		String key = split[0];
		for(Map.Entry<String, Command> entry : cmds.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(key)) {
				if (split.length == 1) return entry.getValue().exec(new String[]{}, from_id);
				String args[] = new String[split.length - 1];
				System.arraycopy(split, 1, args, 0, args.length);
				return entry.getValue().exec(args, from_id);
			}
		}
		return "";
	}

	public static void add(String name, Command command){
		cmds.put(name, command);
	}
}
