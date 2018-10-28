package pro.delfik.vk;

import net.md_5.bungee.api.config.ServerInfo;
import pro.delfik.proxy.Proxy;

public class MessageHandler {
	public static String[] handle(String text, int from_id, long peer_id) {
		if (text.equals("\uD83C\uDF6A")) return new String[]{"Вкусная печенька, спасибо!"};
		else if(text.equalsIgnoreCase("!Онлайн")) {
			ServerInfo infos[] = Proxy.i().getServers().values().toArray(new ServerInfo[]{});
			String result = "Общий онлайн: " + Proxy.i().getPlayers().size();
			for(int i = 0; i < infos.length; i++){
				ServerInfo info = infos[i];
				result = result + "\r\n" + info.getName() + ": " + info.getPlayers().size();
			}
			return new String[]{result};
		}
		return new String[0];
	}
}
