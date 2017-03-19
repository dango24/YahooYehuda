package delay.memory.engine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Dan on 3/17/2017.
 */
public class CacheEngine<K, V> implements CachingDelayService<K, V> {

    // Fields
    private AtomicBoolean isRunning;
    private Map<K, V> dataCache;
    private Queue<V> removableData;
    private ExecutorService cleanerTask;
    private BlockingQueue<DelayData<K>> delayDataQueue;

    // Constructor
    public CacheEngine() {
        isRunning = new AtomicBoolean(true);
        removableData = new LinkedList<>();
        delayDataQueue = new DelayQueue<>();
        dataCache = new ConcurrentHashMap<>();
        cleanerTask = Executors.newSingleThreadExecutor();

        cleanerTask.submit(this::clearTimeOutData);
    }

    // Methods

    private void clearTimeOutData() {
        V removedDataValue;

        while (isRunning.get()) {

            try {
                removedDataValue = dataCache.remove(delayDataQueue.take().getKey());
                removableData.add(removedDataValue);
                System.out.println("Data removed: " + removedDataValue);

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                isRunning.set(false);
            }
        }
    }

    @Override
    public Queue<V> getAllRemovedData() {
        return removableData;
    }

    @Override
    public boolean isEmpty() {
        return delayDataQueue.isEmpty();
    }

    @Override
    public void put(K key, V data, long timeToLeave) {
        dataCache.put(key, data);
        delayDataQueue.add(new DelayData<>(key, timeToLeave));
    }

    @Override
    public void put(K key, V data, long timeToLeave, TimeUnit timeUnit) {
        put(key, data, timeUnit.toMillis(timeToLeave));
    }

    @Override
    public V get(K key) {
        return dataCache.get(key);
    }

    @Override
    public void close() throws IOException {
        isRunning.set(false);
        cleanerTask.shutdownNow();
    }

    private class DelayData<K> implements Delayed {

        // Fields
        private K key;
        private long delayTime;

        // Constructor
        public DelayData(K key, long delay) {
            this.key = key;
            this.delayTime = System.currentTimeMillis() + delay;
        }

        public K getKey() {
            return key;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return delayTime - System.currentTimeMillis();
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.delayTime < ((DelayData)o).delayTime) {
                return -1;
            } else if (this.delayTime > ((DelayData) o).delayTime) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}