import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedObject {

	public int getValue() {
		
		int save = 0;
		int value;

		Server.readers++;
		do {

			while (((save = writeCounter.get()) & 1) == 1)
				;
			value = this.value;
			Server.service++;
		} while (save != writeCounter.get());

		return value;
	}

	Lock lock = new ReentrantLock();

	public void setValue(int value) {
		lock.lock();
		try {
			writeCounter.getAndIncrement();
			this.value = value;
			Server.service++;
			writeCounter.getAndIncrement();
		} finally {
			lock.unlock();
		}
	}
	

//	public void setValue(int value) {
//		writeLock.lock();
//		try {
//			this.value = value;
//		} finally {
//			Server.service++;
//			writeLock.unlock();
//		}
//	}
//
//	public int getValue() {
//		readLock.lock();
//		try {
//			return value;
//		} finally {
//			Server.readers++;
//			Server.service++;
//			readLock.unlock();
//		}
//	}

//	 ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//	 Lock readLock = lock.readLock();
//	 Lock writeLock = lock.writeLock();
	private int value = -1;
	AtomicInteger writeCounter = new AtomicInteger();
}
