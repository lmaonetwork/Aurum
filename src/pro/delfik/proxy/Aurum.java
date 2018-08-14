package pro.delfik.proxy;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import pro.delfik.net.Packet;
import pro.delfik.net.packet.PacketTop;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.moder.CmdBan;
import pro.delfik.proxy.cmd.moder.CmdBanIP;
import pro.delfik.proxy.cmd.moder.CmdKick;
import pro.delfik.proxy.cmd.moder.CmdMute;
import pro.delfik.proxy.cmd.moder.CmdUnban;
import pro.delfik.proxy.cmd.moder.CmdUnbanIP;
import pro.delfik.proxy.cmd.moder.CmdUnmute;
import pro.delfik.proxy.cmd.kurator.CmdAlert;
import pro.delfik.proxy.cmd.kurator.CmdAurum;
import pro.delfik.proxy.cmd.admin.CmdEnd;
import pro.delfik.proxy.cmd.moder.CmdFM;
import pro.delfik.proxy.cmd.user.CmdLogin;
import pro.delfik.proxy.cmd.user.CmdRegister;
import pro.delfik.proxy.cmd.user.CommandGuide;
import pro.delfik.proxy.cmd.user.CmdHub;
import pro.delfik.proxy.cmd.user.CmdIgnore;
import pro.delfik.proxy.cmd.user.CmdOnline;
import pro.delfik.proxy.cmd.user.CmdPassChange;
import pro.delfik.proxy.cmd.user.CmdPing;
import pro.delfik.proxy.cmd.user.CmdSkin;
import pro.delfik.proxy.cmd.user.CmdReply;
import pro.delfik.proxy.cmd.user.CmdStats;
import pro.delfik.proxy.cmd.user.CmdStp;
import pro.delfik.proxy.cmd.admin.CmdUpdate;
import pro.delfik.proxy.cmd.user.CmdVK;
import pro.delfik.proxy.cmd.user.CmdAttachIP;
import pro.delfik.proxy.cmd.user.CmdTell;
import pro.delfik.proxy.data.ServerListener;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.Database;
import pro.delfik.proxy.ev.EvChat;
import pro.delfik.proxy.ev.EvJoin;
import pro.delfik.proxy.ev.EvPacket;
import pro.delfik.proxy.ev.EvQuit;
import pro.delfik.proxy.ev.EvReconnect;
import pro.delfik.proxy.user.SfTop;
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
		for(Command command : new Command[]{
				new CmdOnline(), new CmdLogin(), new CmdRegister(),
				new CmdFM("osk", "Быстрый мут за оскорбление"),
				new CmdFM("flood", "Быстрый мут за флуд"),
				new CmdFM("mt", "Быстрый мут за мат"),
				new CmdFM("caps", "Быстрый мут за капс"),
				new CmdFM("amoral", "Быстрый мут за аморальное поведение"),
				new CommandGuide(), new CmdVK(), new CmdAurum(),
				new CmdTell(), new CmdReply(), new CmdStp(),
				new CmdBanIP(), new CmdUnbanIP(), new CmdAlert(),
				new CmdEnd(), new CmdKick(), new CmdMute(), new CmdUnmute(),
				new CmdUpdate(), new CmdPing(), new CmdStats(), new CmdHub(),
				new CmdSkin(), new CmdPassChange(), new CmdAttachIP(), new CmdIgnore(),
				new CmdBan(), new CmdUnban()
		}) Proxy.registerCommand(command);
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
