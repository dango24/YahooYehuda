package delay.memory.engine;

import java.io.Closeable;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dan on 3/17/2017.
 */
public interface CachingDelayService<K, T> extends Closeable {
    boolean isEmpty();
    void put(K key, T data, long timeToLeaveInMs);
    void put(K key, T data, long timeToLeave, TimeUnit timeUnit);
    T get(K key);
    Queue<T> getAllRemovedData();
}
