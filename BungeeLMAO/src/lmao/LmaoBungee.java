package lmao;

import lmao.commands.CommandImplario;
import lmao.skin.SkinApplier;
import lmao.skin.SkinStorage;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.data.SocketEvent;

import java.io.File;

public class LmaoBungee extends Plugin implements Listener {
	
	public static final String Prefix = "LMAO §e> §c";
	public static Plugin plugin;
	public static final PluginManager manager = BungeeCord.getInstance().pluginManager;
	public static final String LOBBY = "LOBBY_1";
	public static boolean debug = false;
	
	@Override
	public void onEnable() {
		plugin = this;
		manager.registerCommand(this, new CommandImplario("implario"));
		manager.registerCommand(this, new CommandSkin());
		manager.registerListener(this, this);
		manager.registerListener(this, new SkinHandler());
		SkinApplier.init();
		SkinStorage.init(new File("Core/SkinsHandler"));
	}
	
	@EventHandler
	public void onSocket(SocketEvent e) {
		String[] args = e.getMsg().split("/");
		if (e.getChannel().equals("setrank")) {
			String name = args[0];
		}
		if (e.getChannel().equals("hub")) {
			String name = args[0];
			BungeeCord.getInstance().getPlayer(args[0]).connect(BungeeCord.getInstance().getServerInfo("LOBBY_1"));
		}
	}
	
}
