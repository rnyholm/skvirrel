package ax.stardust.skvirrel.stock.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.stock.indicator.ExponentialMovingAverage;
import ax.stardust.skvirrel.stock.indicator.RelativeStrengthIndex;
import ax.stardust.skvirrel.stock.indicator.SimpleMovingAverage;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

/**
 * A lighter and parcelable version of the {@link yahoofinance.Stock} class.
 */
public class ParcelableStock implements Parcelable {

    // formatters and constants
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    private static final DecimalFormat MARKET_CAP_BILLION_FORMAT = new DecimalFormat("###.00B");
    private static final DecimalFormat MARKET_CAP_MILLION_FORMAT = new DecimalFormat("###.00M");
    private static final DecimalFormat MARKET_CAP_FORMAT = new DecimalFormat("###.##");
    private static final DecimalFormat VOLUME_FORMAT = new DecimalFormat("###,###,###");

    private static final int BILLION = 1000000000;
    private static final int MILLION = 1000000;

    private static final String NOT_AVAILABLE = "N/A";

    // values from yahoo finance
    private String ticker;
    private String name;
    private String stockExchange;
    private String currency;

    private double price;
    private double change;
    private double changePercent;
    private double previousClose;
    private double open;
    private double low;
    private double high;
    private double low52Week;   // 52 week low
    private double high52Week;  // 52 week high
    private double marketCap;
    private double pe;
    private double eps;
    private double annualYield;
    private double annualYieldPercent;

    private long volume;
    private long avgVolume;

    private Calendar earnings;

    // calculated values
    private double sma50Close;
    private double ema50Close;
    private double rsi14Close;

    private ParcelableStock() {
        // just empty...
    }

    protected ParcelableStock(Parcel in) {
        ticker = in.readString();
        name = in.readString();
        stockExchange = in.readString();
        currency = in.readString();

        price = in.readDouble();
        change = in.readDouble();
        changePercent = in.readDouble();
        previousClose = in.readDouble();
        open = in.readDouble();
        low = in.readDouble();
        high = in.readDouble();
        low52Week = in.readDouble();
        high52Week = in.readDouble();
        marketCap = in.readDouble();
        pe = in.readDouble();
        eps = in.readDouble();
        annualYield = in.readDouble();
        annualYieldPercent = in.readDouble();

        volume = in.readLong();
        avgVolume = in.readLong();

        // special handling for resolving earnings
        earnings = null;
        long timeInMillis = in.readLong();
        if (timeInMillis != Long.MIN_VALUE) {
            earnings = Calendar.getInstance();
            earnings.setTimeInMillis(timeInMillis);
        }

        sma50Close = in.readDouble();
        ema50Close = in.readDouble();
        rsi14Close = in.readDouble();
    }

