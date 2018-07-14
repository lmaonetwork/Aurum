package lmao;

import lmao.skin.MojangAPI;
import lmao.skin.SkinApplier;
import lmao.skin.SkinStorage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import pro.delfik.proxy.permissions.Person;

public class CommandSkin extends Command {
	public CommandSkin() {
		super("skin");
	}
	
	public void execute(CommandSender sender, String[] args) {
		
		ProxiedPlayer p = (ProxiedPlayer) sender;
		if (!Person.get(sender).isAuthorized()) return;
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("§aДля изменения скина напишите команду §e/skin [Ник]§a."));
			sender.sendMessage(new TextComponent("§aПосле этого вам будет необходимо перезайти на сервер."));
			sender.sendMessage(new TextComponent("§aЧтобы вернуть стандартный скин, напишите §e/skin clear§a."));
			return;
		}
		
		String skin = args[0];
		ProxyServer.getInstance().getScheduler().runAsync(LmaoBungee.plugin, () -> {
			if (skin.equals("clear")) {
				try {
					SkinStorage.removePlayerSkin(p.getName());
					SkinStorage.setPlayerSkin(p.getName(), p.getName());
					SkinApplier.applySkin(p);
					p.sendMessage(new TextComponent("§aТеперь вы снова §e" + p.getName() + "§a."));
				} catch (Exception ignored) {p.sendMessage("§cВо время удаления скина произошла неизвестная ошибка.");}
				return;
			}
			try {
				MojangAPI.getUUID(skin);
				SkinStorage.setPlayerSkin(p.getName(), skin);
				SkinApplier.applySkin(p);
				p.sendMessage(new TextComponent("§aТеперь вы выглядите как §e" + skin + "§a!"));
			} catch (MojangAPI.SkinRequestException var3) {
				p.sendMessage(new TextComponent("§cСкин §e" + skin + "§c не найден."));
			}
		});
	}
}
