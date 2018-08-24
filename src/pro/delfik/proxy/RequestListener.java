package pro.delfik.proxy;

import pro.delfik.proxy.user.User;
import pro.delfik.util.CryptoUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestListener extends Thread {

	private static boolean enabled = true;
	private static ServerSocket serverSocket;

	@Override
	public void run() {
		while (enabled) {
			try {
				Socket socket = serverSocket.accept();
				if (socket == null) continue;
				InputStream in = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String line;
				List<String> params = new ArrayList<>();
				while ((line = reader.readLine()) != null) params.add(line);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				writer.write(process(params, socket.getInetAddress().getHostAddress()));
				writer.flush();
				socket.close();
			} catch (IOException e) {
				System.out.println("Ошибка в слушателе порта 6610:");
				e.printStackTrace();
			}
		}
		try {
			serverSocket.close();
		} catch (IOException ignored) {}
	}

	public static String process(List<String> requestLines, String ip) {
		if (requestLines.size() == 0) return "900";
		switch (requestLines.get(0)) {
			case "auth": {
				if (requestLines.size() < 3) return "901";
				String name = requestLines.get(1);
				if (name.length() == 0 || name.replaceAll("[a-zA-Z0-9]*", "").length() != 0) return "902";
				String uncryptedPass = requestLines.get(2);
				String hash = CryptoUtils.getHash(uncryptedPass);
				User u = User.load(name);
				if (u == null) return "904";
				if (!u.getPassword().equals(hash)) return "903";
				User.outAuth.put(name, ip);
				return "200";
			}
			default: return "905";
		}
	}

	public static void discontinue() {
		enabled = false;
	}

	public static void init() {
		try {
			serverSocket = new ServerSocket(6610);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		new RequestListener().start();
	}

}
