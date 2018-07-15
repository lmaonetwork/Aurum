package pro.delfik.proxy.data;


import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import pro.delfik.proxy.AurumPlugin;
import pro.delfik.proxy.Proxy;
import pro.delfik.proxy.command.handling.Bans;
import pro.delfik.proxy.command.handling.BansIP;
import pro.delfik.proxy.command.handling.CommandKick;
import pro.delfik.proxy.command.handling.Mutes;
import pro.delfik.proxy.games.SfTop;
import pro.delfik.proxy.permissions.Person;
import pro.delfik.util.Rank;
import pro.delfik.util.Converter;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class Connection extends Thread {
	private static final HashMap<String, String> map = new HashMap<>();
	private Socket socket;
	private Connection.a in;
	private Connection.b out;
	
	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		this.in = new Connection.a();
		this.out = new Connection.b();
	}
	
	private void exec() throws IOException {
		this.socket.setSoTimeout(1000);
		String[] keys = in.readAll().split(" ");
		if (!keys(keys, 2)) {
			String key = keys[0];
			if (key.equals("get")) {
				Map<String, String> in = DataIO.readConfig(keys[1]);
				if (in == null) {
					this.out.write("null");
					return;
				}
				for (Map.Entry<String, String> entry : in.entrySet()) this.out.write(entry.getKey() + "/" + entry.getValue() + "\n");
			} else {
				int i;
				if (key.equals("write")) {
					Map<String, String> map = new HashMap<>();
					
					for (i = 2; i < keys.length; ++i) {
						String[] s = keys[i].split("/");
						if (s.length == 2) map.put(s[0], s[1]);
					}
					DataIO.writeConfig(keys[1], map);
				} else if (key.equals("remove")) {
					DataIO.remove(keys[1]);
				} else if (key.equals("getall")) {
					File[] files = DataIO.getAll(keys[1]);
					if (files == null) return;
					StringBuilder builder = new StringBuilder();
					builder.append("files/");
					for (File file : files) {
						builder.append(file.getName()).append("}");
					}
					
					out.write(builder.toString());
				} else {
					Person user;
					if (key.equals("pex")) {
						if (this.keys(keys, 3)) {
							return;
						}
						
						user = Person.get(keys[1]);
						Rank rank;
						if (user == null) {
							out.write("n");
							return;
						} else {
							rank = user.getRank();
							ProxiedPlayer player = user.getHandle();
							if (player != null) {
								Server server = player.getServer();
								if (server != null && !user.getServer().equals(keys[2])) {
									out.write("n");
									return;
								}
							}
						}
						
						out.write(rank + "");
					} else if (key.equals("memget")) {
						out.write(map.get(keys[1]));
					} else if (key.equals("memset")) {
						if (keys(keys, 3)) {
							return;
						}
						map.put(keys[1], keys[2]);
					} else if (key.equals("memrem")) {
						map.remove(keys[1]);
					} else if (key.equals("getauth")) {
						if (keys(keys, 3)) return;
						user = Person.get(keys[1]);
						boolean b = false;
						try {
							if (user.getHandle().getServer().getInfo().getName().equals(keys[2]) && user.isAuthorized())
								b = true;
						} catch (Exception ignored) {}
						out.write(b + "");
					} else if (key.equals("broadevent")) {
						if (keys(keys, 3)) return;
						DataEvent.broadevent(keys[1], keys[2]);
					} else if (key.equals("event")) {
						if (keys(keys, 4)) return;
						DataEvent.event(keys[1], keys[2], keys[3]);
					} else if (key.equals("bungeeevent")) {
						if (keys(keys, 3)) return;
						BungeeCord.getInstance().pluginManager.callEvent(new SocketEvent(keys[1], keys[2]));
					} else if (key.equals("typeevent")) {
						if (keys(keys, 4)) return;
						DataEvent.typeevent(keys[1], keys[2], keys[3]);
					} else if (key.equals("getport")) {
						this.out.write((char) DataPort.putPort(keys[1]) + "");
					} else if (key.equals("getfile")) {
						BufferedInputStream in = null;
						try {
							in = new BufferedInputStream(new FileInputStream(System.getProperty("user.dir") + "/" + keys[1]));
							while (true) {
								i = in.read();
								if (i == -1) {
									in.close();
									break;
								}
								out.write((char) i + "");
							}
						} catch (IOException ex) {
							try {in.close();} catch (IOException ignored) {}
						}
					} else if (key.equals("remport")) {
						DataPort.remPort(keys[1]);
					} else if (key.equals("top")) {
						SfTop.checkTop(keys[1]);
					} else if (key.equals("gettop")) {
						out.write(SfTop.getAllTop());
					} else if (key.equals("writeReal")) {
						List<String> list = new ArrayList<>(keys.length - 2);
						for (i = 2; i < keys.length; ++i) list.add(keys[i]);
						DataIO.write(keys[1], list);
					} else if (key.equals("readReal")) {
						List<String> in = DataIO.read(keys[1]);
						if (in == null) {
							out.write("null");
							return;
						}
						for (i = 0; i < in.size(); ++i) {
							out.write(in.get(i));
							out.write("\n");
						}
					} else if (key.equals("servers")) {
						LinkedHashSet<String> result = new LinkedHashSet<>();
						for (ServerInfo info : Proxy.i().getServers().values()) {
							String line = info.getName() + "@" + info.getPlayers().size() + "\n";
							result.add(line);
							Proxy.ifServerOffline(info,
									() -> DataEvent.event(keys[1], "SSU-offline", info.getName()),
									(ping) -> DataEvent.event(keys[1], "SSU-online", info.getName()));
						}
						String[] array = result.toArray(new String[] {});
						Arrays.sort(array);
						for (String s : array) {
							System.out.println(s);
							out.write(s);
						}
					} else if (key.equals("earn")) {
						if (this.keys(keys, 3)) return;
						Person.get(keys[1]).earn(Converter.toInt(keys[2]));
					} else if (key.equals("disburse")) {
						if (this.keys(keys, 3)) return;
						Person.get(keys[1]).disburse(Converter.toInt(keys[2]));
					} else if (key.equals("getMoney")) {
						this.out.write(Person.get(keys[1]).getMoney() + "");
					} else if (key.equals("mute")) {
						Mutes.mute(keys[1], Converter.mergeArray(keys, 4, " "), Integer.parseInt(keys[3]), keys[2]);
					} else if (key.equals("ban")) {
						Bans.ban(keys[1], Converter.mergeArray(keys, 4, " "), Integer.parseInt(keys[3]), keys[2]);
					} else if (key.equals("kick")) {
						ProxiedPlayer p = Proxy.getPlayer(keys[1]);
						if (p == null) return;
						CommandKick.kick(p, keys[2], Converter.mergeArray(keys, 3, " "));
					} else if (key.equals("unmute")) {
						Mutes.unmute(keys[1], keys[2]);
					} else if (key.equals("unban")) {
						Bans.unban(keys[1], keys[2]);
					} else if (key.equals("ban-ip")) {
						BansIP.banIP(keys[1], keys[2], Converter.mergeArray(keys, 3, " '"));
					} else if (key.equals("ban-playerip")) {
						BansIP.banPlayer(keys[1], keys[2], Converter.mergeArray(keys, 3, " '"));
					} else if (key.equals("unban-ip")) {
						BansIP.unbanIP(keys[1], keys[2]);
					} else if (key.equals("unban-playerip")) {
						BansIP.unbanNickname(keys[1], keys[2]);
					} else if (key.equals("online")) {
						this.out.write(String.valueOf(Proxy.getPlayer(keys[1]) != null));
					}
				}
			}
			
		}
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
	
	private boolean keys(String[] keys, int i) {
		if (keys.length < i) {
			this.close();
			return true;
		} else {
			return false;
		}
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
	
	private class b {
		private BufferedOutputStream out;
		private StringBuilder sb;
		
		public b() throws IOException {
			this.out = new BufferedOutputStream(Connection.this.socket.getOutputStream());
			this.sb = new StringBuilder();
		}
		
		public void write(String line) {
			this.sb.append(line);
		}
		
		public void close() throws IOException {
			char[] var1 = AurumPlugin.getCryptoUtils().encrypt(this.sb.toString()).toCharArray();
			for (char c : var1) this.out.write(c);
			this.out.write(10);
			this.out.flush();
			this.out.close();
		}
	}
	
	private class a {
		private BufferedReader in;
		
		public a() throws IOException {
			this.in = new BufferedReader(new InputStreamReader(Connection.this.socket.getInputStream()));
		}
		
		public String readAll() throws IOException {
			String sb = this.in.readLine();
			return AurumPlugin.getCryptoUtils().decrypt(sb);
		}
	}
}
