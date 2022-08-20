package ai4j.classes.internals;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadManager {
	private Queue<Runnable> runnables;
	private int maxTrhreads;
	private volatile int threadCount;

	public ThreadManager(int maxThreads) {
		this.maxTrhreads = maxThreads;
		this.runnables = new ConcurrentLinkedQueue<>();
		this.threadCount = 0;
	}

	public void put(Runnable runable) {
		this.runnables.add(runable);
	}

	public synchronized void verify() {
		while (runnables.size() > 0 && (maxTrhreads < 0 || threadCount < maxTrhreads)) {
			threadCount++;
			new Thread(() -> {
				Runnable runnable = runnables.poll();
				if (runnable == null)
					return;
				runnable.run();
				threadCount--;
				System.gc();
			}).start();
		}

	}
}
