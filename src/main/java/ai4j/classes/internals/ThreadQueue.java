package ai4j.classes.internals;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadQueue {
	private Queue<Thread> queue;

	public ThreadQueue() {
		this.queue = new ConcurrentLinkedQueue<>();
	}

	public  void register() {
		queue.add(Thread.currentThread());

	}

	public void threadWait() {
		while (true) {
			if (this.myTurn())
				break;

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	private synchronized boolean myTurn() {
		return this.queue.peek().equals(Thread.currentThread());
	}

	public void exit() {
		this.queue.poll();
	}
}