    /**
     * Creates a parcelable stock from given {@link yahoofinance.Stock}
     *
     * @param stock yahoo stock from which parcelable stock is created
     * @return parcelable stock
     */
    public static ParcelableStock from(Stock stock) throws IOException {
        StockQuote quote = stock.getQuote();
        StockStats stats = stock.getStats();
        StockDividend dividend = stock.getDividend();

        // TODO: Only fetch history if necessary
        Calendar from = Calendar.getInstance();
        from.add(Calendar.DATE, ServiceParams.DAYS_OF_HISTORY);

        // get history from specific date and filter out any eventual null values
        List<HistoricalQuote> historicalQuotes = stock.getHistory(from, Interval.DAILY).stream()
                .filter(historicalQuote -> historicalQuote.getClose() != null)
                .collect(Collectors.toList());

        ParcelableStock parcelableStock = new ParcelableStock();
        parcelableStock.setTicker(getString(stock.getSymbol()));
        parcelableStock.setName(getString(stock.getName()));
        parcelableStock.setStockExchange(getString(stock.getStockExchange()));
        parcelableStock.setCurrency(getString(stock.getCurrency()));
        parcelableStock.setPrice(getDouble(quote.getPrice()));
        parcelableStock.setChange(getDouble(quote.getChange()));
        parcelableStock.setChangePercent(getDouble(quote.getChangeInPercent()));
        parcelableStock.setPreviousClose(getDouble(quote.getPreviousClose()));
        parcelableStock.setOpen(getDouble(quote.getOpen()));
        parcelableStock.setLow(getDouble(quote.getDayLow()));
        parcelableStock.setHigh(getDouble(quote.getDayHigh()));
        parcelableStock.setLow52Week(getDouble(quote.getYearLow()));
        parcelableStock.setHigh52Week(getDouble(quote.getYearHigh()));
        parcelableStock.setMarketCap(getDouble(stats.getMarketCap()));
        parcelableStock.setVolume(getLong(quote.getVolume()));
        parcelableStock.setAvgVolume(getLong(quote.getAvgVolume()));
        parcelableStock.setPe(getDouble(stats.getPe()));
        parcelableStock.setEps(getDouble(stats.getEps()));
        parcelableStock.setEarnings(stats.getEarningsAnnouncement());

        // only set dividend data if enough data exists
        if (dividend == null ||
                (dividend.getAnnualYield() == null || dividend.getAnnualYieldPercent() == null)) {
            parcelableStock.setAnnualYield(Double.NaN);
            parcelableStock.setAnnualYieldPercent(Double.NaN);
        } else {
            parcelableStock.setAnnualYield(getDouble(dividend.getAnnualYield()));
            parcelableStock.setAnnualYieldPercent(getDouble(dividend.getAnnualYieldPercent()));
        }

        // do some calculation of some indicator data
        parcelableStock.setSma50Close(calculateSma50Close(historicalQuotes));
        parcelableStock.setEma50Close(calculateEma50Close(historicalQuotes));
        parcelableStock.setRsi14Close(calculateRsi14Close(historicalQuotes));

        return parcelableStock;
    }

    public static final Creator<ParcelableStock> CREATOR = new Creator<ParcelableStock>() {
        @Override
        public ParcelableStock createFromParcel(Parcel in) {
            return new ParcelableStock(in);
        }

        @Override
        public ParcelableStock[] newArray(int size) {
            return new ParcelableStock[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ticker);
        parcel.writeString(name);
        parcel.writeString(stockExchange);
        parcel.writeString(currency);

        parcel.writeDouble(price);
        parcel.writeDouble(change);
        parcel.writeDouble(changePercent);
        parcel.writeDouble(previousClose);
        parcel.writeDouble(open);
        parcel.writeDouble(low);
        parcel.writeDouble(high);
        parcel.writeDouble(low52Week);
        parcel.writeDouble(high52Week);
        parcel.writeDouble(marketCap);
        parcel.writeDouble(pe);
        parcel.writeDouble(eps);
        parcel.writeDouble(annualYield);
        parcel.writeDouble(annualYieldPercent);

        parcel.writeLong(volume);
        parcel.writeLong(avgVolume);

        parcel.writeLong(earnings != null ? earnings.getTimeInMillis() : Long.MIN_VALUE);

        parcel.writeDouble(sma50Close);
        parcel.writeDouble(ema50Close);
        parcel.writeDouble(rsi14Close);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private static String getString(String string) {
        if (StringUtils.isEmpty(string)) {
            return NOT_AVAILABLE;
        }
        return string;
    }

    private static double getDouble(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return Double.NaN;
        }
        return bigDecimal.doubleValue();
    }

    private static Long getLong(Long l) {
        if (l == null) {
            return Long.MIN_VALUE;
        }
        return l;
    }

    private static double calculateSma50Close(List<HistoricalQuote> historicalQuotes) {
        return SimpleMovingAverage.create(historicalQuotes, SimpleMovingAverage.DEFAULT_PERIOD).getLastResult();
    }

    private static double calculateEma50Close(List<HistoricalQuote> historicalQuotes) {
        return ExponentialMovingAverage.create(historicalQuotes, ExponentialMovingAverage.DEFAULT_PERIOD).getLastResult();
    }

    private static double calculateRsi14Close(List<HistoricalQuote> historicalQuotes) {
        return RelativeStrengthIndex.create(historicalQuotes, RelativeStrengthIndex.DEFAULT_PERIOD).getLastResult();
    }

    /**
     * To get ticker, returns N/A if data was not available from yahoo finance
     *
     * @return ticker
     */
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    /**
     * To get name, returns N/A if data was not available from yahoo finance
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * To get stock exchange, returns N/A if data was not available from yahoo finance
     *
     * @return stock exchange
     */
    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    /**
     * To get currency, returns N/A if data was not available from yahoo finance
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * To get price, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return price
     */
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * To get change, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return change
     */
    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    /**
     * To get change percent, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return change percent
     */
    public double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(double changePercent) {
        this.changePercent = changePercent;
    }

    /**
     * To get previous close, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return previous close
     */
    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    /**
     * To get open, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return open
     */
    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    /**
     * To get low, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return low
     */
    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    /**
     * To get high, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return high
     */
    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    /**
     * To get 52 week low, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return 52 week low
     */
    public double getLow52Week() {
        return low52Week;
    }

    public void setLow52Week(double low52Week) {
        this.low52Week = low52Week;
    }

    /**
     * To get 52 week high, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return 52 week high
     */
    public double getHigh52Week() {
        return high52Week;
    }

    public void setHigh52Week(double high52Week) {
        this.high52Week = high52Week;
    }

    /**
     * To get market cap, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return market cap
     */
    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }

