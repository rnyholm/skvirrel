package ax.stardust.skvirrel.stock.indicator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;

import ax.stardust.skvirrel.exception.IndicatorException;
import ax.stardust.skvirrel.test.util.SkvirrelTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class RelativeStrengthIndexTest {

    @Test
    public void testCreate() {
        RelativeStrengthIndex rsi = RelativeStrengthIndex.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), null, 14);

        double[] results = rsi.getResults();
        assertEquals(63.33, results[14], SkvirrelTestUtils.DELTA);
        assertEquals(57.18, results[15], SkvirrelTestUtils.DELTA);
        assertEquals(60.66, results[16], SkvirrelTestUtils.DELTA);
        assertEquals(66.90, results[17], SkvirrelTestUtils.DELTA);
        assertEquals(63.25, results[18], SkvirrelTestUtils.DELTA);
        assertEquals(56.72, results[19], SkvirrelTestUtils.DELTA);
        assertEquals(56.72, rsi.getLastResult(), SkvirrelTestUtils.DELTA);

        rsi = RelativeStrengthIndex.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), BigDecimal.valueOf(35.89), 14);

        results = rsi.getResults();
        assertEquals(63.66, results[14], SkvirrelTestUtils.DELTA);
        assertEquals(66.71, results[15], SkvirrelTestUtils.DELTA);
        assertEquals(72.13, results[16], SkvirrelTestUtils.DELTA);
        assertEquals(68.09, results[17], SkvirrelTestUtils.DELTA);
        assertEquals(60.91, results[18], SkvirrelTestUtils.DELTA);
        assertEquals(64.2, results[19], SkvirrelTestUtils.DELTA);
        assertEquals(64.2, rsi.getLastResult(), SkvirrelTestUtils.DELTA);
    }

    @Test
    public void testCreateException() {
        try {
            RelativeStrengthIndex.create(null, null, 14);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }

        try {
            RelativeStrengthIndex.create(new ArrayList<>(), null, 14);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }

        try {
            RelativeStrengthIndex.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), BigDecimal.valueOf(35.89), 20);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }
    }
}
