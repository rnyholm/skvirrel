package ax.stardust.skvirrel.stock.indicator;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Locale;

import ax.stardust.skvirrel.exception.IndicatorException;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import timber.log.Timber;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Indicator for the exponential moving average. Works by first calculating SMA for the period and
 * use that as the initial EMA for the first EMA calculation. After that EMA will be calculated
 * using the previously calculated EMA and closing price. SMA will also be calculated besides EMA
 * all the time.
 * <br/>
 * Exponential moving average for 14 days requires at least 15 quotes, if 20 quotes are given
 * then SMA will be calculated for the quotes 1 - 14, this SMA will be used as the initial EMA for the
 * EMA calculations. After this EMA will be calculated for each quote using the previously calculated
 * EMA and current quotes closing price as input along with along with a smoothing factor (based on
 * period).s
 * <br/>
 * The calculated EMA values will be stored in an array on position corresponding to what quote the
 * EMA is for. In the previously example EMA values will be stored in array position 13 - 19, leaving
 * the preceding positions in the array left as 0.0.
 */
public class ExponentialMovingAverage implements Indicator {

    // default period for EMA calculations
    public static final int DEFAULT_PERIOD = 50;

    // data needed for the calculations to be made
    private final List<HistoricalQuote> historicalQuotes;
    private final int period;

    // calculated date
    private double smoothingConstant;

    private double[] sma;
    private double[] ema;

    private ExponentialMovingAverage(List<HistoricalQuote> historicalQuotes, int period) {
        this.historicalQuotes = historicalQuotes;
        this.period = period;

        // sanity check before anything else
        validate();

        // do the calculation up instantiation
        calculate();
    }

    /**
     * Creates a new instance of exponential moving average
     *
     * @param historicalQuotes historical quotes for EMA calculation
     * @param period           period of EMA
     * @return created instance of exponential moving average
     */
    public static ExponentialMovingAverage create(List<HistoricalQuote> historicalQuotes, int period) {
        return new ExponentialMovingAverage(historicalQuotes, period);
    }

    @Override
    public void validate() {
        if (historicalQuotes == null || period >= historicalQuotes.size()) {
            IndicatorException exception = new IndicatorException("Given quotes are null or given "
                    + "period is greater than or equal to number of given quotes");
            Timber.e(exception, "Unable to create exponential moving average");
            throw exception;
        }
    }

    @Override
    public void calculate() {
        // make some initializations
        initializeArrays(historicalQuotes.size());

        // calculate smoothing constant for EMA
        smoothingConstant = 2d / (period + 1);

        // go through the values and make tha calculations
        for (int i = (period - 1); i < historicalQuotes.size(); i++) {
            // get slices of the quotes and calculate SMA values based on the slices and period
            List<HistoricalQuote> slice = historicalQuotes.subList(0, i + 1);
            double[] smaResults = SimpleMovingAverage.create(slice, period).getResults();

            sma[i] = smaResults[smaResults.length - 1];

            // set first EMA as SMA if index is period - 1 day, at this point enough data exists
            // to start the EMA calculations
            if (i == (period - 1)) {
                ema[i] = sma[i];
            } else if (i > (period - 1)) { // at this point EMA calculations is to be done for closing prices
                double close = historicalQuotes.get(i).getClose().doubleValue();

                // calculate EMA =
                // (closing price - EMA(of previous day)) * smoothing constant + EMA(of previous day)
                ema[i] = SkvirrelUtils.round((close - ema[i - 1]) * smoothingConstant + ema[i - 1]);
            }
        }
    }

    private void initializeArrays(int size) {
        sma = new double[size];
        ema = new double[size];
    }

    @Override
    public double getLastResult() {
        return ema[ema.length - 1];
    }

    @Override
    public double[] getResults() {
        return ema;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < historicalQuotes.size(); i++) {
            String row = String.format(Locale.ENGLISH, "close: %02.2f sma: %02.2f "
                            + "smoothing constant: %02.2f ema: %02.2f\n",
                    historicalQuotes.get(i).getClose().doubleValue(), sma[i], smoothingConstant, ema[i]);
            sb.append(row);
        }

        return sb.toString();
    }
}
