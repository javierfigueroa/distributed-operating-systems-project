import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SharedObject {

	public int getValue() {
		int save = 0;
		int value;
		Server.readers++;
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
			Server.service++;
			writeCounter.getAndIncrement();
		} finally {
			lock.unlock();
		}
	}
	
	private int value = -1;
	AtomicInteger writeCounter = new AtomicInteger();
}
