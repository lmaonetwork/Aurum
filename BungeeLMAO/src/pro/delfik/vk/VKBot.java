package pro.delfik.vk;

public class VKBot {
	
	protected static final String token = "0c8586d7f6b311b6d991bff5a6820ea621b702bb09b0b8c29fc0f3951ff70a95d5201c791a1d8837bf5b3";
	
	public static void start() {
		new Thread(LongPoll::run).start();
	}
}
