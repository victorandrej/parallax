package parallax.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * an manager who create threads and won't let exceed max of threads;
 * 
 * @author victor
 *
 */
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

	/**
	 * verify if has a free thread to start and start it
	 */
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
