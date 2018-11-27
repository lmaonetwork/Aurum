package pro.delfik.proxy.cmd.user;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.cmd.Command;
import pro.delfik.proxy.user.User;
import implario.util.Rank;
import pro.delfik.proxy.skins.MojangAPI;
import pro.delfik.proxy.skins.SkinApplier;
import pro.delfik.proxy.skins.SkinStorage;

public class CmdSkin extends Command {
	public CmdSkin() {
		super("skin", Rank.PLAYER, "Изменение скина");
	}
	
	@Override
	protected void run(User user, String[] args) {
		if (args.length == 0) {
			user.msg("§aДля изменения скина напишите команду §e/skin [Ник]§a.");
			user.msg("§aПосле этого вам будет необходимо перезайти на сервер.");
			user.msg("§aЧтобы вернуть стандартный скин, напишите §e/skin clear§a.");
			return;
		}
		ProxiedPlayer player = user.getHandle();
		String skin = args[0];
		ProxyServer.getInstance().getScheduler().runAsync(Aurum.instance, () -> {
			if (skin.equals("clear")) {
				try {
					SkinStorage.removePlayerSkin(user.getName());
					SkinStorage.setPlayerSkin(user.getName(), user.getName());
					SkinApplier.applySkin(player);
					user.msg("§aТеперь вы снова §e" + user.getName() + "§a.");
				} catch (Exception ignored) {
					user.msg("§cВо время удаления скина произошла неизвестная ошибка.");
				}
				return;
			}
			try {
				MojangAPI.getUUID(skin);
				SkinStorage.setPlayerSkin(user.getName(), skin);
				SkinApplier.applySkin(player);
				user.msg("§aТеперь вы выглядите как §e" + skin + "§a!");
			} catch (MojangAPI.SkinRequestException var3) {
				user.msg("§cСкин §e" + skin + "§c не найден.");
			}
		});
	}
}
