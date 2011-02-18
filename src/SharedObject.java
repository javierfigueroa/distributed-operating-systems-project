

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedObject{
	private int value = -1;

//	public synchronized void setValue(int value) {
//		this.value = value;	
//	}
//
//	public synchronized int getValue() {
//		return value;
//	}
//	
//	public synchronized void queue(IAction action) {
//		queue.offer(action);
//	}
//	
//	public synchronized int dequeue() {
//		if (queue.peek().getType() == 1) {
//			Write writer = (Write) queue.poll();
//			setValue(writer.getValue());
//			return writer.reply(226);
//		}else{
//			Read reader = (Read) queue.poll();
//			return reader.reply(226, String.valueOf(getValue()));
//		}		
//	}
	
//	AtomicInteger writeCounter = new AtomicInteger();
//
//	public V get(Object key) {
//	  int save = 0;
//	  V value = null;
//	  do {
//	    while (((save = writeCounter.get()) & 1) == 1);
//	    value = map.get(key);
//	  } while (save != writeCounter.get());
//	  return value;
//	}
//
//	Lock lock = new ReentrantLock();
//
//	public V put(K key, V value) {
//	  lock.lock();
//	  try {
//	    writeCounter.getAndIncrement();
//	    map.put(key, value);
//	    writeCounter.getAndIncrement();
//	  } finally {
//	    lock.unlock(); 
//	  }
//	  return value;
//	}


	public void setValue(int value) {
	  writeLock.lock();
	  try {
		Server.service++;
	    this.value = value;
	  } finally {
	    writeLock.unlock();
	  }
	}

	public int getValue() {
	  readLock.lock();
	  try {
		Server.service++;
	    return value;
	  } finally {
	    readLock.unlock();
	  }
	}
	
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	Lock readLock = lock.readLock();
	Lock writeLock = lock.writeLock();
	
//	private PriorityQueue<IAction> queue = new PriorityQueue<IAction>();
}
