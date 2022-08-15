package ai4j.classes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

public class QueueController {
	private HashMap<Field, Queue<Object>> queues;

	public QueueController() {
		queues = new HashMap<>();
	}

	public synchronized void register(Field field) {
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
