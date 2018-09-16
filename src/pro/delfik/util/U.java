package pro.delfik.util;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.user.User;

import java.util.function.Function;

public class U {
	
	// -------------------------------------------- Запустить код, игнорируя исключение
	// ignoreEx(() -> doUnsafeShit());
	
	public static boolean ignoreEx(RunnableExc r) {
		try {
			r.run(); return true;
		} catch (Exception ignored) {
			return false;
		}
	}
	
	@FunctionalInterface
	public interface RunnableExc {
		void run() throws Exception;
	}

	// -------------------------------------------- Работа с чатом
	
	public static final char[] COLOR_CHARS = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
			'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};
	public static String color(String m) {
		m = m.replace("&&", "§§");
		for (char c : COLOR_CHARS) m = m.replace("&" + c, "§" + c);
		m = m.replace("<3", "\u2764");
		m = m.replace(":star:", "\u2605");
		m = m.replace(":sword:", "§6§l§m--[§b§l§m---§f");
		m = m.replace("Жс", ":c");
		m = m.replace("сЖ", "c:");
		return m.replace("§§", "&");
	}
	
	public static void msg(CommandSender s, Object... o) {
		if (s != null) s.sendMessage(constructComponent(o));
	}
	
	public static void bc(Server server, Object... o) {
		TextComponent c = constructComponent(o);
		if (server == null) Proxy.i().broadcast(c);
		else for (ProxiedPlayer p : server.getInfo().getPlayers()) p.sendMessage(c);
	}
	
	public static TextComponent hover(String text, String hover) {
		TextComponent c = new TextComponent(text);
		c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {new TextComponent(hover)}));
		return c;
	}
	
	public static TextComponent simple(String text, String hover, String suggest) {
		TextComponent c = new TextComponent(text);
		c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											  new TextComponent[] {new TextComponent(hover)}));
		c.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
		return c;
	}

	public static TextComponent run(String text, String hover, String suggest) {
		TextComponent c = new TextComponent(text);
		c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											  new TextComponent[] {new TextComponent(hover)}));
		c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, suggest));
		return c;
	}

	public static TextComponent create(String text, HoverEvent hover, ClickEvent click) {
		TextComponent c = new TextComponent(text);
		c.setHoverEvent(hover);
		c.setClickEvent(click);
		return c;
	}
	
	public static final Function<ProxiedPlayer, TextComponent> player = p -> create(p.getDisplayName(), getHover(p), getClick(p));
	public static final Function<User, TextComponent> person = p -> create(p.getHandle().getDisplayName(), getHover(p), getClick(p));
	public static final Function<ServerInfo, TextComponent> server = s -> simple("§b§o@" + s.getName(),
			"§e>§f Телепортироваться на сервер §e<", "/stp @" + s.getName());
	
	public static TextComponent constructComponent(Object... o) {
		TextComponent c = new TextComponent("§7");
		for (Object ob : o) c.addExtra(toComponent(ob));
		return c;
	}
	
	public static TextComponent toComponent(Object o) {
		if (o instanceof ProxiedPlayer) return player.apply((ProxiedPlayer) o);
		else if (o instanceof ServerInfo) return server.apply((ServerInfo) o);
		else if (o instanceof User) return person.apply((User) o);
		else if (o instanceof PlayerWrapper) return ((PlayerWrapper) o).toComponent();
		else if (o instanceof TextComponent) return (TextComponent) o;
		else {
			String string = o.toString();
			return new TextComponent(string.equals("prefix") ? "§9{§eAurum§9} §c" : string);
		}
	}
	
	public static TextComponent link(String s, String url) {
		TextComponent tc = new TextComponent(s);
		tc.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return tc;
	}
	
	private static ClickEvent getClick(ProxiedPlayer p) {
		return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/player " + p.getName());
	}
	
	private static HoverEvent getHover(ProxiedPlayer p) {
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
				new TextComponent("§7" + p.getDisplayName() + "\n§e> Нажмите для списка действий §e<")
		});
	}
	private static ClickEvent getClick(User p) {
		return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/player " + p.name);
	}
	
	private static HoverEvent getHover(User p) {
		return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
				new TextComponent("§7" + p.getHandle().getDisplayName() + "\n§e> Нажмите для списка действий §e<")
		});
	}

	public static class PlayerWrapper {
		private final ProxiedPlayer player;
		private final String prefix;

		public PlayerWrapper(ProxiedPlayer player, String prefix) {
			this.player = player;
			this.prefix = prefix;
		}

		private TextComponent toComponent() {
			TextComponent tc = U.toComponent(player);
			tc.setText(prefix + player.getName());
			return tc;
		}
	}
}
