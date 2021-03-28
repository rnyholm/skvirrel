package ax.stardust.skvirrel.stock.indicator;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import ax.stardust.skvirrel.exception.IndicatorException;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import timber.log.Timber;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Indicator for the simple moving average. Works by adding closing prices and dividing them by period.
 * <br/>
 * Simple moving average for 14 day requires at least 14 quotes, if 20 quotes are given then
 * SMA will be calculated for the quotes 1 - 14, the next SMA is calculated for quotes 2 - 15 and so on,
 * this will yield in six SMA values being calculated.
 * <br/>
 * The calculated SMA values will be stored in an array on position corresponding to what quote the
 * SMA is for. In the previously example SMA values will be stored in array position 13 - 19,
 * leaving the preceding positions in the array left as 0.0.
 */
public class SimpleMovingAverage implements Indicator {

    // default period for SMA calculations
    public static final int DEFAULT_PERIOD = 50;

    // data needed for the calculations to be made
    private List<HistoricalQuote> historicalQuotes;
    private final int period;

    // calculated data
    private double[] sma;

    private SimpleMovingAverage(List<HistoricalQuote> historicalQuotes, BigDecimal currentPrice, int period) {
        this.historicalQuotes = historicalQuotes;
        this.period = period;

        // sanity check before anything else
        validate();

        // add current price to the historical quotes if needed
        handleCurrentPrice(currentPrice);

        // do the calculation up instantiation
        calculate();
    }

    /**
     * Creates a new instance of simple moving average
     *
     * @param historicalQuotes historical quotes for SMA calculation
     * @param currentPrice     current price
     * @param period           period of SMA
     * @return created instance of simple moving average
     */
    public static SimpleMovingAverage create(List<HistoricalQuote> historicalQuotes,
                                             BigDecimal currentPrice, int period) {
        return new SimpleMovingAverage(historicalQuotes, currentPrice, period);
    }

    /**
     * Creates a new instance of simple moving average
     *
     * @param historicalQuotes historical quotes for SMA calculation
     * @param period           period of SMA
     * @return created instance of simple moving average
     */
    public static SimpleMovingAverage create(List<HistoricalQuote> historicalQuotes, int period) {
        return new SimpleMovingAverage(historicalQuotes, null, period);
    }

    @Override
    public void validate() {
        if (historicalQuotes == null || period > historicalQuotes.size()) {
            IndicatorException exception = new IndicatorException("Given quotes are null or period "
                    + "is greater than number of given quotes");
            Timber.e(exception, "Unable to create simple moving average");
            throw exception;
        }
    }

    @Override
    public void handleCurrentPrice(BigDecimal currentPrice) {
        historicalQuotes = IndicatorUtils.handleCurrentPrice(historicalQuotes, currentPrice);
    }

    @Override
    public void calculate() {
        // initialize the sma array
        sma = new double[historicalQuotes.size()];

        int maxSmaCalculations = historicalQuotes.size() - period;

        for (int i = 0; i <= maxSmaCalculations; i++) {
            sma[(i + period - 1)] = SkvirrelUtils.round(historicalQuotes.subList(i, (i + period)).stream()
                    .map(quote -> quote.getClose().doubleValue())
                    .reduce(0.0, Double::sum) / period);
        }
    }

    @Override
    public double getLastResult() {
        return sma[sma.length - 1];
    }

    @Override
    public double[] getResults() {
        return sma;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (double d : sma) {
            sb.append(String.format(Locale.ENGLISH, "sma: %02.2f\n", d));
        }

        return sb.toString();
    }
}
