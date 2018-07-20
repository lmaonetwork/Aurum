package pro.delfik.vk;

import pro.delfik.proxy.data.DataIO;

public class VKBot {
	
	protected static String token;
	
	public static void start() {
		token = DataIO.readFile("vk_token.hl");
		if (token == null || token.length() == 0) System.out.println("[VKBot] Файл с токеном (vk_token.hl) не найден.");
		else new Thread(LongPoll::run).start();
	}
}
