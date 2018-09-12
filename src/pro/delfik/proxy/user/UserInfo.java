package pro.delfik.proxy.user;

import implario.util.ByteUnzip;
import implario.util.ManualByteZip;
import implario.util.Rank;

import java.util.List;
import java.util.function.Function;

public class UserInfo {
	public int version;

	public String name, passhash, lastIP;
	public boolean ipAttached, pmDisabled, darkTheme;
	public Rank rank;
	public int online, money;
	public List<String> friends, ignored;

	public UserInfo(String name, String passhash, Rank rank, int online, String lastIP,
					int money, boolean ipAttached, boolean pmDisabled, List<String> ignored,
					List<String> friends, boolean darkTheme) {
		this.name = name;
		this.passhash = passhash;
		this.rank = rank;
		this.online = online;
		this.lastIP = lastIP;
		this.money = money;
		this.ipAttached = ipAttached;
		this.pmDisabled = pmDisabled;
		this.ignored = ignored;
		this.friends = friends;
		this.darkTheme = darkTheme;
	}

	public ManualByteZip zip() {
		return new ManualByteZip()
				.add(Version.last())
				.add(name)
				.add(passhash)
				.add(rank.getByte())
				.add(online)
				.add(lastIP)
				.add(money)
				.add(ipAttached)
				.add(pmDisabled)
				.add(ignored)
				.add(friends)
				.add(darkTheme);
	}

	public enum Version {
		V0(u -> new UserInfo(
				u.getString(),
				u.getString(),
				Rank.byChar.get((char) u.getByte()),
				u.getInt(),
				u.getString(),
				u.getInt(),
				u.getBoolean(),
				u.getBoolean(),
				u.getList(),
				u.getList(),
				false
		)),
		V1_DARKTHEME(u -> new UserInfo(
				u.getString(),
				u.getString(),
				Rank.byChar.get((char) u.getByte()),
				u.getInt(),
				u.getString(),
				u.getInt(),
				u.getBoolean(),
				u.getBoolean(),
				u.getList(),
				u.getList(),
				u.getBoolean()
		));

		private final Function<ByteUnzip, UserInfo> unzipper;

		Version(Function<ByteUnzip, UserInfo> unzipper) {
			this.unzipper = unzipper;
		}

		public static UserInfo unzip(ByteUnzip unzip) {
			Version v = Version.values()[unzip.getInt()];
			return v.unzipper.apply(unzip);
		}

		public static int last() {
			return values().length - 1;
		}
	}
}
