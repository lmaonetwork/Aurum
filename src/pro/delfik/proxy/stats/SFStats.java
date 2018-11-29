package pro.delfik.proxy.stats;

import implario.util.ByteUnzip;
import implario.util.ByteZip;
import implario.util.Converter;

public class SFStats implements GameStats{
    private String name;
    private int games, wins, beds, deaths;

    public SFStats(ByteUnzip unzip){
        games = unzip.getInt();
        wins = unzip.getInt();
        beds = unzip.getInt();
        deaths = unzip.getInt();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SFStats && ((SFStats) o).name.equals(name);
    }

    @Override
    public String toString() {
        return name + " " + wins + " " + games;
    }

    @Override
    public String[] toReadableString() {
        return new String[]{
                "§a\u1405 §f" + beds + "§a кроват" + Converter.plural(beds, "ь", "и", "ей"),
                "§a\u1405 §f" + deaths + "§a смерт" + Converter.plural(deaths, "ь", "и", "ей"),
                "§a\u1405 §f" + games + "§a игр" + Converter.plural(games, "а", "ы", "") + " сыграно",
                "§a\u1405 §f" + wins + "§a побед" + Converter.plural(wins, "а", "ы", "")
        };
    }

    @Override
    public void add(GameStats game) {
        if(!(game instanceof SFStats))return;
        SFStats stats = (SFStats)game;
        games = games + stats.games;
        wins = wins + stats.wins;
        beds = beds + stats.beds;
        deaths = beds + stats.deaths;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ByteZip toByteZip() {
        return new ByteZip().add(games).add(wins).add(beds).add(deaths);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int criteria() {
        return wins;
    }

	@Override
	public int earnedCoins() {
		int i = wins * 200 + games * 35 + beds * 7 - deaths;
		return i < 0 ? 0 : i;
	}

}
