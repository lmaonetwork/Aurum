package pro.delfik.vk;

public class MessageHandler {
	public static String[] handle(String text, int from_id, long peer_id) {
		String ask = text.toLowerCase().replace(",", "");
		if (text.equals("\uD83C\uDF6A")) return new String[]{"Вкусная печенька, спасибо!"};
//		if (ask.startsWith("ipchange")) {
//			String name = null;
//			try {
//				Database.Result result = Database.sendQuery("SELECT nickname FROM VKPages WHERE link = '" + from_id + "'");
//				ResultSet set = result.set;
//				if (set.next()) {
//					name = set.getString("nickname");
//				}
//				result.st.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//				return new String[] {"При обработке запроса произошла ошибка :c"};
//			}
//
//			if (name == null) {
//				return new String[] {VK.getUserName(from_id) + ", к вашей странице VK не прикреплён игровой аккаунт, ",
//						"Чтобы прикрепить страницу, введите команду /vk в игре."};
//			}
//			User.allowedIP.add(name.toLowerCase());
//			return new String[] {"С аккаунта " + name + " снята привязка IP-адреса. У вас есть одна минута, затем защита восстановится."};
//		}
//		if (ask.startsWith("confirm ")) {
//			String codeStr = ask.substring(8);
//			int code = Converter.toInt(codeStr, -1);
//			if (code < 1) return new String[] {VK.getUserName(from_id) + ", код подтверждения првязки страницы VK введён неверно."};
//			PageAttachRequest request = PageAttachRequest.byCode.get(code);
//			if (request == null || request.getPageID() != from_id)
//				return new String[] {VK.getUserName(from_id) + ", код потверждения введён неверно. Это значит, что либо вы ещё не ввели команду /vk на сервере, " +
//											 "либо вы ошиблись, когда вводили sID страницы/Код подтверждения.", "Попробуйте ещё раз."};
//			request.confirm();
//			return new String[] {VK.getUserName(from_id) + ", аккаунт " + request.getPlayer() + " успешно привязан к вашей странице ВКонтакте!"};
//		}
		return new String[0];
		
	}
}
