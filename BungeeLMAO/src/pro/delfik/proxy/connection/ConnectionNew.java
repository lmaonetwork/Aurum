package pro.delfik.proxy.connection;


import pro.delfik.proxy.AurumPlugin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionNew extends Thread {
	private static final HashMap<String, String> map = new HashMap<>();
	private Socket socket;
	private InputReader in;
	private OutputWriter out;
	
	public ConnectionNew(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new InputReader();
		this.out = new OutputWriter();
	}
	
	private void exec() throws IOException {
		socket.setSoTimeout(1000);
		
		String packetRaw = readLine();
		Packet packet = new Packet(packetRaw);
		String result = packet.process();
		
		if (result != null) out.write(result);
		
		close();
	}
	
	public void run() {
		try {
			this.exec();
		} catch (IOException var2) {
			this.close();
		} catch (Throwable var3) {
			var3.printStackTrace();
		}
		this.close();
	}
	
	private boolean closeIfNotEnough(String[] args, int requiredAmount) {
		boolean b = args.length < requiredAmount;
		if (b) this.close();
		return b;
	}
	
	private void close() {
		try {
			this.out.close();
		} catch (IOException var3) {
			var3.printStackTrace();
		}
		
		try {
			this.socket.close();
		} catch (IOException ignored) {}
		
	}
	
	private String readLine() throws IOException {
		return this.in.readAll();
	}
	
	private class OutputWriter {
		private BufferedOutputStream out;
		private StringBuilder sb;
		
		public OutputWriter() throws IOException {
			this.out = new BufferedOutputStream(ConnectionNew.this.socket.getOutputStream());
			this.sb = new StringBuilder();
		}
		
		public void write(Object line) {
			this.sb.append(line.toString());
		}
		
		public void close() throws IOException {
			char[] var1 = AurumPlugin.getCryptoUtils().encrypt(this.sb.toString()).toCharArray();
			for (char c : var1) this.out.write(c);
			this.out.write(10);
			this.out.flush();
			this.out.close();
		}
	}
	
	private class InputReader {
		private BufferedReader in;
		
		public InputReader() throws IOException {
			this.in = new BufferedReader(new InputStreamReader(ConnectionNew.this.socket.getInputStream()));
		}
		
		public String readAll() throws IOException {
			String sb = this.in.readLine();
			return AurumPlugin.getCryptoUtils().decrypt(sb);
		}
	}
}
