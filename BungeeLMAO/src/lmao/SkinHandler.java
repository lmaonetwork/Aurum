package lmao;

import lmao.skin.SkinApplier;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SkinHandler implements Listener {
	@EventHandler
	public void onJoin(PostLoginEvent e) {
		ProxyServer.getInstance().getScheduler().runAsync(LmaoBungee.plugin, () -> {
			BungeeCord.getInstance().getScheduler().schedule(LmaoBungee.plugin, () -> SkinApplier.applySkin(e.getPlayer()), 10L, TimeUnit.MILLISECONDS);
		});
	}
	
	class SubLogin implements Runnable {
		
		public final SkinHandler s;
		public final PostLoginEvent e;
		
		public SubLogin(SkinHandler s, PostLoginEvent e) {
			this.s = s;
			this.e = e;
		}
		
		public void run() {
			SkinApplier.applySkin(this.e.getPlayer());
		}
	}
}
