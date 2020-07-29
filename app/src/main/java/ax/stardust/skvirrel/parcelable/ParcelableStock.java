package ax.stardust.skvirrel.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

/**
 * A lighter and parcelable version of the {@link yahoofinance.Stock} class.
 */
public class ParcelableStock implements Parcelable {

    private String symbol;
    private String name;
    private String previousClose;
    private String open;
    private String bid;
    private String ask;
    private String daysRange;
    private String range52Week; // 52 week range
    private String volume;
    private String avgVolume;
    private String marketCap;
    private String beta5YMonthly;
    private String peRatioTTM;
    private String epsRatioTTM;
    private String earningsDate;
    private String forwardDividendAndYield;
    private String exDividendDate;
    private String targetEstimate1y; // 1 year target estimate

    private ParcelableStock() {
    }

    protected ParcelableStock(Parcel in) {
        symbol = in.readString();
        name = in.readString();
        previousClose = in.readString();
        open = in.readString();
        bid = in.readString();
        ask = in.readString();
        daysRange = in.readString();
        range52Week = in.readString();
        volume = in.readString();
        avgVolume = in.readString();
        marketCap = in.readString();
        beta5YMonthly = in.readString();
        peRatioTTM = in.readString();
        epsRatioTTM = in.readString();
        earningsDate = in.readString();
        forwardDividendAndYield = in.readString();
        exDividendDate = in.readString();
        targetEstimate1y = in.readString();
    }

    public static ParcelableStock createFrom(Stock stock) {
        DecimalFormat df = new DecimalFormat("##.00");
        DecimalFormat dfMarketCap = new DecimalFormat("##.000M");

        StockQuote quote = stock.getQuote();
        StockStats stats = stock.getStats();
        StockDividend dividend = stock.getDividend();

        ParcelableStock ps = new ParcelableStock();
        ps.setSymbol(stock.getSymbol());
        ps.setName(stock.getName());
        ps.setPreviousClose(df.format(quote.getPreviousClose()));
        ps.setOpen(df.format(quote.getOpen()));
        ps.setBid(df.format(quote.getBid()) + "x" + quote.getBidSize());
        ps.setAsk(df.format(quote.getAsk()) + "x" + quote.getAskSize());
        ps.setDaysRange(quote.getDayLow() + "-" + quote.getDayHigh());
        ps.setRange52Week(quote.getYearLow() + "-" + quote.getYearHigh());
        ps.setVolume(String.valueOf(quote.getVolume()));
        ps.setAvgVolume(String.valueOf(quote.getAvgVolume()));
        ps.setMarketCap(dfMarketCap.format(stats.getMarketCap()));
//        ps.setBeta5YMonthly(stats.);
        ps.setPeRatioTTM(df.format(stats.getPe()));
        ps.setEpsRatioTTM(df.format(stats.getEps()));
        ps.setEarningsDate(stats.getEarningsAnnouncement().toString());
        ps.setForwardDividendAndYield(df.format(dividend.getAnnualYield()) + "(" + df.format(dividend.getAnnualYieldPercent()) + "%)");
        ps.setExDividendDate(dividend.getExDate().toString());
        ps.setTargetEstimate1y(df.format(stats.getOneYearTargetPrice()));

        return ps;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(symbol);
        parcel.writeString(name);
        parcel.writeString(previousClose);
        parcel.writeString(open);
        parcel.writeString(bid);
        parcel.writeString(ask);
        parcel.writeString(daysRange);
        parcel.writeString(range52Week);
        parcel.writeString(volume);
        parcel.writeString(avgVolume);
        parcel.writeString(marketCap);
        parcel.writeString(beta5YMonthly);
        parcel.writeString(peRatioTTM);
        parcel.writeString(epsRatioTTM);
        parcel.writeString(earningsDate);
        parcel.writeString(forwardDividendAndYield);
        parcel.writeString(exDividendDate);
        parcel.writeString(targetEstimate1y);
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

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getDaysRange() {
        return daysRange;
    }

    public void setDaysRange(String daysRange) {
        this.daysRange = daysRange;
    }

    public String getRange52Week() {
        return range52Week;
    }

    public void setRange52Week(String range52Week) {
        this.range52Week = range52Week;
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

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getBeta5YMonthly() {
        return beta5YMonthly;
    }

    public void setBeta5YMonthly(String beta5YMonthly) {
        this.beta5YMonthly = beta5YMonthly;
    }

    public String getPeRatioTTM() {
        return peRatioTTM;
    }

    public void setPeRatioTTM(String peRatioTTM) {
        this.peRatioTTM = peRatioTTM;
    }

    public String getEpsRatioTTM() {
        return epsRatioTTM;
    }

    public void setEpsRatioTTM(String epsRatioTTM) {
        this.epsRatioTTM = epsRatioTTM;
    }

    public String getEarningsDate() {
        return earningsDate;
    }

    public void setEarningsDate(String earningsDate) {
        this.earningsDate = earningsDate;
    }

    public String getForwardDividendAndYield() {
        return forwardDividendAndYield;
    }

    public void setForwardDividendAndYield(String forwardDividendAndYield) {
        this.forwardDividendAndYield = forwardDividendAndYield;
    }

    public String getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(String exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public String getTargetEstimate1y() {
        return targetEstimate1y;
    }

    public void setTargetEstimate1y(String targetEstimate1y) {
        this.targetEstimate1y = targetEstimate1y;
    }
}
