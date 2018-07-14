package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.command.CustomException;
import pro.delfik.proxy.games.SfTop;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

public class CommandStats extends Command{
	public CommandStats() {
		super("stats", Rank.PLAYER, "Просмотр статистики");
	}

	@Override
	protected void run(CommandSender sender, String[] args) {
		SfTop top = SfTop.getPerson(sender.getName());
		if (top == null) throw new CustomException("§eТы ещё никогда не играл в §fMLGRush§e. Самое время это исправить!");
		U.msg(sender, "§e\u2b26 Статистика по §fMLGRush §e\u2b26");
		int beds = top.getBeds(), deaths = top.getDeaths(), games = top.getGames(), wins = top.getWins();
		U.msg(sender, "§a\u1405 §f" + beds + "§a кроват" + Converter.plural(beds, "ь", "и", "ей"));
		U.msg(sender, "§a\u1405 §f" + deaths + "§a смерт" + Converter.plural(deaths, "ь", "и", "ей"));
		U.msg(sender, "§a\u1405 §f" + games + "§a игр" + Converter.plural(games, "а", "ы", "") + " сыграно");
		U.msg(sender, "§a\u1405 §f" + wins + "§a побед" + Converter.plural(wins, "а", "ы", ""));
	}
}
