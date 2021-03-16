package ax.stardust.skvirrel.stock.parcelable;


import android.content.Context;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ax.stardust.skvirrel.cache.CacheManager;
import ax.stardust.skvirrel.cache.IndicatorCache;
import ax.stardust.skvirrel.stock.indicator.ExponentialMovingAverage;
import ax.stardust.skvirrel.stock.indicator.SimpleMovingAverage;
import ax.stardust.skvirrel.test.util.SkvirrelTestUtils;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SimpleMovingAverage.class, ExponentialMovingAverage.class, ParcelableStock.class})
public class ParcelableStockTest {

    private static final DecimalFormat VOLUME_FORMAT = new DecimalFormat("###,###,###");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static final String DIVIDEND_FORMAT = "%s(%s%%)";

    private static final String NAME = "Mocked test company Inc.";
    private static final String TICKER = "TEST";
    private static final String STOCK_EXCHANGE = "NasdaqGS";
    private static final String CURRENCY = "USD";
    private static final String PRICE_STR = "77.43";
    private static final String CHANGE_STR = "-0.77";
    private static final String CHANGE_PERCENT_STR = "-0.98";
    private static final String PREVIOUS_CLOSE_STR = "78.20";
    private static final String OPEN_STR = "78.67";
    private static final String LOW_STR = "75.36";
    private static final String HIGH_STR = "78.96";
    private static final String LOW_52_WEEK_STR = "27.43";
    private static final String HIGH_52_WEEK_STR = "78.96";
    private static final String MARKET_CAP_STR = "90.97B";
    private static final String PE_STR = "150.64";
    private static final String EPS_STR = "0.51";
    private static final String ANNUAL_YIELD_STR = "5.88";
    private static final String ANNUAL_YIELD_PERCENT_STR = "3.91";
    private static final String SMA_50_CLOSE_STR = "35.38";
    private static final String EMA_50_CLOSE_STR = "35.37";
    private static final String RSI_14_CLOSE_STR = "56.72";
    private static final String NOT_AVAILABLE = "N/A";

    private static final BigDecimal PRICE = new BigDecimal(PRICE_STR);
    private static final BigDecimal CHANGE = new BigDecimal(CHANGE_STR);
    private static final BigDecimal CHANGE_PERCENT = new BigDecimal(CHANGE_PERCENT_STR);
    private static final BigDecimal PREVIOUS_CLOSE = new BigDecimal(PREVIOUS_CLOSE_STR);
    private static final BigDecimal OPEN = new BigDecimal(OPEN_STR);
    private static final BigDecimal LOW = new BigDecimal(LOW_STR);
    private static final BigDecimal HIGH = new BigDecimal(HIGH_STR);
    private static final BigDecimal LOW_52_WEEK = new BigDecimal(LOW_52_WEEK_STR);
    private static final BigDecimal HIGH_52_WEEK = new BigDecimal(HIGH_52_WEEK_STR);
    private static final BigDecimal MARKET_CAP = new BigDecimal("90970000000");
    private static final BigDecimal PE = new BigDecimal(PE_STR);
    private static final BigDecimal EPS = new BigDecimal(EPS_STR);
    private static final BigDecimal ANNUAL_YIELD = new BigDecimal(ANNUAL_YIELD_STR);
    private static final BigDecimal ANNUAL_YIELD_PERCENT = new BigDecimal(ANNUAL_YIELD_PERCENT_STR);
    private static final BigDecimal SMA_50_CLOSE = new BigDecimal(SMA_50_CLOSE_STR);
    private static final BigDecimal EMA_50_CLOSE = new BigDecimal(EMA_50_CLOSE_STR);
    private static final BigDecimal RSI_14_CLOSE = new BigDecimal(RSI_14_CLOSE_STR);

    private static final Long VOLUME = 71699667L;
    private static final Long AVG_VOLUME = 61294639L;

    private static final Calendar EARNINGS = Calendar.getInstance();

