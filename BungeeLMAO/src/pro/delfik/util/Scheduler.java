package pro.delfik.util;

import java.util.ArrayList;
import java.util.List;

public class Scheduler extends Thread{
	private static List<Task> tasks = new ArrayList<>();
	private int times = 0;

	public static void init(){
		new Scheduler().start();
	}

	@Override
	public void run() {
		while (true){
			sleep(100);
			if(++times == 11)times = 0;
			for(Task task : tasks)
				if(times % task.times == 0)task.run();
		}
	}

	public static void addTask(Task task){
		tasks.add(task);
	}

	public static void runThr(Runnable runnable){
		new Thread(){
			@Override
			public void run(){
				try{
					runnable.run();
				}catch (Throwable ignored){}
				this.stop();
			}
		}.start();
	}

	public static void sleep(int millis){
		try{
			Thread.sleep(millis);
		}catch (InterruptedException ex){
			throw new RuntimeException(ex);
		}
	}

	public static abstract class Task implements Runnable{
		private final int times;

		protected Task(int times){
			this.times = times;
		}
	}
}
