package br.com.victorandrej.jia.classes;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Primitive {

	private Queue<Object> history;
	private boolean isNewStory;
	Random random = new Random();

	public Primitive() {
		this.isNewStory = false;
	}

	public Primitive(Queue<?> history) {
		this.isNewStory = true;
		this.history = new LinkedList<>();
	}

	protected Queue<Object> getHistory() {
		return history;
	}

	public int getInteger() {
		int value;

		if (isNewStory) {
			value = random.nextInt();
			this.history.add(value);
		} else {
			value = (int) this.history.poll();
		}

		return value;

	}

	public int getInteger(int bound) {
		int value;

		if (isNewStory) {
			value = random.nextInt(bound);
			this.history.add(value);
		} else {
			value = (int) this.history.poll();
		}

		return value;

	}

	public double getDouble() {
		double value;

		if (isNewStory) {
			value = random.nextDouble();
			this.history.add(value);
		} else {
			value = (double) this.history.poll();
		}

		return value;

	}

	public double getDouble(double bound) {
		double value;

		if (isNewStory) {
			value = random.nextDouble(bound);
			this.history.add(value);
		} else {
			value = (double) this.history.poll();
		}

		return value;
	}

	public long getlong() {
		long value;

		if (isNewStory) {
			value = random.nextLong();
			this.history.add(value);
		} else {
			value = (long) this.history.poll();
		}

		return value;
	}

	public long getlong(long bound) {
		long value;

		if (isNewStory) {
			value = random.nextLong(bound);
			this.history.add(value);
		} else {
			value = (long) this.history.poll();
		}

		return value;
	}

	public float getFloat() {
		float value;

		if (isNewStory) {
			value = random.nextFloat();
			this.history.add(value);
		} else {
			value = (float) this.history.poll();
		}

		return value;
	}

	public float getFloat(float bound) {
		float value;

		if (isNewStory) {
			value = random.nextFloat(bound);
			this.history.add(value);
		} else {
			value = (float) this.history.poll();
		}

		return value;
	}

	public boolean getBoolean() {
		boolean value;

		if (isNewStory) {
			value = random.nextInt(1) == 1;
			this.history.add(value);
		} else {
			value = (boolean) this.history.poll();
		}

		return value;
	}

}
