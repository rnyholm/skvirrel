package ax.stardust.skvirrel.test.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import yahoofinance.histquotes.HistoricalQuote;

/**
 * Small utility suit for testing purposes.
 */
public class SkvirrelTestUtils {

    // delta used for assert equals with doubles, delta is the value two doubles can be off
    // by in a double assert equals. Bu using a very small number the comparison will be very precise
    public static final double DELTA = 1e-15;

    /**
     * To get mocked historical quotes for testing purposes
     *
     * @return mocked historical quotes
     */
    public static List<HistoricalQuote> getMockedHistoricalQuotes() {
        List<HistoricalQuote> historicalQuotes = new ArrayList<>();
        HistoricalQuote hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.82));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.51));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.52));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.87));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.32));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.60));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.00));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.91));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.93));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.01));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.97));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(34.68));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(36.01));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.86));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.57));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.18));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.51));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(36.22));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.98));
        historicalQuotes.add(hq);

        hq = new HistoricalQuote();
        hq.setClose(BigDecimal.valueOf(35.51));
        historicalQuotes.add(hq);

        return historicalQuotes;
    }
}
