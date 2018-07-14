package pro.delfik.proxy.skins;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import pro.delfik.util.ReflectionUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class SkinApplier {
	private static Class<?> loginresul;
	
	public static void applySkin(ProxiedPlayer p) {
		new Thread(new Runnable() {
			public void run() {
				try {
					LoginResult.Property textures = (LoginResult.Property) SkinStorage.getOrCreateSkinForPlayer(p.getName());
					
					InitialHandler handler = (InitialHandler) p.getPendingConnection();
					if (handler.isOnlineMode()) {
						SkinApplier.sendUpdateRequest(p, textures);
						return;
					}
					LoginResult profile = new LoginResult(p.getUniqueId().toString(), p.getName(), new LoginResult.Property[]{textures});
					LoginResult.Property[] present = profile.getProperties();
					LoginResult.Property[] newprops = new LoginResult.Property[present.length + 1];
					System.arraycopy(present, 0, newprops, 0, present.length);
					newprops[present.length] = textures;
					profile.getProperties()[0].setName(newprops[0].getName());
					profile.getProperties()[0].setValue(newprops[0].getValue());
					profile.getProperties()[0].setSignature(newprops[0].getSignature());
					ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);
					SkinApplier.sendUpdateRequest(p, null);
				} catch (Exception ignored) {
				}
			}
		}).start();
	}
	
	public static void applySkin(String pname) {
		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(pname);
		if (p != null) {
			applySkin(p);
		}
	}
	
	public static void init() {
		try {
			loginresul = ReflectionUtil.getBungeeClass("connection", "loginresul");
		} catch (Exception ignored) {}
	}
	
	private static void sendUpdateRequest(ProxiedPlayer p, LoginResult.Property textures) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("SkinUpdate");
			if (textures != null) {
				out.writeUTF(textures.getName());
				out.writeUTF(textures.getValue());
				out.writeUTF(textures.getSignature());
			}
			p.getServer().sendData("SkinsRestorer", b.toByteArray());
		} catch (Exception ignored) {}
	}
}
