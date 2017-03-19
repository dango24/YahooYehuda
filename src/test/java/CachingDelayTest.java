import delay.memory.engine.CacheEngine;
import delay.memory.engine.CachingDelayService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Dan on 3/12/2017.
 */
public class CachingDelayTest {

    // Constants
    private static int EXTRA_SAFE_TIME = 1000;

    // Fields
    private CachingDelayService<String, Integer> cachingDelayService;

    @Before
    public void setUp() throws Exception {
        cachingDelayService = new CacheEngine<>();
    }

    @After
    public void tearDown() throws Exception {
        cachingDelayService.close();
    }

    // Tests

    @Test
    public void testKeyValueDelay() {
        cachingDelayService.put("A", 1, 10_000);
        cachingDelayService.put("B", 2, 10_000);
        cachingDelayService.put("C", 3, 10_000);

        assertTrue(1 == cachingDelayService.get("A"));
        assertTrue(2 == cachingDelayService.get("B"));
        assertTrue(3 == cachingDelayService.get("C"));
    }

    @Test
    public void testKeyValueDelay1() {
        cachingDelayService.put("A", 1, 3000);
        cachingDelayService.put("B", 2, 1500);
        cachingDelayService.put("C", 3, 2000);

        sleepingBeauty(4000);
        assertTrue(cachingDelayService.isEmpty());
    }

    @Test
    public void testKeyValueDelay2() {
        cachingDelayService.put("A", 1, 10_000);
        cachingDelayService.put("B", 2, 1500);
        cachingDelayService.put("C", 3, 2000);

        sleepingBeauty(3000);
        assertTrue(2 == cachingDelayService.getAllRemovedData().size());
    }

    @Test
    public void testKeyValueDelay3() {
        cachingDelayService.put("A", 1, 10_000);
        cachingDelayService.put("B", 2, 1500);
        cachingDelayService.put("C", 3, 3000);
        cachingDelayService.put("C", 3, 7000);

        sleepingBeauty(4000);
        assertTrue(2 == cachingDelayService.getAllRemovedData().size());
    }

    @Test
    public void testKeyValueDelay4() {
        cachingDelayService.put("B", 2, 1500);
        cachingDelayService.put("C", 3, 3000);
        cachingDelayService.put("A", 3, 7000);

        sleepingBeauty(4000);
        assertEquals(null, cachingDelayService.get("B"));
    }

    @Test
    public void testKeyValueDelay5() {
        cachingDelayService.put("B", 2, 1, TimeUnit.SECONDS);
        cachingDelayService.put("C", 3, 3, TimeUnit.SECONDS);
        cachingDelayService.put("A", 3, 7 , TimeUnit.SECONDS);

        sleepingBeauty(4000);
        assertEquals(null, cachingDelayService.get("B"));
    }

    @Test
    public void testKeyValueDelay6() {
        cachingDelayService.put("B", 2, 1, TimeUnit.SECONDS);
        cachingDelayService.put("C", 3, 3, TimeUnit.SECONDS);
        cachingDelayService.put("A", 3, 7 , TimeUnit.SECONDS);

        sleepingBeauty(4000);
        assertTrue(cachingDelayService.getAllRemovedData().size() == 2);
    }

    @Test
    public void testKeyValueDelay7() {
        cachingDelayService.put("B", 2, 1, TimeUnit.SECONDS);
        cachingDelayService.put("C", 3, 3, TimeUnit.SECONDS);
        cachingDelayService.put("A", 3, 7 , TimeUnit.SECONDS);

        assertTrue(2 == cachingDelayService.get("B"));
        assertTrue(3 == cachingDelayService.get("C"));
    }


    private void sleepingBeauty(int timeToSleep) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeToSleep + EXTRA_SAFE_TIME);
        } catch (InterruptedException e) {
            System.out.println("Thread sleep interrupted " + e.getMessage());
        }
    }

}