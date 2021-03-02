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
public class ExponentialMovingAverageTest {

    @Test
    public void testCreate() {
        ExponentialMovingAverage ema = ExponentialMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 14);

        double[] results = ema.getResults();
        assertEquals(0.00, results[0], SkvirrelTestUtils.DELTA);
        assertEquals(0.00, results[12], SkvirrelTestUtils.DELTA);
        assertEquals(34.93, results[13], SkvirrelTestUtils.DELTA);
        assertEquals(35.02, results[14], SkvirrelTestUtils.DELTA);
        assertEquals(35.04, results[15], SkvirrelTestUtils.DELTA);
        assertEquals(35.10, results[16], SkvirrelTestUtils.DELTA);
        assertEquals(35.25, results[17], SkvirrelTestUtils.DELTA);
        assertEquals(35.35, results[18], SkvirrelTestUtils.DELTA);
        assertEquals(35.37, results[19], SkvirrelTestUtils.DELTA);
        assertEquals(35.37, ema.getLastResult(), SkvirrelTestUtils.DELTA);

        ema = ExponentialMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 19);
        System.out.println(ema);

        results = ema.getResults();
        assertEquals(0.00, results[0], SkvirrelTestUtils.DELTA);
        assertEquals(0.00, results[12], SkvirrelTestUtils.DELTA);
        assertEquals(0.00, results[17], SkvirrelTestUtils.DELTA);
        assertEquals(35.13, results[18], SkvirrelTestUtils.DELTA);
        assertEquals(35.17, results[19], SkvirrelTestUtils.DELTA);
        assertEquals(35.17, ema.getLastResult(), SkvirrelTestUtils.DELTA);
    }

    @Test
    public void testCreateException() {
        try {
            ExponentialMovingAverage.create(null, 14);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }

        try {
            ExponentialMovingAverage.create(new ArrayList<>(), 14);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }

        try {
            ExponentialMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 20);
            fail("Exception should have been thrown");
        } catch (IndicatorException ignore) {
            // exception is expected
        }
    }
}