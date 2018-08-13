package pro.delfik.util;

import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.Aurum;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class TimedList<T> extends ArrayList<T> {
	private final int time;

	public TimedList(int time){
		this.time = time;
	}

	public boolean add(T t){
		BungeeCord.getInstance().getScheduler().schedule(Aurum.instance, () -> super.remove(t), time, TimeUnit.SECONDS);
		return super.add(t);
	}
}