    private static final SimpleMovingAverage SMA_MOCK =
            SimpleMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 14);

    private static final ExponentialMovingAverage EMA_MOCK =
            ExponentialMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), 14);

    private static Context mockedContext;
    private static CacheManager mockedCacheManager;
    private static Stock mockedStock;
    private static StockStats mockedStats;
    private static StockDividend mockedDividend;

    @BeforeClass
    public static void setUp() throws IOException {
        IndicatorCache indicatorCache = new IndicatorCache(TICKER);
        StockQuote mockedQuote = Mockito.mock(StockQuote.class);

        mockedContext = Mockito.mock(Context.class);
        mockedCacheManager = Mockito.mock(CacheManager.class);
        mockedStock = Mockito.mock(Stock.class);
        mockedStats = Mockito.mock(StockStats.class);
        mockedDividend = Mockito.mock(StockDividend.class);

        Mockito.when(mockedCacheManager.getIndicatorCache(Mockito.anyString())).thenReturn(indicatorCache);
        Mockito.when(mockedCacheManager.updateIndicatorCache(indicatorCache)).thenReturn(indicatorCache);

        Mockito.when(mockedQuote.getPrice()).thenReturn(PRICE);
        Mockito.when(mockedQuote.getChange()).thenReturn(CHANGE);
        Mockito.when(mockedQuote.getChangeInPercent()).thenReturn(CHANGE_PERCENT);
        Mockito.when(mockedQuote.getPreviousClose()).thenReturn(PREVIOUS_CLOSE);
        Mockito.when(mockedQuote.getOpen()).thenReturn(OPEN);
        Mockito.when(mockedQuote.getDayLow()).thenReturn(LOW);
        Mockito.when(mockedQuote.getDayHigh()).thenReturn(HIGH);
        Mockito.when(mockedQuote.getYearLow()).thenReturn(LOW_52_WEEK);
        Mockito.when(mockedQuote.getYearHigh()).thenReturn(HIGH_52_WEEK);
        Mockito.when(mockedQuote.getVolume()).thenReturn(VOLUME);
        Mockito.when(mockedQuote.getAvgVolume()).thenReturn(AVG_VOLUME);

        Mockito.when(mockedStats.getMarketCap()).thenReturn(MARKET_CAP);
        Mockito.when(mockedStats.getPe()).thenReturn(PE);
        Mockito.when(mockedStats.getEps()).thenReturn(EPS);
        Mockito.when(mockedStats.getEarningsAnnouncement()).thenReturn(EARNINGS);

        Mockito.when(mockedStock.getName()).thenReturn(NAME);
        Mockito.when(mockedStock.getSymbol()).thenReturn(TICKER);
        Mockito.when(mockedStock.getStockExchange()).thenReturn(STOCK_EXCHANGE);
        Mockito.when(mockedStock.getCurrency()).thenReturn(CURRENCY);
        Mockito.when(mockedStock.getCurrency()).thenReturn(CURRENCY);
        Mockito.when(mockedStock.getQuote()).thenReturn(mockedQuote);
        Mockito.when(mockedStock.getStats()).thenReturn(mockedStats);
        Mockito.when(mockedStock.getDividend()).thenReturn(mockedDividend);
        Mockito.when(mockedStock.getHistory(Mockito.any(Calendar.class), Mockito.any(Interval.class)))
                .thenReturn(SkvirrelTestUtils.getMockedHistoricalQuotes());
    }

    @Before
    public void init() throws Exception {
        // static mocks
        PowerMockito.mockStatic(SimpleMovingAverage.class);
        PowerMockito.mockStatic(ExponentialMovingAverage.class);

        PowerMockito.when(SimpleMovingAverage.create(Mockito.anyList(), Mockito.anyInt()))
                .thenReturn(SMA_MOCK);
        PowerMockito.when(ExponentialMovingAverage.create(Mockito.anyList(), Mockito.anyInt()))
                .thenReturn(EMA_MOCK);

        PowerMockito.whenNew(CacheManager.class).withArguments(mockedContext).thenReturn(mockedCacheManager);
    }

    @Test
    public void testFromBasics() throws IOException {
        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(ANNUAL_YIELD);
        Mockito.when(mockedDividend.getAnnualYieldPercent()).thenReturn(ANNUAL_YIELD_PERCENT);
        Mockito.when(mockedStats.getMarketCap()).thenReturn(MARKET_CAP);

        ParcelableStock ps = ParcelableStock.from(mockedContext, mockedStock);

        assertEquals(TICKER, ps.getTicker());
        assertEquals(NAME, ps.getName());
        assertEquals(STOCK_EXCHANGE, ps.getStockExchange());
        assertEquals(CURRENCY, ps.getCurrency());
        assertEquals(PRICE.doubleValue(), ps.getPrice(), SkvirrelTestUtils.DELTA);
        assertEquals(PRICE_STR, ParcelableStock.getString(ps.getPrice()));
        assertEquals(CHANGE.doubleValue(), ps.getChange(), SkvirrelTestUtils.DELTA);
        assertEquals(CHANGE_STR, ParcelableStock.getString(ps.getChange()));
        assertEquals(CHANGE_PERCENT.doubleValue(), ps.getChangePercent(), SkvirrelTestUtils.DELTA);
        assertEquals(CHANGE_PERCENT_STR + "%", ParcelableStock.getString(ps.getChangePercent(), "%"));
        assertEquals(PREVIOUS_CLOSE.doubleValue(), ps.getPreviousClose(), SkvirrelTestUtils.DELTA);
        assertEquals(PREVIOUS_CLOSE_STR, ParcelableStock.getString(ps.getPreviousClose()));
        assertEquals(OPEN.doubleValue(), ps.getOpen(), SkvirrelTestUtils.DELTA);
        assertEquals(OPEN_STR, ParcelableStock.getString(ps.getOpen()));
        assertEquals(LOW.doubleValue(), ps.getLow(), SkvirrelTestUtils.DELTA);
        assertEquals(LOW_STR, ParcelableStock.getString(ps.getLow()));
        assertEquals(HIGH.doubleValue(), ps.getHigh(), SkvirrelTestUtils.DELTA);
        assertEquals(HIGH_STR, ParcelableStock.getString(ps.getHigh()));
        assertEquals(LOW_52_WEEK.doubleValue(), ps.getLow52Week(), SkvirrelTestUtils.DELTA);
        assertEquals(LOW_52_WEEK_STR, ParcelableStock.getString(ps.getLow52Week()));
        assertEquals(HIGH_52_WEEK.doubleValue(), ps.getHigh52Week(), SkvirrelTestUtils.DELTA);
        assertEquals(HIGH_52_WEEK_STR, ParcelableStock.getString(ps.getHigh52Week()));
        assertEquals(MARKET_CAP.doubleValue(), ps.getMarketCap(), SkvirrelTestUtils.DELTA);
        assertEquals(MARKET_CAP_STR, ParcelableStock.getMarketCapString(ps.getMarketCap()));
        assertEquals(PE.doubleValue(), ps.getPe(), SkvirrelTestUtils.DELTA);
        assertEquals(PE_STR + "x", ParcelableStock.getString(ps.getPe(), "x"));
        assertEquals(EPS.doubleValue(), ps.getEps(), SkvirrelTestUtils.DELTA);
        assertEquals(EPS_STR, ParcelableStock.getString(ps.getEps()));
        assertEquals(ANNUAL_YIELD.doubleValue(), ps.getAnnualYield(), SkvirrelTestUtils.DELTA);
        assertEquals(ANNUAL_YIELD_STR, ParcelableStock.getString(ps.getAnnualYield()));
        assertEquals(ANNUAL_YIELD_PERCENT.doubleValue(), ps.getAnnualYieldPercent(), SkvirrelTestUtils.DELTA);
        assertEquals(ANNUAL_YIELD_PERCENT_STR + "%", ParcelableStock.getString(ps.getAnnualYieldPercent(), "%"));
        assertEquals(VOLUME, ps.getVolume());
        assertEquals(VOLUME_FORMAT.format(VOLUME), ParcelableStock.getString(ps.getVolume()));
        assertEquals(AVG_VOLUME, ps.getAvgVolume());
        assertEquals(VOLUME_FORMAT.format(AVG_VOLUME), ParcelableStock.getString(ps.getAvgVolume()));
        assertEquals(DATE_FORMAT.format(EARNINGS.getTime()), ParcelableStock.getString(ps.getEarnings()));
        assertEquals(SMA_50_CLOSE.doubleValue(), ps.getSma50Close(), SkvirrelTestUtils.DELTA);
        assertEquals(SMA_50_CLOSE_STR, ParcelableStock.getString(ps.getSma50Close()));
        assertEquals(EMA_50_CLOSE.doubleValue(), ps.getEma50Close(), SkvirrelTestUtils.DELTA);
        assertEquals(EMA_50_CLOSE_STR, ParcelableStock.getString(ps.getEma50Close()));
        assertEquals(RSI_14_CLOSE.doubleValue(), ps.getRsi14Close(), SkvirrelTestUtils.DELTA);
        assertEquals(RSI_14_CLOSE_STR, ParcelableStock.getString(ps.getRsi14Close()));
        assertEquals(String.format(DIVIDEND_FORMAT, ANNUAL_YIELD_STR, ANNUAL_YIELD_PERCENT_STR),
                ParcelableStock.getDividendString(ps.getAnnualYield(), ps.getAnnualYieldPercent()));
    }

    @Test
    public void testFromMissingDividend() throws IOException {
        Mockito.when(mockedStock.getDividend()).thenReturn(null);
        ParcelableStock ps = ParcelableStock.from(mockedContext, mockedStock);
        assertEquals(NOT_AVAILABLE, ParcelableStock.getDividendString(ps.getAnnualYield(), ps.getAnnualYieldPercent()));

        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(null);
        Mockito.when(mockedStock.getDividend()).thenReturn(mockedDividend);
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertEquals(NOT_AVAILABLE, ParcelableStock.getDividendString(ps.getAnnualYield(), ps.getAnnualYieldPercent()));

        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(ANNUAL_YIELD);
        Mockito.when(mockedDividend.getAnnualYieldPercent()).thenReturn(null);
        Mockito.when(mockedStock.getDividend()).thenReturn(mockedDividend);
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertEquals(NOT_AVAILABLE, ParcelableStock.getDividendString(ps.getAnnualYield(), ps.getAnnualYieldPercent()));
    }

    @Test
    public void testFromMarketCapFormatting() throws IOException {
        Mockito.when(mockedStats.getMarketCap()).thenReturn(BigDecimal.ONE);
        ParcelableStock ps = ParcelableStock.from(mockedContext, mockedStock);
        assertEquals(1, ps.getMarketCap(), SkvirrelTestUtils.DELTA);

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("999999"));
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertFalse(ParcelableStock.getMarketCapString(ps.getMarketCap()).contains("M"));

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1000000"));
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertTrue(ParcelableStock.getMarketCapString(ps.getMarketCap()).contains("M"));

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1560000"));
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertTrue(ParcelableStock.getMarketCapString(ps.getMarketCap()).contains("M"));

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1999999"));
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertTrue(ParcelableStock.getMarketCapString(ps.getMarketCap()).contains("M"));

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1000000000"));
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertTrue(ParcelableStock.getMarketCapString(ps.getMarketCap()).contains("B"));

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1670000000"));
        ps = ParcelableStock.from(mockedContext, mockedStock);
        assertTrue(ParcelableStock.getMarketCapString(ps.getMarketCap()).contains("B"));
    }
}
