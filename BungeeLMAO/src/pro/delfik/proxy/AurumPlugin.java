package pro.delfik.proxy;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import pro.delfik.proxy.command.handling.Authorization;
import pro.delfik.proxy.command.handling.Bans;
import pro.delfik.proxy.command.handling.BansIP;
import pro.delfik.proxy.command.handling.CommandAlert;
import pro.delfik.proxy.command.handling.CommandAurum;
import pro.delfik.proxy.command.handling.CommandEnd;
import pro.delfik.proxy.command.handling.CommandFM;
import pro.delfik.proxy.command.handling.CommandGuide;
import pro.delfik.proxy.command.handling.CommandHub;
import pro.delfik.proxy.command.handling.CommandKick;
import pro.delfik.proxy.command.handling.CommandOnline;
import pro.delfik.proxy.command.handling.CommandPassChange;
import pro.delfik.proxy.command.handling.CommandPing;
import pro.delfik.proxy.command.handling.CommandSkin;
import pro.delfik.proxy.command.handling.CommandStats;
import pro.delfik.proxy.command.handling.CommandStp;
import pro.delfik.proxy.command.handling.CommandVK;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.command.handling.PrivateMessages;
import pro.delfik.proxy.data.Connection;
import pro.delfik.proxy.data.DataEvent;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.DataPort;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.data.SocketCommunicator;
import pro.delfik.proxy.data.SocketEvent;
import pro.delfik.proxy.games.SfTop;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.proxy.skins.SkinApplier;
import pro.delfik.proxy.skins.SkinStorage;
import pro.delfik.util.ArrayIterator;
import pro.delfik.util.ArrayUtils;
import pro.delfik.util.Converter;
import pro.delfik.util.CryptoUtils;
import pro.delfik.util.Scheduler;
import pro.delfik.util.StringUtils;
import pro.delfik.util.TimedList;
import pro.delfik.vk.LongPoll;
import pro.delfik.vk.MessageHandler;
import pro.delfik.vk.VK;
import pro.delfik.vk.VKBot;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class AurumPlugin extends Plugin implements Runnable {
	private static ServerSocket read;
	private static volatile boolean has;
	private static int port;
	private static CryptoUtils cryptoUtils;
	public static AurumPlugin instance;
	
	private static void classLoader() {
		Rank.class.getCanonicalName();
		SfTop.class.getCanonicalName();
		DataEvent.class.getCanonicalName();
		SocketEvent.class.getCanonicalName();
		ArrayUtils.class.getCanonicalName();
		CryptoUtils.class.getCanonicalName();
		CryptoUtils.Keccak.class.getCanonicalName();
		ServerType.class.getCanonicalName();
		ServerInfo.class.getCanonicalName();
		DataPort.class.getCanonicalName();
		Connection.class.getCanonicalName();
		CryptoUtils.Keccak.Parameters.class.getCanonicalName();
		Converter.class.getCanonicalName();
		StringUtils.class.getCanonicalName();
		ArrayIterator.class.getCanonicalName();
		TimedList.class.getCanonicalName();
		VK.class.getCanonicalName();
		VKBot.class.getCanonicalName();
		LongPoll.class.getCanonicalName();
		MessageHandler.class.getCanonicalName();
	}
	
	public void onLoad() {
		instance = this;
		classLoader();
		BungeeCord cord = BungeeCord.getInstance();
		this.load();
		PluginManager manager = cord.pluginManager;
		Database.enable();
		manager.registerListener(this, new OnlineHandler());
		manager.registerListener(this, new SocketCommunicator());
		manager.registerListener(this, new ChatHandler());
	}
	
	@Override
	public void onEnable() {
		new CommandOnline();
		new Authorization("login", "Авторизация на сервере.", "l");
		new Authorization("register", "Регистрация на сервере", "p", "reg");
		new CommandFM("osk", "");
		new CommandFM("flood", "");
		new CommandFM("mat", "");
		new CommandGuide();
		new CommandVK();
		new CommandAurum();
		new PrivateMessages(false);
		new PrivateMessages(true);
		new CommandStp();
		new Bans(false);
		new Bans(true);
		new BansIP(false);
		new BansIP(true);
		new CommandAlert();
		new CommandEnd();
		new CommandKick();
		new Mutes(false);
		new Mutes(true);

		new CommandPing();
		new CommandStats();
		new CommandHub();
		new CommandSkin();
		new CommandPassChange();
		
		SkinApplier.init();
		SkinStorage.init(new File("Core/SkinsHandler"));
		Scheduler.init();
		
		VKBot.start();
		
	}
	
	private void load() {
		has = false;
		Map<String, String> read = DataIO.readConfig("config");
		if (read == null) {
			cryptoUtils = new CryptoUtils("1234567890123456");
			port = 1234;
			System.out.println("Configs not found, using default values");
		} else {
			cryptoUtils = new CryptoUtils(read.get("crypto"));
			port = Converter.toInt(read.get("port"));
			if (port < 1 || port > 65535) {
				throw new Error("Error from read port, needed 1 - 65535");
			}
		}
		new Thread(this).start();
	}
	
	public static CryptoUtils getCryptoUtils() {
		return cryptoUtils;
	}
	
	@Override
	public void onDisable() {
		has = true;
		if (read != null) {
			try {
				read.close();
			} catch (IOException ignored) {
			}
		}
		
		DataPort.unload();
		SfTop.unload();
	}
	
	public void run() {
		try {
			read = new ServerSocket(port);
			while(!has) {
				Socket socket = read.accept();
				if (socket != null) {
					(new Connection(socket)).start();
				}
			}
		} catch (IOException var2) {
			System.out.println("Socket closed.");
		}
		
	}
}
