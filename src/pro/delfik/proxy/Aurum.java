package pro.delfik.proxy;

import __google_.util.FileIO;
import implario.net.Packet;
import implario.util.*;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
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
import pro.delfik.proxy.module.*;
import pro.delfik.proxy.skins.SkinStorage;
import pro.delfik.proxy.stats.StatsThread;
import pro.delfik.proxy.stats.Top;
import pro.delfik.proxy.user.ConsoleUser;
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

public class Aurum extends Plugin {
    public static void main(String args[]){
		String strings[] = FileIO.read(new File("./a.txt")).split("\n");
		List<String> list = new ArrayList<>(strings.length);
		for(String str : strings)
			if(str.contains("RU"))list.add(str);
		System.out.println(strings.length);
		System.out.println(list.size());
		for(int i = 10; i < 20; i++)
		System.out.println(list.get(i));
    }

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
				Registeable.get(Packet::init), new Chat(), new PublicConnector(),
				new Logger(), new Obj(PrivateConnector::init, PrivateConnector::close),
				new Obj(Scheduler::init, Scheduler::kill), new VKBot(), new SkinStorage(),
				Registeable.get(Top::init), new StatsThread(), new Admin(), new ConsoleUser()
		}) register(object);
		events();
		commands();
	}

	private void commands(){
		for(Command command : new Command[]{
				new CmdFM("osk", "Быстрый мут за оскорбление"),
				new CmdFM("flood", "Быстрый мут за флуд"),
				new CmdFM("mt", "Быстрый мут за мат"),
				new CmdFM("caps", "Быстрый мут за капс"),
				new CmdFM("amoral", "Быстрый мут за аморальное поведение"),
				new CommandGuide(), new CmdAurum(),
				new CmdTell(), new CmdReply(), new CmdStp(),
				new CmdBanIP(), new CmdUnbanIP(), new CmdAlert(),
				new CmdEnd(), new CmdKick(), new CmdMute(), new CmdUnmute(),
				new CmdUpdate(), new CmdPing(), new CmdStats(), new CmdHub(),
				new CmdSkin(), new CmdPassChange(), new CmdIgnore(),
				new CmdBan(), new CmdUnban(), new CmdTheme(), new CmdStats(),
				new CmdOnline(), new CmdLogin(), new CmdRegister(), new CmdMoney(), new CmdCheater()
		})register(command);
	}

	private void events(){
		for(Listener listener : new Listener[]{
				new EvChat(), new EvJoin(), new EvPacket(), new EvQuit(),
				new EvReconnect()
		})register(listener);
	}
	
	@Override
	public void onDisable() {
//		unload.forEach(Unloadable::unload);
		for (Unloadable u : unload) {
			u.unload();
		}
	}

	private static final List<Unloadable> unload = new ArrayList<>();

	public static void register(Object object){
		if(object instanceof Unloadable)register((Unloadable)object);
		if(object instanceof Registeable)register((Registeable)object);
		if(object instanceof Listener)register((Listener)object);
		if(object instanceof Command)register((Command)object);
	}

	private static void register(Registeable registeable){
		registeable.register();
	}

	private static void register(Unloadable unloadable){
		unload.add(unloadable);
	}

	private static void register(Listener listener){
		BungeeCord.getInstance().pluginManager.registerListener(instance, listener);
	}

	private static void register(Command command){
		Proxy.registerCommand(command);
	}
}