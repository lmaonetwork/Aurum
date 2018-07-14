package lmao;

import net.md_5.bungee.BungeeCord;

public class Restart extends Thread {
	
	public static void bc(String message) {
		BungeeCord.getInstance().broadcast(message);
	}
	@Override
	public void run() {
		try {
			bc("§6Сервер будет перезагружен через 10 секунд!");
			sleep(5000);
			bc("§6Сервер будет перезагружен через 5 секунд!");
			sleep(1000);
			bc("§6Сервер будет перезагружен через 4 секунды!");
			sleep(1000);
			bc("§6Сервер будет перезагружен через 3 секунды!");
			sleep(1000);
			bc("§6Сервер будет перезагружен через 2 секунды!");
			sleep(1000);
			bc("§6Сервер будет перезагружен через 1 секунду!");
			sleep(1000);
			bc("§c§lСервер перезагружается.");
			sleep(1000);
			BungeeCord.getInstance().stop("§6Сервер перезагружается.\n§6Это займёт не более 30 секунд.\n§6Если " +
												  "сервер не заработает, \n§6Просьба обратиться к администратору.");
		} catch (InterruptedException ignored) {}
		
	}
}
