import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedObject {

	AtomicInteger writeCounter = new AtomicInteger();

	public int getValue() {
		int save = 0;
		int value;
		do {
			while (((save = writeCounter.get()) & 1) == 1)
				;
			value = this.value;
		} while (save != writeCounter.get());

		Server.service++;
		return value;
	}

	Lock lock = new ReentrantLock();

	public void setValue(int value) {
		lock.lock();
		try {
			writeCounter.getAndIncrement();
			this.value = value;
			writeCounter.getAndIncrement();
		} finally {

			Server.service++;
			lock.unlock();
		}
	}

	// public void setValue(int value) {
	// writeLock.lock();
	// try {
	// Server.increase();
	// this.value = value;
	// } finally {
	// writeLock.unlock();
	// }
	// }
	//
	// public int getValue() {
	// readLock.lock();
	// try {
	// Server.increase();
	// return value;
	// } finally {
	// readLock.unlock();
	// }
	// }

	// ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	// Lock readLock = lock.readLock();
	// Lock writeLock = lock.writeLock();
	private int value = -1;
	// private PriorityQueue<IAction> queue = new PriorityQueue<IAction>();
}
