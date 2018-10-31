package pro.delfik.vk;

import pro.delfik.proxy.data.DataIO;
import pro.delfik.vk.cmd.CmdAdmin;
import pro.delfik.vk.module.Admin;
import pro.delfik.vk.module.Registeable;
import pro.delfik.vk.module.Unloadable;

import java.util.ArrayList;
import java.util.List;

public class VKBot {
	private static final List<Unloadable> unload = new ArrayList<>();

	protected static String token;
	
	public static void start() {
		token = DataIO.readFile("vk_token");
		if (token == null || token.length() == 0) System.out.println("[VKBot] Файл с токеном (vk_token.hl) не найден.");
		else {
			register();
			new Thread(LongPoll::run).start();
		}
	}

	public static void stop(){
		unload.forEach(Unloadable::unload);
	}

	private static void register(){
		register(new Admin());
	}

	public static void register(Registeable registeable){
		if(registeable instanceof Unloadable)unload.add((Unloadable)registeable);
		registeable.register();
	}
}
