package pro.delfik.proxy;

import __google_.util.FileIO;
import implario.net.Packet;
import implario.util.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.cmd.admin.CmdEnd;
import pro.delfik.proxy.cmd.admin.CmdUpdate;
import pro.delfik.proxy.cmd.kurator.CmdAlert;
import pro.delfik.proxy.cmd.kurator.CmdAurum;
import pro.delfik.proxy.cmd.moder.*;
import pro.delfik.proxy.cmd.user.*;
import pro.delfik.proxy.data.PrivateConnector;
import pro.delfik.proxy.data.PublicConnector;
import pro.delfik.proxy.ev.*;
import pro.delfik.proxy.module.Obj;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.user.Chat;
import pro.delfik.proxy.skins.SkinApplier;
import pro.delfik.proxy.skins.SkinStorage;
import pro.delfik.proxy.stats.StatsThread;
import pro.delfik.proxy.stats.Top;
import pro.delfik.util.Logger;
import pro.delfik.util.TimedList;
import pro.delfik.util.U;
import pro.delfik.vk.LongPoll;
import pro.delfik.vk.MessageHandler;
import pro.delfik.vk.VK;
import pro.delfik.vk.VKBot;
import pro.delfik.proxy.module.Unloadable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Aurum extends Plugin {
	public static Aurum instance;
	
	private static void classLoader() {
		U.class.getCanonicalName();
		VK.class.getCanonicalName();
		Top.class.getCanonicalName();
		Rank.class.getCanonicalName();
		Chat.class.getCanonicalName();
		VKBot.class.getCanonicalName();
		Logger.class.getCanonicalName();
		LongPoll.class.getCanonicalName();
		TimedList.class.getCanonicalName();
		Converter.class.getCanonicalName();
		ArrayUtils.class.getCanonicalName();
		ServerInfo.class.getCanonicalName();
		StringUtils.class.getCanonicalName();
		CryptoUtils.class.getCanonicalName();
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
		register();
	}

	private void register(){
		for(Object object : new Object[]{
				Registeable.get(Packet::init), new CmdOnline(), new CmdLogin(), new CmdRegister(),
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
				new CmdBan(), new CmdUnban(), new CmdTheme(), new CmdStats(),
				new EvChat(), new EvJoin(), new EvPacket(), new EvQuit(),
				new EvReconnect(), new Chat(), new PublicConnector(),
				new Logger(), new Obj(PrivateConnector::init, PrivateConnector::close),
				new Obj(Scheduler::init, Scheduler::kill), new VKBot(), new SkinStorage(),
				Registeable.get(Top::init), new StatsThread()
		}) register(object);
	}
	
	@Override
	public void onDisable() {
		unload.forEach(Unloadable::unload);
	}

	private static final List<Unloadable> unload = new ArrayList<>();

	public static void register(Object object){
		if(object instanceof Unloadable)unload.add((Unloadable)object);
		if(object instanceof Registeable)((Registeable)object).register();
		if(object instanceof Listener)BungeeCord.getInstance().pluginManager.registerListener(instance, (Listener)object);
		if(object instanceof Command)Proxy.registerCommand((Command)object);
	}
}
