package ax.stardust.skvirrel.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class IndicatorCacheTest {

    private static final String TICKER = "Test";
    private static final long ID = 1;

    @Test
    public void testSetExpires() {
        IndicatorCache indicatorCache = new IndicatorCache(ID, TICKER);
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();

        indicatorCache.setExpires(timeInMillis);
        assertEquals(timeInMillis, indicatorCache.getExpires().getTime());

        indicatorCache.setExpires(calendar.getTime());
        assertTrue(indicatorCache.getExpires().getTime() > timeInMillis);
    }

    @Test
    public void testNeedsRefresh() {
        IndicatorCache indicatorCache = new IndicatorCache(ID, TICKER);
        assertTrue(indicatorCache.needsRefresh());

        indicatorCache.setSma(1.1);
        assertTrue(indicatorCache.needsRefresh());

        indicatorCache.setEma(3.4);
        assertTrue(indicatorCache.needsRefresh());

        indicatorCache.setRsi(5.2);
        assertTrue(indicatorCache.needsRefresh());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -10);

        indicatorCache.setExpires(calendar.getTime());
        assertTrue(indicatorCache.needsRefresh());

        calendar.add(Calendar.HOUR, 20);

        indicatorCache.setExpires(calendar.getTime());
        assertFalse(indicatorCache.needsRefresh());
    }

    @Test
    public void testHasExpired() {
        IndicatorCache indicatorCache = new IndicatorCache(ID, TICKER);
        assertTrue(indicatorCache.hasExpired());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -10);

        indicatorCache.setExpires(calendar.getTime());
        assertTrue(indicatorCache.hasExpired());

        calendar.add(Calendar.HOUR, 20);

        indicatorCache.setExpires(calendar.getTime());
        assertFalse(indicatorCache.hasExpired());
    }

    @Test
    public void testIsMissingData() {
        IndicatorCache indicatorCache = new IndicatorCache(ID, TICKER);
        assertTrue(indicatorCache.isMissingData());

        indicatorCache.setSma(1.1);
        assertTrue(indicatorCache.isMissingData());

        indicatorCache.setEma(3.4);
        assertTrue(indicatorCache.isMissingData());

        indicatorCache.setRsi(5.2);
        assertTrue(indicatorCache.isMissingData());

        indicatorCache.setExpires(Calendar.getInstance().getTime());
        assertFalse(indicatorCache.isMissingData());
    }
}