    /**
     * To get pe, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return pe
     */
    public double getPe() {
        return pe;
    }

    public void setPe(double pe) {
        this.pe = pe;
    }

    /**
     * To get eps, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return eps
     */
    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    /**
     * To get annual yield, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return annual yield
     */
    public double getAnnualYield() {
        return annualYield;
    }

    public void setAnnualYield(double annualYield) {
        this.annualYield = annualYield;
    }

    /**
     * To get annual yield percent, returns {@link Double#NaN} if data was not available from yahoo finance
     *
     * @return annual yield percent
     */
    public double getAnnualYieldPercent() {
        return annualYieldPercent;
    }

    public void setAnnualYieldPercent(double annualYieldPercent) {
        this.annualYieldPercent = annualYieldPercent;
    }

    /**
     * To get volume, returns {@link Long#MIN_VALUE} if data was not available from yahoo finance
     *
     * @return volume
     */
    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    /**
     * To get average volume, returns {@link Long#MIN_VALUE} if data was not available from yahoo finance
     *
     * @return average volume
     */
    public Long getAvgVolume() {
        return avgVolume;
    }

    public void setAvgVolume(Long avgVolume) {
        this.avgVolume = avgVolume;
    }

    /**
     * To get earnings, returns null if data was not available from yahoo finance
     *
     * @return earnings
     */
    public Calendar getEarnings() {
        return earnings;
    }

    public void setEarnings(Calendar earnings) {
        this.earnings = earnings;
    }

    /**
     * To get simple moving average(50, close)
     *
     * @return simple moving average(50, close)
     */
    public double getSma50Close() {
        return sma50Close;
    }

    public void setSma50Close(double sma50Close) {
        this.sma50Close = sma50Close;
    }

    /**
     * To get exponential moving average(50, close)
     *
     * @return exponential moving average(50, close)
     */
    public double getEma50Close() {
        return ema50Close;
    }

    public void setEma50Close(double ema50Close) {
        this.ema50Close = ema50Close;
    }

    /**
     * To get relative strength index(14, close)
     *
     * @return relative strength index(14, close)
     */
    public double getRsi14Close() {
        return rsi14Close;
    }

    public void setRsi14Close(double rsi14Close) {
        this.rsi14Close = rsi14Close;
    }

    /**
     * Convenience method to transform given double to a string. If given double isNaN then a
     * default string of N/A is returned
     *
     * @param value to transform
     * @return string representation of given double
     */
    public static String getString(double value) {
        if (Double.isNaN(value)) {
            return NOT_AVAILABLE;
        }
        return DECIMAL_FORMAT.format(value).replace(",", ".");
    }

    /**
     * Convenience method to transform given double to a string with given suffix.
     * If given double isNaN then a default string of N/A is returned, else string representation
     * of given double with given suffix is returned
     *
     * @param value to transform
     * @param suffix of transformed double
     * @return string representation of given double with given suffix
     */
    public static String getString(double value, String suffix) {
        String str = getString(value);
        return NOT_AVAILABLE.equals(str) ? str : str + suffix;
    }

