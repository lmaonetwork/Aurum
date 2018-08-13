package pro.delfik.proxy.ev;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pro.delfik.proxy.permissions.Person;

public class EvQuit implements Listener{
	@EventHandler
	public void event(PlayerDisconnectEvent event) {
		Person.unload(event.getPlayer().getName());
	}
}
