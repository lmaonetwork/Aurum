package pro.delfik.proxy.skins;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import pro.delfik.util.ReflectionUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SkinStorage {
	private static Class<?> property;
	private static File folder;
	private static ExecutorService exe;
	private static boolean isBungee = true;
	
	static {
		try {
			exe = Executors.newCachedThreadPool();
			property = Class.forName("com.mojang.authlib.properties.Property");
		} catch (Exception e) {
			try {
				property = Class.forName("net.md_5.bungee.connection.LoginResult$Property");
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static Object createProperty(String name, String value, String signature) {
		try {
			return ReflectionUtil.invokeConstructor(property, new Class[]{String.class, String.class, String.class}, name, value, signature);
		} catch (Exception ignored) {}
		return null;
	}
	
	public static ExecutorService getExecutor() {
		return exe;
	}
	
	public static Object getOrCreateSkinForPlayer(String name)
			throws MojangAPI.SkinRequestException {
		String skin = getPlayerSkin(name);
		if (skin == null) {
			skin = name.toLowerCase();
		}
		Object textures;
//		if (Config.DEFAULT_SKINS_ENABLED) {
//			textures = getSkinData((String) Config.DEFAULT_SKINS.get(new Random().nextInt(Config.DEFAULT_SKINS.size())));
//		}
		textures = getSkinData(skin);
		if (textures != null) {
			return textures;
		}
		String sname = skin;
		Object oldprops = textures;
		try {
			Object props;
			
			props = MojangAPI.getSkinProperty(MojangAPI.getUUID(sname));
			if (props == null) {
				return props;
			}
			boolean shouldUpdate;
			
			String value = Base64Coder.decodeString((String) ReflectionUtil.invokeMethod(props, "getValue"));
			
			String urlbeg = "url\":\"";
			String urlend = "\"}}";
			
			String newurl = MojangAPI.getStringBetween(value, urlbeg, urlend);
			try {
				value = Base64Coder.decodeString((String) ReflectionUtil.invokeMethod(oldprops, "getValue"));
				
				String oldurl = MojangAPI.getStringBetween(value, urlbeg, urlend);
				
				shouldUpdate = !oldurl.equals(newurl);
			} catch (Exception e) {
				shouldUpdate = true;
			}
			setSkinData(sname, props);
			if (shouldUpdate) SkinApplier.applySkin(name);
		} catch (Exception e) {
			throw new MojangAPI.SkinRequestException("Подождите минутку...");
		}
		return textures;
	}
	
	public static String getPlayerSkin(String name) {
		name = name.toLowerCase();
		File playerFile = new File(folder.getAbsolutePath() + File.separator + "Players" + File.separator + name + ".player");
		try {
			if (!playerFile.exists()) {
				return null;
			}
			BufferedReader buf = new BufferedReader(new FileReader(playerFile));
			
			String skin = null;
			String line;
			if ((line = buf.readLine()) != null) {
				skin = line;
			}
			buf.close();
			if (skin.equalsIgnoreCase(name)) {
				playerFile.delete();
			}
			return skin;
		} catch (Exception ignored) {}
		return name;
	}
	
	public static Object getSkinData(String name) {
		name = name.toLowerCase();
		File skinFile = new File(folder.getAbsolutePath() + File.separator + "Skins" + File.separator + name + ".skin");
		try {
			if (!skinFile.exists()) {
				return null;
			}
			BufferedReader buf = new BufferedReader(new FileReader(skinFile));
			
			String value = "";
			String signature = "";
			String timestamp = "";
			for (int i = 0; i < 3; i++) {
				String line;
				if ((line = buf.readLine()) != null) {
					if (value.isEmpty()) {
						value = line;
					} else if (signature.isEmpty()) {
						signature = line;
					} else {
						timestamp = line;
					}
				}
			}
			buf.close();
			if (isOld(Long.valueOf(timestamp))) {
				Object skin = MojangAPI.getSkinProperty(MojangAPI.getUUID(name));
				if (skin != null) {
					setSkinData(name, skin);
				}
			}
			return createProperty("textures", value, signature);
		} catch (Exception e) {
			removeSkinData(name);
			System.out.println("[SkinsRestorer] Unsupported player format.. removing (" + name + ").");
		}
		return null;
	}
	
	public static boolean isOld(long timestamp) {
		return timestamp + TimeUnit.MINUTES.toMillis(1584) <= System.currentTimeMillis();
	}
	
	public static void init(File pluginFolder) {
		folder = pluginFolder;
		File tempFolder = new File(folder.getAbsolutePath() + File.separator + "Skins" + File.separator);
		tempFolder.mkdirs();
		tempFolder = new File(folder.getAbsolutePath() + File.separator + "Players" + File.separator);
		tempFolder.mkdirs();
	}
	
	public static void removePlayerSkin(String name) {
		name = name.toLowerCase();
		File playerFile = new File(folder.getAbsolutePath() + File.separator + "Players" + File.separator + name + ".player");
		if (playerFile.exists()) playerFile.delete();
	}
	
	public static void removeSkinData(String name) {
		name = name.toLowerCase();
		File skinFile = new File(folder.getAbsolutePath() + File.separator + "Skins" + File.separator + name + ".skin");
		if (skinFile.exists()) skinFile.delete();
	}
	
	public static void setPlayerSkin(String name, String skin) {
		name = name.toLowerCase();
		File playerFile = new File(folder.getAbsolutePath() + File.separator + "Players" + File.separator + name + ".player");
		try {
			if ((skin.equalsIgnoreCase(name)) && (playerFile.exists())) {
				playerFile.delete();
				return;
			}
			if (!playerFile.exists()) {
				playerFile.createNewFile();
			}
			FileWriter writer = new FileWriter(playerFile);
			
			writer.write(skin);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setSkinData(String name, Object textures) {
		name = name.toLowerCase();
		String value = "";
		String signature = "";
		String timestamp = "";
		try {
			value = (String) ReflectionUtil.invokeMethod(textures, "getValue");
			signature = (String) ReflectionUtil.invokeMethod(textures, "getSignature");
			timestamp = String.valueOf(System.currentTimeMillis());
		} catch (Exception ignored) {}
		File skinFile = new File(folder.getAbsolutePath() + File.separator + "Skins" + File.separator + name + ".skin");
		try {
			if ((value.isEmpty()) || (signature.isEmpty()) || (timestamp.isEmpty())) {
				return;
			}
			if (!skinFile.exists()) {
				skinFile.createNewFile();
			}
			FileWriter writer = new FileWriter(skinFile);
			
			writer.write(value + "\n" + signature + "\n" + timestamp);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Object> getSkins(int number) {
		ConcurrentHashMap<String, Object> thingy = new ConcurrentHashMap<>();
		Map<String, Object> list = new TreeMap<>(thingy);
		String path = "Core/Skins/";
		File folder = new File(path);
		String[] fileNames = folder.list();
		int i = 0;
		for (String file : fileNames) {
			if (i >= number) {
				list.put(file.replace(".skin", ""), getSkinDataMenu(file.replace(".skin", "")));
			}
			i++;
		}
		return list;
	}
	
	public static Object getSkinDataMenu(String name) {
		name = name.toLowerCase();
		File skinFile = new File(folder.getAbsolutePath() + File.separator + "Skins" + File.separator + name + ".skin");
		try {
			if (!skinFile.exists()) {
				return null;
			}
			BufferedReader buf = new BufferedReader(new FileReader(skinFile));
			
			String value = "";
			String signature = "";
			String timestamp = "";
			for (int i = 0; i < 3; i++) {
				String line;
				if ((line = buf.readLine()) != null) {
					if (value.isEmpty()) {
						value = line;
					} else if (signature.isEmpty()) {
						signature = line;
					}
				}
			}
			buf.close();
			return createProperty("textures", value, signature);
		} catch (Exception e) {
			System.out.println("[SkinsRestorer] Unsupported player format.. removing (" + name + ").");
		}
		return null;
	}
}
