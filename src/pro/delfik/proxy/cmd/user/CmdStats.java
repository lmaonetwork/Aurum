package pro.delfik.proxy.cmd.user;

import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.ex.ExCustom;
import pro.delfik.proxy.user.SfTop;
import pro.delfik.proxy.user.User;
import implario.util.Rank;
import implario.util.Converter;

public class CmdStats extends Command{
	public CmdStats() {
		super("stats", Rank.PLAYER, "Просмотр статистики");
	}

	@Override
	protected void run(User user, String args[]) {
		SfTop top = SfTop.getPerson(user.getName());
		if (top == null) throw new ExCustom("§eТы ещё никогда не играл в §fMLGRush§e. Самое время это исправить!");
		user.msg("§e\u2b26 Статистика по §fMLGRush §e\u2b26");
		int beds = top.getBeds(), deaths = top.getDeaths(), games = top.getGames(), wins = top.getWins();
		user.msg("§a\u1405 §f" + beds + "§a кроват" + Converter.plural(beds, "ь", "и", "ей"));
		user.msg("§a\u1405 §f" + deaths + "§a смерт" + Converter.plural(deaths, "ь", "и", "ей"));
		user.msg("§a\u1405 §f" + games + "§a игр" + Converter.plural(games, "а", "ы", "") + " сыграно");
		user.msg("§a\u1405 §f" + wins + "§a побед" + Converter.plural(wins, "а", "ы", ""));
	}
}