    /**
     * Convenience method to transform given market cap to a string. If given market cap isNaN
     * then a default string of N/A is returned
     *
     * @param marketCap to transform
     * @return string representation of market cap
     */
    public static String getMarketCapString(double marketCap) {
        if (Double.isNaN(marketCap)) {
            return NOT_AVAILABLE;
        }

        // billion
        if (marketCap >= BILLION) {
            return MARKET_CAP_BILLION_FORMAT
                    .format(SkvirrelUtils.round(marketCap / BILLION))
                    .replace(",", ".");
        }

        // million
        if (marketCap >= MILLION) {
            return MARKET_CAP_MILLION_FORMAT
                    .format(SkvirrelUtils.round(marketCap / MILLION))
                    .replace(",", ".");
        }

        return MARKET_CAP_FORMAT.format(marketCap).replace(",", ".");
    }

    /**
     * Convenience method to transform given annual yield and percentage into a string.
     * If either the annual yield or percentage isNaN then a default string of N/A is returned
     *
     * @param annualYield        to transform
     * @param annualYieldPercent to transform
     * @return string representation of annual yield and percentage
     */
    public static String getDividendString(double annualYield, double annualYieldPercent) {
        if (Double.isNaN(annualYield) || Double.isNaN(annualYieldPercent)) {
            return NOT_AVAILABLE;
        }
        return String.format("%s(%s%%)", getString(annualYield), getString(annualYieldPercent));
    }

    /**
     * Convenience method to transform given long to a string. If given long {@link Long#MIN_VALUE}
     * then a default string of N/A is returned
     *
     * @param l long to transform
     * @return string representation of given long
     */
    public static String getString(Long l) {
        if (l == Long.MIN_VALUE) {
            return NOT_AVAILABLE;
        }
        return VOLUME_FORMAT.format(l).replace(".", ",");
    }

    /**
     * Convenience method to transform given calendar to a string. If given calendar is null then a
     * default string of N/A is returned
     *
     * @param calendar to transform
     * @return string representation of given calendar
     */
    public static String getString(Calendar calendar) {
        if (calendar == null) {
            return NOT_AVAILABLE;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    @Override
    @NonNull
    public String toString() {
        return "ParcelableStock {" +
                "\n\tticker='" + ticker + '\'' +
                "\n\tname='" + name + '\'' +
                "\n\tstockExchange='" + stockExchange + '\'' +
                "\n\tcurrency='" + currency + '\'' +
                "\n\tprice='" + getString(price) + '\'' +
                "\n\tchange='" + getString(change) + '\'' +
                "\n\tchangePercent='" + getString(changePercent, "%") + '\'' +
                "\n\tpreviousClose='" + getString(previousClose) + '\'' +
                "\n\topen='" + getString(open) + '\'' +
                "\n\tlow='" + getString(low) + '\'' +
                "\n\thigh='" + getString(high) + '\'' +
                "\n\tlow52Week='" + getString(low52Week) + '\'' +
                "\n\thigh52Week='" + getString(high52Week) + '\'' +
                "\n\tmarketCap='" + getMarketCapString(marketCap) + '\'' +
                "\n\tpe='" + getString(pe, "x") + '\'' +
                "\n\teps='" + getString(eps) + '\'' +
                "\n\tannualYield='" + getString(annualYield) + '\'' +
                "\n\tannualYieldPercent='" + getString(annualYieldPercent, "%") + '\'' +
                "\n\tdividend='" + getDividendString(annualYield, annualYieldPercent) + '\'' +
                "\n\tvolume='" + getString(volume) + '\'' +
                "\n\tavgVolume='" + getString(avgVolume) + '\'' +
                "\n\tearnings='" + getString(earnings) + '\'' +
                "\n\tsma50Close='" + getString(sma50Close) + '\'' +
                "\n\tema50Close='" + getString(ema50Close) + '\'' +
                "\n\trsi14Close='" + getString(rsi14Close) + '\'' +
                "\n}";
    }
}
