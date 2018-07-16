package pro.delfik.util;

import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.AurumPlugin;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TimedHashMap<K, V> extends HashMap<K, V> {
	private final int time;
	public TimedHashMap(int time){
		super();
		this.time = time;
	}
	public V put(K key, V value){
		BungeeCord.getInstance().getScheduler().schedule(AurumPlugin.instance, () -> super.remove(key), time, TimeUnit.SECONDS);
		return super.put(key, value);
	}
}
