package pro.delfik.proxy.command;

import net.md_5.bungee.api.CommandSender;

@FunctionalInterface
public interface CommandProcessor {
	Object[] process(CommandSender sender, Command command, String[] args);
}
