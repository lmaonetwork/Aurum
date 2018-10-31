package pro.delfik.proxy;

import implario.net.Packet;
import implario.net.packet.PacketTop;
import implario.util.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.admin.CmdEnd;
import pro.delfik.proxy.cmd.admin.CmdUpdate;
import pro.delfik.proxy.cmd.kurator.CmdAlert;
import pro.delfik.proxy.cmd.kurator.CmdAurum;
import pro.delfik.proxy.cmd.moder.*;
import pro.delfik.proxy.cmd.user.*;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.data.PublicConnector;
import pro.delfik.proxy.data.ServerListener;
import pro.delfik.proxy.ev.*;
import pro.delfik.proxy.modules.Chat;
import pro.delfik.proxy.modules.SfTop;
import pro.delfik.proxy.skins.SkinApplier;
import pro.delfik.proxy.skins.SkinStorage;
import pro.delfik.util.Logger;
import pro.delfik.util.TimedList;
import pro.delfik.util.U;
import pro.delfik.vk.LongPoll;
import pro.delfik.vk.MessageHandler;
import pro.delfik.vk.VK;
import pro.delfik.vk.VKBot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Aurum extends Plugin {
	private static final List<Runnable> unload = new ArrayList<>();

	private static CryptoUtils cryptoUtils;
	public static Aurum instance;
	
	private static void classLoader() {
		U.class.getCanonicalName();
		VK.class.getCanonicalName();
		Rank.class.getCanonicalName();
		Chat.class.getCanonicalName();
		SfTop.class.getCanonicalName();
		SfTop.class.getCanonicalName();
		VKBot.class.getCanonicalName();
		Logger.class.getCanonicalName();
		LongPoll.class.getCanonicalName();
		TimedList.class.getCanonicalName();
		PacketTop.class.getCanonicalName();
		Converter.class.getCanonicalName();
		ArrayUtils.class.getCanonicalName();
		ServerInfo.class.getCanonicalName();
		StringUtils.class.getCanonicalName();
		CryptoUtils.class.getCanonicalName();
		PacketTop.Top.class.getCanonicalName();
		ArrayIterator.class.getCanonicalName();
		MessageHandler.class.getCanonicalName();
		U.PlayerWrapper.class.getCanonicalName();
		CryptoUtils.Keccak.class.getCanonicalName();
		CryptoUtils.Keccak.Parameters.class.getCanonicalName();
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
		VKBot.start();
		RequestListener.init();
		Map<String, String> read = DataIO.readConfig("config");
		cryptoUtils = new CryptoUtils(read.get("crypto"));
		ServerListener.init(Converter.toInt(read.get("port")));
		PublicConnector.enable();
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
				new CmdBan(), new CmdUnban(), new CmdTheme()
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
	
	public static CryptoUtils getCryptoUtils() {
		return cryptoUtils;
	}

	public static void addUnload(Runnable runnable){
		unload.add(runnable);
	}
	
	@Override
	public void onDisable() {
		unload.forEach(Runnable::run);
		ServerListener.close();
		Scheduler.kill();
		RequestListener.discontinue();
		PublicConnector.disable();
		Logger.close();
		VKBot.stop();
	}
}
