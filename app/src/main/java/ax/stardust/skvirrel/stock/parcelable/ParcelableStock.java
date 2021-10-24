package ax.stardust.skvirrel.stock.parcelable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.cache.CacheManager;
import ax.stardust.skvirrel.cache.IndicatorCache;
import ax.stardust.skvirrel.exception.StockServiceException;
import ax.stardust.skvirrel.service.ServiceParams;
import ax.stardust.skvirrel.stock.indicator.ExponentialMovingAverage;
import ax.stardust.skvirrel.stock.indicator.RelativeStrengthIndex;
import ax.stardust.skvirrel.stock.indicator.SimpleMovingAverage;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

/**
 * A lighter and parcelable version of the {@link yahoofinance.Stock} class.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ParcelableStock implements Parcelable {

    /**
     * Enum for describing the change trend.
     */
    public enum ChangeTrend {
        POSITIVE, NEUTRAL, NEGATIVE;

        /**
         * To resolve change trend from given change
         *
         * @param change to resolve change trend from
         * @return trend of change
         */
        public static ChangeTrend fromChange(double change) {
            if (change > 0) {
                return ChangeTrend.POSITIVE;
            } else if (change < 0) {
                return ChangeTrend.NEGATIVE;
            }
            return ChangeTrend.NEUTRAL;
        }
    }

    // formatters and constants
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    private static final DecimalFormat BILLION_FORMAT = new DecimalFormat("###.00B");
    private static final DecimalFormat MILLION_FORMAT = new DecimalFormat("###.00M");
    private static final DecimalFormat HUNDRED_FORMAT = new DecimalFormat("###.##");

    private static final int BILLION = 1000000000;
    private static final int MILLION = 1000000;

    private static final String NOT_AVAILABLE = "N/A";

    public static final String LAST_TRADE_DATE_PATTERN = "MMM dd, yyyy K:mma";

    // values from yahoo finance
    private String ticker;
    private String name;
    private String stockExchange;
    private String currency;

    private TimeZone timeZone;

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

    private Calendar lastTrade;
    private Calendar earnings;

    // calculated values
    private double sma50Close;
    private double ema50Close;
    private double rsi14Close;

    protected ParcelableStock(Parcel in) {
        ticker = in.readString();
        name = in.readString();
        stockExchange = in.readString();
        currency = in.readString();

        // special handling for resolving time zone
        timeZone = null;
        String timeZoneString = in.readString();
        if (StringUtils.isNotEmpty(timeZoneString)) {
            timeZone = TimeZone.getTimeZone(timeZoneString);
        }

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

        // special handling for resolving last trade and earnings
        lastTrade = null;
        long lastTradeInMillis = in.readLong();
        if (lastTradeInMillis != Long.MIN_VALUE) {
            lastTrade = Calendar.getInstance();
            lastTrade.setTimeInMillis(lastTradeInMillis);
        }

        earnings = null;
        long earningsInMillis = in.readLong();
        if (earningsInMillis != Long.MIN_VALUE) {
            earnings = Calendar.getInstance();
            earnings.setTimeInMillis(earningsInMillis);
        }

        sma50Close = in.readDouble();
        ema50Close = in.readDouble();
        rsi14Close = in.readDouble();
    }

    /**
     * Creates a parcelable stock from given {@link yahoofinance.Stock}
     *
     * @param context context for which parcelable stock is created from
     * @param stock   yahoo stock from which parcelable stock is created
     * @return parcelable stock
     */
    @SuppressLint("BinaryOperationInTimber")
    public static ParcelableStock from(Context context, Stock stock) throws IOException {
        StockQuote quote = stock.getQuote();
        StockStats stats = stock.getStats();
        StockDividend dividend = stock.getDividend();
        BigDecimal currentPrice = quote.getPrice();

        // resolve ticker
        String ticker = getString(stock.getSymbol());

        // and validate it, this should NOT happen, and if it does abort mission
        if (NOT_AVAILABLE.equals(ticker)) {
            StockServiceException exception = new StockServiceException(
                    String.format("Resolved ticker is: %s, abort further handling", ticker));
            Timber.e(exception, "Unable to create parcelable stock");
            throw exception;
        }

        ParcelableStock parcelableStock = new ParcelableStock();
        parcelableStock.setTicker(ticker);
        parcelableStock.setName(getString(stock.getName()));
        parcelableStock.setStockExchange(getString(stock.getStockExchange()));
        parcelableStock.setCurrency(getString(stock.getCurrency()));
        parcelableStock.setTimeZone(quote.getTimeZone());
        parcelableStock.setPrice(getDouble(currentPrice));
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
        parcelableStock.setLastTrade(quote.getLastTradeTime());
        parcelableStock.setEarnings(stats.getEarningsAnnouncement());

        // only set dividend data if enough data exists
        if (dividend == null ||
                (dividend.getAnnualYield() == null || dividend.getAnnualYieldPercent() == null)) {
            parcelableStock.setAnnualYield(SkvirrelUtils.UNSET);
            parcelableStock.setAnnualYieldPercent(SkvirrelUtils.UNSET);
        } else {
            parcelableStock.setAnnualYield(getDouble(dividend.getAnnualYield()));
            parcelableStock.setAnnualYieldPercent(getDouble(dividend.getAnnualYieldPercent()));
        }

        CacheManager cacheManager = new CacheManager(context);
        IndicatorCache indicatorCache = cacheManager.getIndicatorCache(ticker);

        // fetch further data from yahoo finance if refresh of cache is needed
        if (indicatorCache.needsRefresh()) {
            Timber.d("Indicator cache for ticker: %s needs to be refreshed, fetching "
                    + "fresh data from yahoo finance and make needed calculations", indicatorCache.getTicker());

            Calendar from = Calendar.getInstance();
            from.add(Calendar.DATE, ServiceParams.DAYS_OF_HISTORY);

            // get history from specific date and filter out any eventual null values
            List<HistoricalQuote> historicalQuotes = stock.getHistory(from, Interval.DAILY).stream()
                    .filter(historicalQuote -> historicalQuote.getClose() != null)
                    .collect(Collectors.toList());

            // do some calculation of some indicator data and add them to cache
            indicatorCache.setSma(calculateSma50Close(historicalQuotes, currentPrice));
            indicatorCache.setEma(calculateEma50Close(historicalQuotes, currentPrice));
            indicatorCache.setRsi(calculateRsi14Close(historicalQuotes, currentPrice));
            indicatorCache.setExpires(Calendar.getInstance().getTime());

            // at last update the cache
            indicatorCache = cacheManager.updateIndicatorCache(indicatorCache);
        }

        // set calculated values from cache
        parcelableStock.setSma50Close(indicatorCache.getSma());
        parcelableStock.setEma50Close(indicatorCache.getEma());
        parcelableStock.setRsi14Close(indicatorCache.getRsi());

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
        parcel.writeString(timeZone != null ? timeZone.getID() : "");

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

        parcel.writeLong(lastTrade != null ? lastTrade.getTimeInMillis() : Long.MIN_VALUE);
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
            return SkvirrelUtils.UNSET;
        }
        return bigDecimal.doubleValue();
    }

    private static long getLong(Long l) {
        if (l == null) {
            return Long.MIN_VALUE;
        }
        return l;
    }

    private static double calculateSma50Close(List<HistoricalQuote> historicalQuotes, BigDecimal currentPrice) {
        return SimpleMovingAverage.create(historicalQuotes, currentPrice, SimpleMovingAverage.DEFAULT_PERIOD).getLastResult();
    }

    private static double calculateEma50Close(List<HistoricalQuote> historicalQuotes, BigDecimal currentPrice) {
        return ExponentialMovingAverage.create(historicalQuotes, currentPrice, ExponentialMovingAverage.DEFAULT_PERIOD).getLastResult();
    }

    private static double calculateRsi14Close(List<HistoricalQuote> historicalQuotes, BigDecimal currentPrice) {
        return RelativeStrengthIndex.create(historicalQuotes, currentPrice, RelativeStrengthIndex.DEFAULT_PERIOD).getLastResult();
    }

    /**
     * Convenience method to transform given double value to a string.
     * If given double is {@link SkvirrelUtils#UNSET} then a default string of N/A is returned.
     *
     * @param value to transform
     * @return string representation of given double
     */
    public static String toString(double value) {
        return toString(value, Double.NaN, "", "", false, false);
    }

    /**
     * Convenience method to transform given double value to a string with given suffix. Besides that
     * it's possible to choose if the values should be formatted as large numbers(millions, billions, etc.)
     * If given double is {@link SkvirrelUtils#UNSET} then a default string of N/A is returned.
     *
     * @param value  to transform
     * @param largeNumberFormat if values should be formatted as large numbers(millions, billions, etc.)
     * @return string representation of given double
     */
    public static String toString(double value, boolean largeNumberFormat) {
        return toString(value, Double.NaN, "", "", false, largeNumberFormat);
    }

    /**
     * Convenience method to transform given double value to a string with given suffix.
     * If given double is {@link SkvirrelUtils#UNSET} then a default string of N/A is returned.
     *
     * @param value  to transform
     * @param suffix of transformed value
     * @return string representation of given double
     */
    public static String toString(double value, String suffix) {
        return toString(value, Double.NaN, suffix, "", false, false);
    }

    /**
     * Convenience method to transform given double value to a string with given suffix. It's also
     * possible to force a prefix(+/- sign) on the formatted values.
     * If given double is {@link SkvirrelUtils#UNSET} then a default string of N/A is returned.
     *
     * @param value       to transform
     * @param suffix      of transformed value
     * @param forcePrefix if prefix should be forced(+/- signs)
     * @return string representation of given double
     */
    public static String toString(double value, String suffix, boolean forcePrefix) {
        return toString(value, Double.NaN, suffix, "", forcePrefix, false);
    }

    /**
     * Convenience method to transform given double values to a string with given suffix and formatted
     * with given pattern.
     * If the second double value is not a NaN then the method will take that into account as well.
     * Note! formatting is only taken into account if two values are being handled, if no format
     * is passed in a default one of '%s(%s)' is used.
     * If given double(s) are {@link SkvirrelUtils#UNSET} then a default string of N/A is returned.
     *
     * @param value1 to transform
     * @param value2 to transform
     * @param suffix of transformed value(s)
     * @param format format of the transformed values
     * @return string representation of given double(s)
     */
    public static String toString(double value1, double value2, String suffix, String format) {
        return toString(value1, value2, suffix, format, false, false);
    }

    /**
     * Convenience method to transform given double values to a string with given suffix and formatted
     * with given pattern. It's also possible to force a prefix(+/- sign) on the formatted values.
     * Besides that it's possible to choose if the values should be formatted as large numbers(millions,
     * billions, etc.)
     * If the second double value is not a NaN then the method will take that into account as well.
     * Note! formatting is only taken into account if two values are being handled, if no format
     * is passed in a default one of '%s(%s)' is used.
     * If given double(s) are {@link SkvirrelUtils#UNSET} then a default string of N/A is returned.
     *
     * @param value1            to transform
     * @param value2            to transform
     * @param suffix            of transformed value(s)
     * @param format            format of the transformed values
     * @param forcePrefix       if prefix should be forced(+/- signs)
     * @param largeNumberFormat if values should be formatted as large numbers(millions, billions, etc.)
     * @return string representation of given double(s)
     */
    public static String toString(double value1, double value2, String suffix, String format, boolean forcePrefix, boolean largeNumberFormat) {
        if (SkvirrelUtils.UNSET == value1
                || (!Double.isNaN(value2) && SkvirrelUtils.UNSET == value2)) {
            return NOT_AVAILABLE;
        }

        String str1 = numberFormat(value1, largeNumberFormat);
        String str2 = !Double.isNaN(value2) ? numberFormat(value2, largeNumberFormat) : "";

        if (StringUtils.isBlank(str2)) {
            str1 = str1 + suffix;
        } else {
            str2 = str2 + suffix;
        }

        if (forcePrefix) {
            str1 = addPrefix(value1, str1);

            if (StringUtils.isNotBlank(str2)) {
                str2 = addPrefix(value2, str2);
            }
        }

        if (StringUtils.isNotBlank(str2)) {
            if (StringUtils.isBlank(format)) {
                str1 = String.format("%s(%s)", str1, str2);
            } else {
                str1 = String.format(format, str1, str2);
            }
        }

        return str1;
    }

    /**
     * Convenience method to transform given time zone to a string. If given time zone is null
     * then a default string of N/A is returned
     *
     * @param timeZone to transform
     * @return string representation of given time zone
     */
    public static String toString(TimeZone timeZone) {
        if (timeZone == null) {
            return NOT_AVAILABLE;
        }
        return timeZone.getID();
    }

    /**
     * Convenience method to transform given calendar with given timezone to a string formatted
     * according to given pattern. A default pattern of 'dd MMM yyyy' will be used and no short
     * display name of timezone will be appended to returned string.
     * If given calendar or timezone is null then a default string of N/A is returned.
     *
     * @param calendar to transform
     * @param timeZone time zone of calendar
     * @return string representation of given calendar
     */
    public static String toString(Calendar calendar, TimeZone timeZone) {
        return toString(calendar,timeZone, "", false);
    }

    /**
     * Convenience method to transform given calendar with given timezone to a string formatted
     * according to given pattern. It's also possible to decide whether or not to append the short
     * display name of the timezone to the formatted string. If given calendar or timezone is null
     * then a default string of N/A is returned.
     * If no pattern is given a default of 'dd MMM yyyy' will be used.
     *
     * @param calendar          to transform
     * @param timeZone          time zone of calendar
     * @param pattern           pattern for the calender to be formatted after
     * @param appendTimeZone    if the short display name of the timezone should be appended or not
     *                          not the returned string
     * @return string representation of given calendar
     */
    public static String toString(Calendar calendar, TimeZone timeZone, String pattern, boolean appendTimeZone) {
        if (calendar == null || timeZone == null) {
            return NOT_AVAILABLE;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                StringUtils.isNotBlank(pattern) ? pattern : "dd MMM yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(timeZone);

        String timeZoneShortDisplayName = "";

        if (appendTimeZone) {
            String displayName = timeZone.getDisplayName(Locale.ENGLISH);
            timeZoneShortDisplayName = " " + StringUtils.toRootUpperCase(
                    Arrays.stream(displayName.split(" "))
                            .map(s -> "" + s.charAt(0)).collect(Collectors.joining()));
        }

        return dateFormat.format(calendar.getTime()) + timeZoneShortDisplayName;
    }

    private static String numberFormat(double value, boolean largeNumberFormat) {
        String str = largeNumberFormat ? largeNumberFormat(value) : smallNumberFormat(value);
        return str.replace(",", ".");
    }

    private static String smallNumberFormat(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    private static String largeNumberFormat(double value) {
        // billion
        if (value >= BILLION || value <= (BILLION * -1)) {
            return BILLION_FORMAT
                    .format(SkvirrelUtils.round(value / BILLION));
        }

        // million
        if (value >= MILLION || value <= (MILLION * -1)) {
            return MILLION_FORMAT
                    .format(SkvirrelUtils.round(value / MILLION));
        }

        return HUNDRED_FORMAT.format(value);
    }

    private static String addPrefix(double value, String string) {
        String sign = value > 0 ? "+" : value < 0 ? "-" : "";

        // validate first character of number if it's not a number remove it
        char c = string.charAt(0);
        if (!(c >= '0' && c <= '9')) {
            string = string.substring(1);
        }

        return sign + string;
    }
}
