package pro.delfik.proxy;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketTop;
import pro.delfik.proxy.command.handling.Authorization;
import pro.delfik.proxy.command.handling.Bans;
import pro.delfik.proxy.command.handling.BansIP;
import pro.delfik.proxy.command.handling.CommandAlert;
import pro.delfik.proxy.command.handling.CommandAurum;
import pro.delfik.proxy.command.handling.CommandEnd;
import pro.delfik.proxy.command.handling.CommandFM;
import pro.delfik.proxy.command.handling.CommandGuide;
import pro.delfik.proxy.command.handling.CommandHub;
import pro.delfik.proxy.command.handling.CommandIgnore;
import pro.delfik.proxy.command.handling.Kicks;
import pro.delfik.proxy.command.handling.CommandOnline;
import pro.delfik.proxy.command.handling.CommandPassChange;
import pro.delfik.proxy.command.handling.CommandPing;
import pro.delfik.proxy.command.handling.CommandSkin;
import pro.delfik.proxy.command.handling.CommandStats;
import pro.delfik.proxy.command.handling.CommandStp;
import pro.delfik.proxy.command.handling.CommandUpdate;
import pro.delfik.proxy.command.handling.CommandVK;
import pro.delfik.proxy.command.handling.IPBound;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.command.handling.PrivateMessages;
import pro.delfik.proxy.connection.ServerListener;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.ev.EvChat;
import pro.delfik.proxy.ev.EvJoin;
import pro.delfik.proxy.ev.EvPacket;
import pro.delfik.proxy.ev.EvQuit;
import pro.delfik.proxy.ev.EvReconnect;
import pro.delfik.proxy.games.SfTop;
import pro.delfik.proxy.skins.SkinApplier;
import pro.delfik.proxy.skins.SkinStorage;
import pro.delfik.util.ArrayIterator;
import pro.delfik.util.ArrayUtils;
import pro.delfik.util.Converter;
import pro.delfik.util.CryptoUtils;
import pro.delfik.util.Rank;
import pro.delfik.util.Scheduler;
import pro.delfik.util.StringUtils;
import pro.delfik.util.TimedList;
import pro.delfik.util.U;
import pro.delfik.vk.LongPoll;
import pro.delfik.vk.MessageHandler;
import pro.delfik.vk.VK;
import pro.delfik.vk.VKBot;

import java.io.File;
import java.util.Map;

public class Aurum extends Plugin {
	private static CryptoUtils cryptoUtils;
	public static Aurum instance;
	
	private static void classLoader() {
		Rank.class.getCanonicalName();
		SfTop.class.getCanonicalName();
		ArrayUtils.class.getCanonicalName();
		CryptoUtils.class.getCanonicalName();
		CryptoUtils.Keccak.class.getCanonicalName();
		ServerInfo.class.getCanonicalName();
		CryptoUtils.Keccak.Parameters.class.getCanonicalName();
		Converter.class.getCanonicalName();
		StringUtils.class.getCanonicalName();
		ArrayIterator.class.getCanonicalName();
		TimedList.class.getCanonicalName();
		VK.class.getCanonicalName();
		VKBot.class.getCanonicalName();
		LongPoll.class.getCanonicalName();
		MessageHandler.class.getCanonicalName();
		U.class.getCanonicalName();
		SfTop.class.getCanonicalName();
		PacketTop.class.getCanonicalName();
		PacketTop.Top.class.getCanonicalName();
	}

	@Override
	public void onLoad() {
		instance = this;
		classLoader();
		events();
		commands();
		SkinApplier.init();
		SkinStorage.init(new File("Core/SkinsHandler"));
		Scheduler.init();
		Packet.init();
		SfTop.init();
		VKBot.start();
		Database.enable();
		load();
	}

	private void commands(){
		new CommandOnline();
		new Authorization("login", "Авторизация на сервере.", "l");
		new Authorization("register", "Регистрация на сервере", "p", "reg");
		new CommandFM("osk", "Быстрый мут за оскорбление");
		new CommandFM("flood", "Быстрый мут за флуд");
		new CommandFM("mt", "Быстрый мут за мат");
		new CommandFM("caps", "Быстрый мут за капс");
		new CommandFM("amoral", "Быстрый мут за аморальное поведение");
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
		new Kicks();
		new Mutes(false);
		new Mutes(true);
		new CommandUpdate();
		new CommandPing();
		new CommandStats();
		new CommandHub();
		new CommandSkin();
		new CommandPassChange();
		new IPBound();
		new CommandIgnore();
	}

	private void events(){
		PluginManager manager = BungeeCord.getInstance().pluginManager;
		manager.registerListener(this, new EvChat());
		manager.registerListener(this, new EvJoin());
		manager.registerListener(this, new EvPacket());
		manager.registerListener(this, new EvQuit());
		manager.registerListener(this, new EvReconnect());
	}
	
	private void load() {
		Map<String, String> read = DataIO.readConfig("config");
		cryptoUtils = new CryptoUtils(read.get("crypto"));
		ServerListener.init(Converter.toInt(read.get("port")));
	}
	
	public static CryptoUtils getCryptoUtils() {
		return cryptoUtils;
	}
	
	@Override
	public void onDisable() {
		ServerListener.close();
		SfTop.unload();
		EvChat.unload();
	}
	
	public void run() {
		ServerListener.run();
	}
}
