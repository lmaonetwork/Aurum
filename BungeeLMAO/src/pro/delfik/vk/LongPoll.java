package pro.delfik.vk;

import main.java.stroum.HTTP.Requests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LongPoll {
	protected static String key;
	protected static String server;
	protected static int ts;
	public static volatile long lastPeer;
	
	protected static void requestLongPollServer() {
		String data = VK.query("groups.getLongPollServer", "group_id=164443675");
		try {
			JSONObject obj = new JSONObject(data);
			JSONObject response = obj.getJSONObject("response");
			key = response.getString("key");
			server = response.getString("server");
			ts = response.getInt("ts");
			System.out.println("[VKBot] Сервер LongPoll успешно обновлён.");
		} catch (Exception ex) {
			System.out.println("[VKBot] Во время обновления сервера LongPoll произошла ошибка.");
			ex.printStackTrace();
		}
	}
	
	public static String getServer() {
		return server;
	}
	public static String getKey() {
		return key;
	}
	public static int getTs() {
		return ts;
	}
	
	protected static String getConnectUrl(int ts) {
		return (server.startsWith("http") ? "" : "https://") + server + "?act=a_check&key=" + key +
					   "&ts=" + ts + "&wait=25&mode=2";
	}
	private static boolean failed = false;
	public static void run() {
		requestLongPollServer();
		while (true) {
			String lp = getConnectUrl(ts);
			String data = Requests.get(lp);
			try {
				JSONObject response = new JSONObject(data);
				int _ts = response.getInt("ts");
				JSONArray updates = response.getJSONArray("updates");
				if(updates.length() != 0) processEvent(updates, _ts);
				ts = _ts;
			} catch (JSONException ex) {
				if (failed) throw new RuntimeException("Не удалось подключиться к LongPoll."); else {
					requestLongPollServer();
					failed = true;
				}
			}
		}
	}
	
	
	private static void processEvent(JSONArray array, long ts) {
		for (int i = 0; i <  array.length(); ++i) {
			try {
				JSONObject arrayItem = array.getJSONObject(i);
				String eventType = arrayItem.getString("type");
				JSONObject object = arrayItem.getJSONObject("object");
				
				
				switch (eventType) {
					case "message_new":
						String text;
						int from_id;
						long peer_id = object.getLong("peer_id");
						try {
							from_id = object.getInt("user_id");
						} catch (Exception ex) {
							from_id = object.getInt("from_id");
						}
						try {
							text = object.getString("body");
						} catch (Exception ex) {
							text = object.getString("text");
						}
						
						lastPeer = peer_id;
						
						text = text.replaceAll("\\[.*]", "");
						String name = VK.getUserName(peer_id > 2000000000 ? from_id : (int) peer_id);
						for (String msg : MessageHandler.handle(text, from_id, peer_id)) msg(msg, peer_id);
						break;
				}
				
			} catch (JSONException ignored) {}
		}
	}
	
	public static String msg(String message, long peer) {
		return VK.query("messages.send", "message=" + message + "&" +
												 (peer > 2000000000 ? "chat_id=" + (peer - 2000000000) : "user_id=" + peer));
	}
	
}
