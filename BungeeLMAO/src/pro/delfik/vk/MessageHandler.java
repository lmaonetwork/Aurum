package pro.delfik.vk;

import pro.delfik.proxy.command.handling.Authorization;
import pro.delfik.proxy.data.Database;

import java.sql.ResultSet;

public class MessageHandler {
	public static String[] handle(String text, int from_id, long peer_id) {
		if (!text.toLowerCase().replace(",", "").contains("lmaobot сбрось ip")) return new String[] {};
		String name = null;
		try {
			Database.Result result = Database.sendQuery("SELECT nickname FROM VKPages WHERE link = '" + from_id + "'");
			ResultSet set = result.set;
			if (set.next()) {
				name = set.getString("nickname");
			}
			result.st.close();
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] {"При обработке запроса произошла ошибка :c"};
		}
		
		if (name == null) {
			return new String[] {"К вашей странице VK не прикреплён игровой аккаунт, "  + VK.getUserName(from_id),
								"Чтобы прикрепить страницу, введите команду /vk в игре."};
		}
		Authorization.allowedIPs.add(name.toLowerCase());
		return new String[] {"С аккаунта " + name + " снята привязка IP-адреса. У вас есть одна минута, затем защита восстановится."};
	}
}
