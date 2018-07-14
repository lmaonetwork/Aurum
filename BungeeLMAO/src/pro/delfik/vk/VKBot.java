package pro.delfik.vk;

import java.util.Scanner;

public class VKBot {
	
	protected static final String token = "0c8586d7f6b311b6d991bff5a6820ea621b702bb09b0b8c29fc0f3951ff70a95d5201c791a1d8837bf5b3";
	
	public static void start() {
		new Thread(LongPoll::run).start();
		
		new Thread(() -> {
			Scanner in = new Scanner(System.in);
			while (true) {
				String input = in.next();
				if (input.equals("end")) {
					System.exit(0);
					return;
				}
				LongPoll.msg(input, LongPoll.lastPeer);
			}
		}).start();
	}
}
