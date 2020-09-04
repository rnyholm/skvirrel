package ax.stardust.skvirrel.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

/**
 * A lighter and parcelable version of the {@link yahoofinance.Stock} class.
 */
public class ParcelableStock implements Parcelable {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");
    private static final DecimalFormat MARKET_CAP_BILLION_FORMAT = new DecimalFormat("###.00B");
    private static final DecimalFormat MARKET_CAP_MILLION_FORMAT = new DecimalFormat("###.00M");
    private static final DecimalFormat MARKET_CAP_FORMAT = new DecimalFormat("###.##");
    private static final DecimalFormat VOLUME_FORMAT = new DecimalFormat("###,###,###");

    private static final BigDecimal BILLION_BIG_DECIMAL = new BigDecimal("1000000000");
    private static final BigDecimal MILLION_BIG_DECIMAL = new BigDecimal("1000000");

    private static final String NOT_AVAILABLE = "N/A";

    private String symbol;
    private String name;
    private String stockExchange;
    private String currency;
    private String price;
    private String change;
    private String changePercent;
    private String previousClose;
    private String open;
    private String low;
    private String high;
    private String low52Week;   // 52 week low
    private String high52Week;  // 52 week high
    private String marketCap;
    private String volume;
    private String avgVolume;
    private String pe;
    private String eps;
    private String earnings;
    private String dividend;    // dividend and yield

    private ParcelableStock() {
        // just empty...
    }

    protected ParcelableStock(Parcel in) {
        symbol = in.readString();
        name = in.readString();
        stockExchange = in.readString();
        currency = in.readString();
        price = in.readString();
        change = in.readString();
        changePercent = in.readString();
        previousClose = in.readString();
        open = in.readString();
        low = in.readString();
        high = in.readString();
        low52Week = in.readString();
        high52Week = in.readString();
        marketCap = in.readString();
        volume = in.readString();
        avgVolume = in.readString();
        pe = in.readString();
        eps = in.readString();
        earnings = in.readString();
        dividend = in.readString();
    }

    public static ParcelableStock from(Stock stock) {
        StockQuote quote = stock.getQuote();
        StockStats stats = stock.getStats();

        ParcelableStock parcelableStock = new ParcelableStock();
        parcelableStock.setSymbol(getString(stock.getSymbol()));
        parcelableStock.setName(getString(stock.getName()));
        parcelableStock.setStockExchange(getString(stock.getStockExchange()));
        parcelableStock.setCurrency(getString(stock.getCurrency()));
        parcelableStock.setPrice(getString(quote.getPrice()));
        parcelableStock.setChange(getString(quote.getChange()));
        parcelableStock.setChangePercent(getString(quote.getChangeInPercent()));
        parcelableStock.setPreviousClose(getString(quote.getPreviousClose()));
        parcelableStock.setOpen(getString(quote.getOpen()));
        parcelableStock.setLow(getString(quote.getDayLow()));
        parcelableStock.setHigh(getString(quote.getDayHigh()));
        parcelableStock.setLow52Week(getString(quote.getYearLow()));
        parcelableStock.setHigh52Week(getString(quote.getYearHigh()));
        parcelableStock.setMarketCap(getMarketCapString(stats.getMarketCap()));
        parcelableStock.setVolume(getString(quote.getVolume()));
        parcelableStock.setAvgVolume(getString(quote.getAvgVolume()));
        parcelableStock.setPe(getString(stats.getPe()));
        parcelableStock.setEps(getString(stats.getEps()));
        parcelableStock.setEarnings(getString(stats.getEarningsAnnouncement()));
        parcelableStock.setDividend(getString(stock.getDividend()));

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
        parcel.writeString(symbol);
        parcel.writeString(name);
        parcel.writeString(stockExchange);
        parcel.writeString(currency);
        parcel.writeString(price);
        parcel.writeString(change);
        parcel.writeString(changePercent);
        parcel.writeString(previousClose);
        parcel.writeString(open);
        parcel.writeString(low);
        parcel.writeString(high);
        parcel.writeString(low52Week);
        parcel.writeString(high52Week);
        parcel.writeString(marketCap);
        parcel.writeString(volume);
        parcel.writeString(avgVolume);
        parcel.writeString(pe);
        parcel.writeString(eps);
        parcel.writeString(earnings);
        parcel.writeString(dividend);
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

    private static String getString(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return NOT_AVAILABLE;
        }
        return DECIMAL_FORMAT.format(bigDecimal);
    }

