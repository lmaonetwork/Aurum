package pro.delfik.proxy.games;

import pro.delfik.proxy.data.DataIO;
import pro.delfik.util.ArrayUtils;
import pro.delfik.util.Converter;

import java.util.ArrayList;
import java.util.List;

public class SfTop {
	private static SfTop[] top = new SfTop[15];
	private int games;
	private int wins;
	private int beds;
	private int deaths;
	private String nick;
	
	private SfTop(String nick, int games, int wins, int beds, int deaths) {
		this.nick = nick;
		this.games = games;
		this.wins = wins;
		this.beds = beds;
		this.deaths = deaths;
	}
	
	public String toString() {
		return this.nick + '}' + this.games + '}' + this.wins + '}' + this.beds + '}' + this.deaths;
	}
	
	public static String getAllTop() {
		StringBuilder sb = new StringBuilder();
		for (SfTop top : SfTop.top) sb.append(top).append('\n');
		return sb.toString();
	}
	
	public static void unload() {
		List<String> write = new ArrayList<>();
		for (SfTop top : top) if (top != null) write.add(top.nick);
		DataIO.write("top/sf", write);
	}
	
	public static void checkTop(String nick) {
		SfTop player = getPerson(nick);
		if (player != null) {
			int index = ArrayUtils.indexOf(top, player);
			if (index != -1) {
				if (index != 0) {
					while(index != 0) {
						SfTop p = top[index - 1];
						if (player.wins <= p.wins) {
							top[index] = player;
							break;
						}
						
						top[index] = p;
						top[index - 1] = player;
						--index;
					}
				} else {
					top[index] = player;
				}
			} else {
				for(int i = 0; i < top.length; ++i) {
					SfTop toper = top[i];
					if (toper == null) {
						top[i] = player;
						return;
					}
					
					if (player.wins > toper.wins) {
						addTop(player, i);
						return;
					}
				}
			}
			
		}
	}
	
	private static void addTop(SfTop player, int top) {
		SfTop.top = (SfTop[]) ArrayUtils.arrayShift(SfTop.top, top, player, new SfTop[SfTop.top.length]);
	}
	
	public static SfTop getPerson(String nick) {
		List<String> list = DataIO.read("players/" + nick + "/sf");
		if (list == null) {
			return null;
		} else {
			int games = Converter.toInt(list.get(0));
			int wins = Converter.toInt(list.get(1));
			int beds = Converter.toInt(list.get(2));
			int deaths = Converter.toInt(list.get(3));
			return games != -1 && wins != -1 && beds != -1 && deaths != -1 ? new SfTop(nick, games, wins, beds, deaths) : null;
		}
	}
	
	public boolean equals(Object object) {
		return object instanceof SfTop && ((SfTop)object).nick.equals(this.nick);
	}
	
	public int getGames() {
		return this.games;
	}
	
	public int getWins() {
		return this.wins;
	}
	
	public int getBeds() {
		return this.beds;
	}
	
	public int getDeaths() {
		return this.deaths;
	}
	
	static {
		List<String> in = DataIO.read("top/sf");
		if (in != null) for (String top : in) checkTop(top);
		
	}
}
