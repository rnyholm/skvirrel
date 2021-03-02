package ax.stardust.skvirrel.stock.indicator;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ax.stardust.skvirrel.exception.IndicatorException;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import timber.log.Timber;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Indicator for the relative strength index. Works by calculating changes between closing prices
 * of given data. Those changes will be stored in it's separate gain/loss arrays.
 * <br/>
 * Based on those average gains/losses will be calculated, for the first n number of
 * changes(where n is period of RSI) SMA will be used for calculating averages.
 * <br/>
 * After n number of changes averages of gains/losses will be calculated using SMMA, using
 * SMA as it's initial input. After the first SMMA has been calculated RS and it's corresponding
 * RSI can be calculated as well.
 */
public class RelativeStrengthIndex implements Indicator {

    // default period for RSI calculations
    public static final int DEFAULT_PERIOD = 14;

    // data needed for the calculations to be made
    private final List<HistoricalQuote> historicalQuotes;
    private final int period;

    // calculated data
    private double[] change;
    private double[] gain;
    private double[] loss;
    private double[] avgGain;
    private double[] avgLoss;
    private double[] rs;
    private double[] rsi;

    private RelativeStrengthIndex(List<HistoricalQuote> historicalQuotes, int period) {
        this.historicalQuotes = historicalQuotes;
        this.period = period;

        // sanity check before anything else
        validate();

        // do the calculation up instantiation
        calculate();
    }

    /**
     * Creates a new instance of relative strength index
     *
     * @param historicalQuotes historical quotes for RSI calculation
     * @param period           period of RSI
     * @return created instance of relative strength index
     */
    public static RelativeStrengthIndex create(List<HistoricalQuote> historicalQuotes, int period) {
        return new RelativeStrengthIndex(historicalQuotes, period);
    }

    @Override
    public void validate() {
        if (historicalQuotes == null || period >= historicalQuotes.size()) {
            IndicatorException exception = new IndicatorException("Given quotes are null or given "
                    + "period is greater than or equal to number of given quotes");
            Timber.e(exception, "Unable to create relative strength index");
            throw exception;
        }
    }

    @Override
    public void calculate() {
        // make some initializations
        initializeArrays(historicalQuotes.size());

        // go through the values and make the calculations
        for (int i = 0; i < historicalQuotes.size(); i++) {
            // do nothing on the first iteration
            if (i > 0) {
                // one historical quote per day, and since we want to calculate the difference between
                // close of two days we need get quotes for "yesterday" and "today"
                HistoricalQuote quoteYesterday = historicalQuotes.get(i - 1);
                HistoricalQuote quoteToday = historicalQuotes.get(i);

                // calculate the change between closing prices
                change[i] = quoteToday.getClose().subtract(quoteYesterday.getClose()).doubleValue();

                // if change is positive it's a gain else loss
                if (change[i] > 0) {
                    gain[i] = change[i];
                } else if (change[i] < 0) {
                    loss[i] = Math.abs(change[i]); // we're interested in the absolute value eg. -1.4 -> 1.4
                }
            }

            // calculate SMA
            if (i == period) {
                // the first avg gain/loss will be a simple SMA = sum of price changes / period
                avgGain[i] = Arrays.stream(Arrays.copyOfRange(gain, 0, period)).sum() / period;
                avgLoss[i] = Arrays.stream(Arrays.copyOfRange(loss, 0, period)).sum() / period;
            } else if (i >= period) { // calculate SMMA
                // ((avg * (period - 1)) - change) / period
                avgGain[i] = (avgGain[i - 1] * (period - 1) + gain[i]) / period;
                avgLoss[i] = (avgLoss[i - 1] * (period - 1) + loss[i]) / period;
            }

            // time to calculate RS and RSI
            if (i >= period) {
                // calculate RS = average gain / average loss
                rs[i] = avgGain[i] / avgLoss[i];

                // calculate RSI = 100 - 100 / (1 + RS)
                rsi[i] = SkvirrelUtils.round(100 - (100 / (1 + rs[i])));
            }
        }
    }

    private void initializeArrays(int size) {
        change = new double[size];
        gain = new double[size];
        loss = new double[size];
        avgGain = new double[size];
        avgLoss = new double[size];
        rs = new double[size];
        rsi = new double[size];
    }

    @Override
    public double getLastResult() {
        return rsi[rsi.length - 1];
    }

    @Override
    public double[] getResults() {
        return rsi;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < historicalQuotes.size(); i++) {
            String row = String.format(Locale.ENGLISH, "close: %02.2f change: %02.2f gain: "
                            + "%02.2f loss: %02.2f avgGain: %02.2f avgLoss: %02.2f rs: %02.2f rsi "
                            + "%02.2f\n", historicalQuotes.get(i).getClose().doubleValue(), change[i],
                    gain[i], loss[i], avgGain[i], avgLoss[i], rs[i], rsi[i]);
            sb.append(row);
        }

        return sb.toString();
    }
}
