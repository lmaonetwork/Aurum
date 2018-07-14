package lmao.commands;

import lmao.Restart;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.ConsoleCommandSender;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.proxy.permissions.Person;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandImplario extends Command implements TabExecutor {
	
	public CommandImplario(String implario) {
		super(implario);
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ConsoleCommandSender)) if (!Person.get(sender.getName()).isAuthorized()) return;
		if (!(sender.getName().equals("DelfikPro") || sender.getName().equals("__Google_")) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(new TextComponent("§d[Implario] §7§oКогда кончится всё, когда внезапно кончится стёб..."));
			sender.sendMessage(new TextComponent("§d[Implario] §7§oМы закажем кофе, пончик и счёт,"));
			sender.sendMessage(new TextComponent("§d[Implario] §7§oМы это вспомним ещё..."));
			sender.sendMessage(new TextComponent("§d[Implario] §7§oФальшивить даже негде."));
			sender.sendMessage(new TextComponent("§d[Implario] §7§oПриходи ко мне на годовщину нашей смерти."));
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("Implario §e> §6Всегда к твоим услугам."));
			return;
		}
		switch (args[0]) {
			case "upd": {
				int total = 0;
				int failed = 0;
				for (File f : new File("Core/players").listFiles()) {
					if (!f.isDirectory()) continue;
					File file = new File(f.getPath() + "/backup.txt");
					if (!file.exists()) continue;
					try {
						String r = DataIO.readFile("players/" + f.getName() + "/backup");
						String[] as = r.split("\n");
						StringBuilder sb = new StringBuilder();
						for (String a : as) {
							String[] c = a.split("/");
							sb.append(c.length > 1 ? c[1] : c[0].length() < 4 ? " " : c[0]).append('\n');
						}
						DataIO.writeFile("players/" + f.getName() + "/back", r);
						DataIO.writeFile("players/" + f.getName() + "/user", sb.toString());
						
						total++;
					} catch (Exception ignored) {
						sender.sendMessage(new TextComponent("§cfailed " + f.getName() + " with " + ignored.getClass().getSimpleName()));
						failed++;
					}
				}
				sender.sendMessage(new TextComponent("§a§lПеренесено " + total + " файлов. Неудач: " + failed));
				return;
			}
			case "updplayer": {
				int total = 0;
				int failed = 0;
				File f = new File("Core/players/" + args[1]);
				if (!f.isDirectory()) return;
				File file = new File(f.getPath() + "/user.txt");
				if (!file.exists()) return;
				try {
					String r = DataIO.readFile("players/" + f.getName() + "/user");
					sender.sendMessage(new TextComponent("§e" + r));
					String[] as = r.split("\n");
					StringBuilder sb = new StringBuilder();
					for (String a : as) {
						sender.sendMessage(new TextComponent("§e" + a));
						String[] c = a.split("/");
						sb.append(c.length > 1 ? c[1] : ' ').append('\n');
					}
					DataIO.writeFile("players/" + f.getName() + "/back", r);
					DataIO.writeFile("players/" + f.getName() + "/user", sb.toString());
					
					total++;
				} catch (Exception ignored) {
					sender.sendMessage(new TextComponent("§cfailed " + f.getName() + " with " + ignored.getClass().getSimpleName()));
					ignored.printStackTrace();
				}
				sender.sendMessage(new TextComponent("§a§lПеренесено " + total + " файлов"));
				return;
			}
			case "rmrf": {
				int failed = 0;
				int total = 0;
				for (File f : new File("Core/players").listFiles()) {
					if (!f.isDirectory()) continue;
					total++;
					File file = new File(f.getPath() + "/user.txt");
					try {Files.delete(file.toPath());} catch (IOException ignored) {failed++;}
				}
				sender.sendMessage(new TextComponent("Implario §e> §aОчистка завершена. Удалено §e" + total +
															 " файлов. Неудач: " + failed));
				return;
			}
			case "restart": {
				sender.sendMessage(new TextComponent("Implario §e> §aНачинаю перезагрузку главного сервера, сэр."));
				try {new Restart().start();} catch (Exception e) {
					sender.sendMessage(new TextComponent("Implario §e> §cПерезагрузка сервера не удалась из-за " + e
																														   .getMessage()));
				}
				sender.sendMessage(new TextComponent("Implario §e> §aОбратный отсчёт запущен."));
				return;
			}
			case "execute": {
				String cmd = mergeArray(args, 1, " ");
				BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),
						cmd);
				sender.sendMessage(new TextComponent("Implario §e> §aКоманда успешно выполнена от лица " +
															 "§eBungeeConsole"));
				return;
			}
			case "ranklist":
				HashMap<String, String> hashMap = new HashMap<>();
				for (File f : new File("Core/players").listFiles()) {
					if (!f.isDirectory()) continue;
					File file = new File(f.getPath() + "/user.txt");
					if (!file.exists()) continue;
					try {
						
						List<String> list = DataIO.read("players/" + f.getName() + "/user");
						String rank = list.get(2);
						if (rank.equals("P")) continue;
						hashMap.put(rank, hashMap.get(rank) == null ? f.getName() : hashMap.get(rank) + ", " +
								f.getName());
					} catch (Exception ignored) {}
				}
				for (String rank : hashMap.keySet()) {
					sender.sendMessage(new TextComponent("§e§l" + rank + "§f: " + hashMap.get(rank)));
				}
				return;
			case "cleanup": {
				sender.sendMessage(new TextComponent("Implario §e> §6Начинаю очистку неиспользуемых игроков, сэр."));
				int deleted = 0;
				int total = 0;
				for (File f : new File("Core/players").listFiles()) {
					if (!f.isDirectory()) continue;
					total++;
					File file = new File(f.getPath() + "/sf.txt");
					if (f.getName().length() < 14) continue;
					if (!file.exists()) {
						sender.sendMessage(new TextComponent("§6Удаление данных в папке §e" + f.getName()));
						for (File f1 : f.listFiles()) {
							f1.delete();
						}
						f.delete();
						deleted++;
					}
				}
				sender.sendMessage(new TextComponent("Implario §e> §aОчистка завершена. Удалено §e" + deleted +
															 " файлов из " + total + " всего."));
				return;
			}
			default: {
				sender.sendMessage(new TextComponent("Implario §e> §6Вот ваш список команд, сэр:"));
				sender.sendMessage(new TextComponent("§a/implario execute [Команда] §e- выполнить команду от лица " +
															 "главного сервера."));
				sender.sendMessage(new TextComponent("§a/implario ranklist §e- отобразить список игроков с рангами."));
				sender.sendMessage(new TextComponent("Implario §e> §6Помните, что я всегда готов помочь."));
			}
		}
	}
	
	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		Set<String> matches = new HashSet<>();
		String[] SUB = new String[] {"execute", "ranklist", "banlist"};
		if (args.length == 0) return Arrays.asList(SUB);
		if (args.length > 1) return BungeeCord.getInstance().getServers().keySet();
		for (String sub : SUB) {
			if (sub.toLowerCase().startsWith(args[0])) matches.add(sub);
		}
		return matches;
	}
	
	public static String mergeArray(String[] array, int start, String separator) {
		if (array.length < start + 1) return null;
		StringBuilder s = new StringBuilder(array[start]);
		for (int i = start + 1; i < array.length; i++) {
			s.append(separator).append(array[i]);
		}
		return s.toString();
	}
	
}
