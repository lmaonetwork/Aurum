package pro.delfik.proxy.stats;

import implario.util.ByteUnzip;
import implario.util.ByteZip;
import implario.util.Converter;

public class SpleefStats implements GameStats {

	private String name;
	private int games, wins;
	private int destroyed;

	public SpleefStats(ByteUnzip unzip) {
		games = unzip.getInt();
		wins = unzip.getInt();
		if (unzip.next()) destroyed = unzip.getInt();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SpleefStats && ((SpleefStats) o).name.equals(name);
	}

	@Override
	public String toString() {
		return name + " " + wins + " " + games;
	}

	@Override
	public String[] toReadableString() {
		return new String[] {
				"§a\u1405 §f" + games + "§a игр" + Converter.plural(games, "а", "ы", "") + " сыграно",
				"§a\u1405 §f" + wins + "§a побед" + Converter.plural(wins, "а", "ы", ""),
				"§a\u1405 §f" + destroyed + "§a блок" + Converter.plural(destroyed, "", "а", "ов") + " сломано"
		};
	}

	@Override
	public int earnedCoins() {
		return wins * 40 + (destroyed >> 2);
	}

	@Override
	public void add(GameStats game) {
		if (!(game instanceof SpleefStats)) return;
		SpleefStats stats = (SpleefStats) game;
		games += stats.games;
		wins += stats.wins;
		destroyed += stats.destroyed;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ByteZip toByteZip() {
		return new ByteZip().add(games).add(wins).add(destroyed);
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int criteria() {
		return wins;
	}

}
