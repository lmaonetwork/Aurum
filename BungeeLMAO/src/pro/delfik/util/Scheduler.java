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
			try{
				sleep(100);
			}catch (InterruptedException ex){
				return; //Сервер упал, или остановился
			}
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
				}catch (Throwable ex){}
				this.stop();
			}
		}.start();
	}

	public static abstract class Task implements Runnable{
		private final int times;

		protected Task(int times){
			this.times = times;
		}
	}
}
