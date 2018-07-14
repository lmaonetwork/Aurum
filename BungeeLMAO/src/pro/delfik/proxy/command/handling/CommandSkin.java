package pro.delfik.proxy.command.handling;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.AurumPlugin;
import pro.delfik.proxy.command.Command;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.proxy.permissions.Rank;
import pro.delfik.proxy.skins.MojangAPI;
import pro.delfik.proxy.skins.SkinApplier;
import pro.delfik.proxy.skins.SkinStorage;

public class CommandSkin extends Command {
	
	public CommandSkin() {
		super("skin", Rank.PLAYER, "Изменение скина");
	}
	
	@Override
	protected void run(CommandSender sender, String[] args) {
		ProxiedPlayer p = (ProxiedPlayer) sender;
		if (!Person.get(sender).isAuthorized()) return;
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("§aДля изменения скина напишите команду §e/skin [Ник]§a."));
			sender.sendMessage(new TextComponent("§aПосле этого вам будет необходимо перезайти на сервер."));
			sender.sendMessage(new TextComponent("§aЧтобы вернуть стандартный скин, напишите §e/skin clear§a."));
			return;
		}
		
		String skin = args[0];
		ProxyServer.getInstance().getScheduler().runAsync(AurumPlugin.instance, () -> {
			if (skin.equals("clear")) {
				try {
					SkinStorage.removePlayerSkin(p.getName());
					SkinStorage.setPlayerSkin(p.getName(), p.getName());
					SkinApplier.applySkin(p);
					p.sendMessage(new TextComponent("§aТеперь вы снова §e" + p.getName() + "§a."));
				} catch (Exception ignored) {
					msg(p,"§cВо время удаления скина произошла неизвестная ошибка.");
				}
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
