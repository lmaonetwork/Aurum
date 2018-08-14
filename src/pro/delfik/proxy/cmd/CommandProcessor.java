package pro.delfik.proxy.cmd;

import net.md_5.bungee.api.CommandSender;

@FunctionalInterface
public interface CommandProcessor {
	String process(CommandSender sender, Command command, String[] args);
}
