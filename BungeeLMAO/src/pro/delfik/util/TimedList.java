package pro.delfik.util;

import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.AurumPlugin;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TimedList<T> extends ArrayList<T> {
	private final int time;
	public TimedList(int time){
		super();
		this.time = time;
	}
	public boolean add(T t){
		BungeeCord.getInstance().getScheduler().schedule(AurumPlugin.instance, () -> super.remove(t), time, TimeUnit.SECONDS);
		return super.add(t);
	}
}
