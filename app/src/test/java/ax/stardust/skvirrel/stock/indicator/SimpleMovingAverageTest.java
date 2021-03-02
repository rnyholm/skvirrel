package ax.stardust.skvirrel.stock.indicator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import ax.stardust.skvirrel.exception.IndicatorException;
import ax.stardust.skvirrel.test.util.SkvirrelTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class SimpleMovingAverageTest {

    @Test
    public void testCreate() {
        SimpleMovingAverage sma = SimpleMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 14);

        double[] results = sma.getResults();
        assertEquals(0.00, results[0], SkvirrelTestUtils.DELTA);
        assertEquals(0.00, results[12], SkvirrelTestUtils.DELTA);
        assertEquals(34.93, results[13], SkvirrelTestUtils.DELTA);
        assertEquals(34.98, results[14], SkvirrelTestUtils.DELTA);
        assertEquals(35.03, results[15], SkvirrelTestUtils.DELTA);
        assertEquals(35.10, results[16], SkvirrelTestUtils.DELTA);
        assertEquals(35.20, results[17], SkvirrelTestUtils.DELTA);
        assertEquals(35.32, results[18], SkvirrelTestUtils.DELTA);
        assertEquals(35.38, results[19], SkvirrelTestUtils.DELTA);
        assertEquals(35.38, sma.getLastResult(), SkvirrelTestUtils.DELTA);

        sma = SimpleMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 20);

        results = sma.getResults();
        assertEquals(0.00, results[0], SkvirrelTestUtils.DELTA);
        assertEquals(0.00, results[12], SkvirrelTestUtils.DELTA);
        assertEquals(0.00, results[16], SkvirrelTestUtils.DELTA);
        assertEquals(35.15, results[19], SkvirrelTestUtils.DELTA);
        assertEquals(35.15, sma.getLastResult(), SkvirrelTestUtils.DELTA);
    }

    @Test
    public void testCreateException() {
        try {
            SimpleMovingAverage.create(null, 14);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }

        try {
            SimpleMovingAverage.create(new ArrayList<>(), 14);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }

        try {
            SimpleMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 21);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }
    }
}
