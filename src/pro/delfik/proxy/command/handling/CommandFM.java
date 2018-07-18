package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.util.Rank;

import java.util.HashMap;

public class CommandFM extends Command{
	public CommandFM(String command, String description, String... aliases){
		super(command, Rank.RECRUIT, description, aliases);
	}
	
	private static final HashMap<String, Rule> rules = new HashMap<>();

	static {
		rules.put("osk", new Rule("Оскорбление", 240));
		rules.put("flood", new Rule("Флуд", 30));
		rules.put("mt", new Rule("Мат", 60));
		rules.put("caps", new Rule("Капс", 30));
		rules.put("amoral", new Rule("Аморал", 120));
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		requireArgs(args, 1, "[Игрок]");
		String command = getCommand();
		Rule rule = rules.get(command);
		Mutes.mute(args[0], rule.reason, rule.time, sender.getName());
	}
	
	private static class Rule {
		private final String reason;
		private final int time;
		
		public Rule(String reason, int time) {
			this.reason = reason;
			this.time = time;
		}
	}
}
