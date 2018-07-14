package pro.delfik.util;

import net.md_5.bungee.BungeeCord;
import pro.delfik.proxy.AurumPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimedList<T> implements Iterable{
	private final List<T> list = new ArrayList<>(100);
	
	private final int time;
	
	public TimedList(int time){
		this.time = time;
	}
	
	public void add(T t){
		list.add(t);
		BungeeCord.getInstance().getScheduler().schedule(AurumPlugin.instance, () -> remove(t), time, TimeUnit.SECONDS);
	}
	
	public void remove(T t){
		list.remove(t);
	}
	
	@Override
	public Iterator iterator() {
		return list.iterator();
	}
}
