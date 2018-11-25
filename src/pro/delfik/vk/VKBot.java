package pro.delfik.vk;

import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.data.DataIO;
import pro.delfik.vk.module.Admin;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.module.Unloadable;

public class VKBot implements Registeable{
	protected static String token;

	@Override
	public void register(){
		token = DataIO.readFile("vk_token");
		if (token == null || token.length() == 0) System.out.println("[VKBot] Файл с токеном (vk_token.hl) не найден.");
		else {
			Aurum.register(new Admin());
			new Thread(LongPoll::run).start();
		}
	}
}
