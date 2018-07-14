package pro.delfik.proxy.connection;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.handling.Bans;
import pro.delfik.proxy.command.handling.CommandKick;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.DataPort;
import pro.delfik.util.Converter;
import pro.delfik.util.U;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class PacketProcessor {
	private PacketProcessor() {}
	
	// 000?123.123.123.123:12345
	protected static String handshake(Packet packet) {
		U.msg(Proxy.i().getConsole(), "§aПолучен хендшейк §e§o" + packet);
		return String.valueOf(DataPort.putPort(packet.getArguments()[0]));
	}
	
	// 001?config
	protected static String readFile(Packet packet) {
		Map<String, String> in = DataIO.readConfig(packet.getArguments()[0]);
		if (in == null) return null;
		StringBuilder b = new StringBuilder();
		for (Map.Entry<String, String> entry : in.entrySet())
			b.append(entry.getKey()).append('/').append(entry.getValue()).append("\n");
		return b.toString();
	}
	
	// 002?config&args...
	protected static String writeFile(Packet packet) {
		Map<String, String> map = new HashMap<>();
		String[] args = packet.getArguments();
		for (int i = 1; i < args.length; ++i) {
			String[] s = args[i].split("/");
			if (s.length == 2) map.put(s[0], s[1]);
		}
		DataIO.writeConfig(args[0], map);
		return null;
	}
	
	// 003?trash
	protected static String removeFile(Packet packet) {
		DataIO.remove(packet.getArguments()[0]);
		return null;
	}
	
	// 004?path
	protected static String getAllFiles(Packet packet) {
		File[] files = DataIO.getAll(packet.getArguments()[0]);
		if (files == null) return null;
		StringBuilder builder = new StringBuilder("files/");
		for (File file : files) builder.append(file.getName()).append("}");
		return builder.toString();
	}
	
	// 151?Cheater&Moderator&1440&Использование читов
	// 152?Flooder&Moderator&120&Флуд
	// 153?Disturber&Moderator&Помеха игровому процессу
	protected static String punish(Packet packet) {
		String[] args = packet.getArguments();
		String player = args[0];
		String moderator = args[1];
		Packet.Type punishmentType = packet.getType();
		
		if (punishmentType == Packet.Type.KICK) {
			String reason = Converter.mergeArray(args, 2, "&");
			ProxiedPlayer p = Proxy.getPlayer(player);
			if (p == null) return null;
			CommandKick.kick(p, moderator, reason);
			return null;
		}
		String reason = Converter.mergeArray(args, 3, " ");
		int minutes = Integer.parseInt(args[2]);
		
		if (punishmentType == Packet.Type.BAN) Bans.ban(player, reason, minutes, moderator);
		if (punishmentType == Packet.Type.MUTE) Mutes.mute(player, reason, minutes, moderator);
		return null;
	}
	
	
}