    private static String getMarketCapString(BigDecimal marketCap) {
        if (marketCap == null) {
            return NOT_AVAILABLE;
        }

        // billion
        if (marketCap.compareTo(BILLION_BIG_DECIMAL) >= 0) {
            return MARKET_CAP_BILLION_FORMAT.format(marketCap.divide(BILLION_BIG_DECIMAL, 2, RoundingMode.DOWN));
        }

        // million
        if (marketCap.compareTo(MILLION_BIG_DECIMAL) >= 0) {
            return MARKET_CAP_MILLION_FORMAT.format(marketCap.divide(MILLION_BIG_DECIMAL, 2, RoundingMode.DOWN));
        }

        return MARKET_CAP_FORMAT.format(marketCap);
    }

    private static String getString(StockDividend dividend) {
        if (dividend == null ||
                (dividend.getAnnualYield() == null || dividend.getAnnualYieldPercent() == null)) {
            return NOT_AVAILABLE;
        }
        return String.format("%s (%s%%)", getString(dividend.getAnnualYield()), getString(dividend.getAnnualYieldPercent()));
    }

    private static String getString(long l) {
        return VOLUME_FORMAT.format(l);
    }

    private static String getString(Calendar calendar) {
        if (calendar == null) {
            return NOT_AVAILABLE;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockExchange() {
        return stockExchange;
    }

    public void setStockExchange(String stockExchange) {
        this.stockExchange = stockExchange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(String previousClose) {
        this.previousClose = previousClose;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow52Week() {
        return low52Week;
    }

    public void setLow52Week(String low52Week) {
        this.low52Week = low52Week;
    }

    public String getHigh52Week() {
        return high52Week;
    }

    public void setHigh52Week(String high52Week) {
        this.high52Week = high52Week;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getAvgVolume() {
        return avgVolume;
    }

    public void setAvgVolume(String avgVolume) {
        this.avgVolume = avgVolume;
    }

    public String getPe() {
        return pe;
    }

    public void setPe(String pe) {
        this.pe = pe;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getDividend() {
        return dividend;
    }

    public void setDividend(String dividend) {
        this.dividend = dividend;
    }

    @Override
    @NonNull
    public String toString() {
        return "ParcelableStock {" +
                "\n\tsymbol='" + symbol + '\'' +
                "\n\tname='" + name + '\'' +
                "\n\tstockExchange='" + stockExchange + '\'' +
                "\n\tcurrency='" + currency + '\'' +
                "\n\tprice='" + price + '\'' +
                "\n\tchange='" + change + '\'' +
                "\n\tchangePercent='" + changePercent + '\'' +
                "\n\tpreviousClose='" + previousClose + '\'' +
                "\n\topen='" + open + '\'' +
                "\n\tlow='" + low + '\'' +
                "\n\thigh='" + high + '\'' +
                "\n\tlow52Week='" + low52Week + '\'' +
                "\n\thigh52Week='" + high52Week + '\'' +
                "\n\tmarketCap='" + marketCap + '\'' +
                "\n\tvolume='" + volume + '\'' +
                "\n\tavgVolume='" + avgVolume + '\'' +
                "\n\tpe='" + pe + '\'' +
                "\n\teps='" + eps + '\'' +
                "\n\tearnings='" + earnings + '\'' +
                "\n\tdividend='" + dividend + '\'' +
                "\n}";
    }
}
