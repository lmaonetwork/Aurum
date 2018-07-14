package pro.delfik.vk;

import main.java.stroum.HTTP.Requests;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class VK {
	public static String query(String method) {
		return Requests.post("https://api.vk.com/method/" + method, "v=3&access_token=" + VKBot.token);
	}
	public static String query(String method, String params) {
		return Requests.post("https://api.vk.com/method/" + method, "v=3&access_token=" + VKBot.token + "&" + params);
	}
	
	
	
	public static void markAsRead(int from_id) {
		VK.query("messages.markAsRead", "peer_id=" + from_id);
	}
	
	public static String getChatName(int cid) {
		if (cid > 2000000000) {
			cid = cid - 2000000000;
		}
		
		String title;
		
		String data = query("messages.getChat", "chat_id=" + cid);
		try {
			JSONObject obj = new JSONObject(data);
			JSONObject response = obj.getJSONObject("response");
			title = response.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
			title = "";
		}
		
		return title;
	}
	
	/* Don't try to understand wtf is this */
	public static String uploadPhoto(File f) {
		String id = "";
		String upload_url;
		
		try {
			String server = VK.query("photos.getMessagesUploadServer");
			
			JSONObject s = new JSONObject(server);
			upload_url = s.getJSONObject("response").getString("upload_url");
			
			String res = Requests.post_upload(upload_url, f);
			
			
			JSONObject j = new JSONObject(res);
			
			int _server = j.getInt("server");
			String _photo = j.getString("photo");
			String _hash = j.getString("hash");
			
			String params = "server=" + _server + "&photo=" + _photo + "&hash=" + _hash;
			
			res = VK.query("photos.saveMessagesPhoto", params);
			
			j = new JSONObject(res);
			JSONArray arr = j.getJSONArray("response");
			JSONObject g = arr.getJSONObject(0);
			id = g.getString("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static String getUserName(int uid) {
		String data = query("users.get", "user_id=" + uid);
		String full_name;
		System.out.println(data);
		try {
			JSONObject obj = new JSONObject(data);
			JSONArray response = obj.getJSONArray("response");
			JSONObject _data = response.getJSONObject(0);
			String first_name = _data.getString("first_name");
			String last_name = _data.getString("last_name");
			
			full_name = first_name + " " + last_name;
			
		} catch (JSONException e) {
			e.printStackTrace();
			full_name = "";
		}
		
		return full_name;
	}
}
