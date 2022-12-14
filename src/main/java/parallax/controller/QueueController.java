package parallax.controller;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the controller of triggered instance of application
 * 
 * @author victor
 *
 */
public class QueueController {
	private Map<Field, Queue<Object>> queues;

	public QueueController() {
		queues = new ConcurrentHashMap<>();
	}

	public synchronized void register(Field field) {
		if (this.queues.get(field) == null)
			this.queues.put(field, new LinkedList<>());
	}

	public synchronized void put(Object object, Field field) {
		Queue<Object> queue = this.queues.get(field);

		if (queue == null)
			throw new NoSuchElementException("this field not registered");

		queue.add(object);
	}

	public synchronized Optional<Object> poll(Field field) {
		Queue<Object> queue = this.queues.get(field);
		return queue == null ? Optional.empty() : Optional.ofNullable(queue.poll());
	}

	public synchronized Optional<Object> peek(Field field) {
		Queue<Object> queue = this.queues.get(field);
		return queue == null ? Optional.empty() : Optional.ofNullable(queue.peek());
	}

	public boolean exists(Field field) {
		Queue<Object> queue = this.queues.get(field);
		return queue != null && queue.size() > 0;
	}

}
