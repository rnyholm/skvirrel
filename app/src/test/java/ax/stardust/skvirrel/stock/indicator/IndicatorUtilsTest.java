package ax.stardust.skvirrel.stock.indicator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.List;

import ax.stardust.skvirrel.test.util.SkvirrelTestUtils;
import yahoofinance.histquotes.HistoricalQuote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class IndicatorUtilsTest {

    @Test
    public void handleCurrentPriceTest() {
        List<HistoricalQuote> historicalQuotes = SkvirrelTestUtils.getMockedHistoricalQuotes();
        int size = historicalQuotes.size();
        BigDecimal currentPrice = BigDecimal.valueOf(34.9836);

        // current price is null
        List<HistoricalQuote> result = IndicatorUtils.handleCurrentPrice(historicalQuotes, null);
        assertNotNull(result);
        assertEquals(size, result.size());
        assertEquals(historicalQuotes.get(size - 1).getClose(), result.get(size - 1).getClose());

        // current price is same as last quotes
        result = IndicatorUtils.handleCurrentPrice(historicalQuotes, historicalQuotes.get(size - 1).getClose());
        assertNotNull(result);
        assertEquals(size, result.size());
        assertEquals(historicalQuotes.get(size - 1).getClose(), result.get(size - 1).getClose());

        // current price is same as last quotes
        result = IndicatorUtils.handleCurrentPrice(historicalQuotes, currentPrice);
        assertNotNull(result);
        assertEquals(size, result.size());
        assertEquals(historicalQuotes.get(1).getClose(), result.get(0).getClose());
        assertEquals(currentPrice, result.get(size - 1).getClose());
    }
}
