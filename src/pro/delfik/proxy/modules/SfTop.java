package pro.delfik.proxy.modules;

import implario.net.packet.PacketTop;
import implario.net.packet.PacketUpdateTop;
import implario.util.ArrayUtils;
import implario.util.Converter;
import implario.util.ManualByteUnzip;
import implario.util.ManualByteZip;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.User;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Server;

import java.util.ArrayList;
import java.util.List;

public class SfTop extends PacketTop.Top {

	public static final String path = "sf_top";

	private static SfTop[] top = new SfTop[15];
	private int games;
	private int wins;
	private int beds;
	private int deaths;
	private String nick;
	
	private SfTop(String nick, int games, int wins, int beds, int deaths) {
		super(nick, wins, beds);
		this.nick = nick;
		this.games = games;
		this.wins = wins;
		this.beds = beds;
		this.deaths = deaths;
	}

	private SfTop(String nick){
		this(nick, 0, 0, 0, 0);
	}

	static{
		List<String> in = DataIO.read("top/sf");
		if (in != null) for (String top : in) checkTop(getPerson(top));
		Aurum.addUnload(() -> {
			List<String> write = new ArrayList<>();
			for (SfTop top : top) if (top != null) write.add(top.nick);
			DataIO.write("top/sf", write);
		});
	}
	
	public static void checkTop(SfTop player) {
		if (player != null) {
			int index = indexOf(player.nick);
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
		if(Server.get("SF_1") == null)return;
		PacketTop.Top tops[] = new PacketTop.Top[top.length];
		for(int i = 0; i < top.length; i++)
			tops[i] = top[i] == null ? new PacketTop.Top("null", 0, 0) : top[i].getTop();
		PacketTop packet = new PacketTop(tops);
		Server.get("SF_1").send(packet);
	}
	
	private static void addTop(SfTop player, int top) {
		SfTop.top = (SfTop[]) ArrayUtils.arrayShift(SfTop.top, top, player, new SfTop[SfTop.top.length]);
	}

	public static void updateTop(PacketUpdateTop packet){
		SfTop top = getPerson(packet.getNick());
		if(top == null)top = new SfTop(packet.getNick());
		top.beds = top.beds + packet.getBeds();
		top.deaths = top.deaths + packet.getDeaths();
		top.games = top.games + 1;
		top.wins = top.wins + (packet.isWin() ? 1 : 0);
		DataIO.writeBytes(User.getPath(packet.getNick()) + path, new ManualByteZip()
				.add(top.games).add(top.wins).add(top.beds).add(top.deaths).build());
		checkTop(top);
	}
	
	public static SfTop getPerson(String nick) {
		byte[] array = DataIO.readBytes(User.getPath(nick) + path);
		if(array == null || array.length == 0){
			List<String> list = DataIO.read(User.getPath(nick) + "sf");
			if(list == null) return null;
			int games = Converter.toInt(list.get(0));
			int wins = Converter.toInt(list.get(1));
			int beds = Converter.toInt(list.get(2));
			int deaths = Converter.toInt(list.get(3));
			return games != -1 && wins != -1 && beds != -1 && deaths != -1 ? new SfTop(nick, games, wins, beds, deaths) : null;
		}else{
			ManualByteUnzip unzip = new ManualByteUnzip(array);
			return new SfTop(nick, unzip.getInt(), unzip.getInt(), unzip.getInt(), unzip.getInt());
		}
	}

	@Override
	public String toString() {
		return nick + "-" + games + "-" + wins + "-" + beds + "-" + deaths;
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

	public PacketTop.Top getTop(){
		return new PacketTop.Top(nick, wins, games);
	}

	private static boolean contains(String contains) {
		int i = indexOf(contains);
		return i != 1;
	}

	public static int indexOf(String indexOf) {
		for(int i = 0; i < top.length; i++) {
			SfTop top = SfTop.top[i];
			if(top == null)continue;
			if(top.nick.equalsIgnoreCase(indexOf))return i;
		}
		return -1;
	}
}
