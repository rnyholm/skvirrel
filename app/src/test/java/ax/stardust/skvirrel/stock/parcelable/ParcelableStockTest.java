package ax.stardust.skvirrel.stock.parcelable;


import android.content.Context;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Calendar;
import java.util.TimeZone;

import ax.stardust.skvirrel.cache.CacheManager;
import ax.stardust.skvirrel.cache.IndicatorCache;
import ax.stardust.skvirrel.stock.indicator.ExponentialMovingAverage;
import ax.stardust.skvirrel.stock.indicator.SimpleMovingAverage;
import ax.stardust.skvirrel.test.util.SkvirrelTestUtils;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import yahoofinance.Stock;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SimpleMovingAverage.class, ExponentialMovingAverage.class, ParcelableStock.class})
public class ParcelableStockTest {

    private static final String NOT_AVAILABLE = "N/A";

    private static final String TICKER = "TEST";
    private static final String NAME = "Mocked test company Inc.";
    private static final String STOCK_EXCHANGE = "NasdaqGS";
    private static final String CURRENCY = "USD";

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("EST");

    private static final BigDecimal PRICE = new BigDecimal("77.43");
    private static final BigDecimal CHANGE = new BigDecimal("-0.77");
    private static final BigDecimal CHANGE_PERCENT = new BigDecimal("-0.98");
    private static final BigDecimal PREVIOUS_CLOSE = new BigDecimal("78.20");
    private static final BigDecimal OPEN = new BigDecimal("78.67");
    private static final BigDecimal LOW = new BigDecimal("75.36");
    private static final BigDecimal HIGH = new BigDecimal("78.96");
    private static final BigDecimal LOW_52_WEEK = new BigDecimal("27.43");
    private static final BigDecimal HIGH_52_WEEK = new BigDecimal("78.96");
    private static final BigDecimal MARKET_CAP = new BigDecimal("90970000000");
    private static final BigDecimal PE = new BigDecimal("150.64");
    private static final BigDecimal EPS = new BigDecimal("0.51");
    private static final BigDecimal ANNUAL_YIELD = new BigDecimal("5.88");
    private static final BigDecimal ANNUAL_YIELD_PERCENT = new BigDecimal("3.91");

    private static final long VOLUME = 71699667L;
    private static final long AVG_VOLUME = 61294639L;

    private static final Calendar LAST_TRADE = Calendar.getInstance();
    private static final Calendar EARNINGS = Calendar.getInstance();

    private static final BigDecimal SMA_50_CLOSE = new BigDecimal("38.41");
    private static final BigDecimal EMA_50_CLOSE = new BigDecimal("40.96");
    private static final BigDecimal RSI_14_CLOSE = new BigDecimal("96.49");

    private static final SimpleMovingAverage SMA_MOCK =
            SimpleMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), PRICE, 14);

    private static final ExponentialMovingAverage EMA_MOCK =
            ExponentialMovingAverage.create(SkvirrelTestUtils.getMockedHistoricalQuotes(), PRICE, 14);

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
        Mockito.when(mockedQuote.getTimeZone()).thenReturn(TIME_ZONE);
        Mockito.when(mockedQuote.getLastTradeTime()).thenReturn(LAST_TRADE);

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

        PowerMockito.when(SimpleMovingAverage.create(Mockito.anyList(), Mockito.any(BigDecimal.class),
                Mockito.anyInt())).thenReturn(SMA_MOCK);
        PowerMockito.when(ExponentialMovingAverage.create(Mockito.anyList(), Mockito.any(BigDecimal.class),
                Mockito.anyInt())).thenReturn(EMA_MOCK);

        PowerMockito.whenNew(CacheManager.class).withArguments(mockedContext).thenReturn(mockedCacheManager);
    }

    @Test
    public void testFrom() throws IOException {
        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(ANNUAL_YIELD);
        Mockito.when(mockedDividend.getAnnualYieldPercent()).thenReturn(ANNUAL_YIELD_PERCENT);
        Mockito.when(mockedStats.getMarketCap()).thenReturn(MARKET_CAP);

        ParcelableStock ps = ParcelableStock.from(mockedContext, mockedStock);

        assertEquals(TICKER, ps.getTicker());
        assertEquals(NAME, ps.getName());
        assertEquals(STOCK_EXCHANGE, ps.getStockExchange());
        assertEquals(CURRENCY, ps.getCurrency());

        assertEquals(TIME_ZONE, ps.getTimeZone());

        assertEquals(PRICE.doubleValue(), ps.getPrice(), SkvirrelTestUtils.DELTA);
        assertEquals(CHANGE.doubleValue(), ps.getChange(), SkvirrelTestUtils.DELTA);
        assertEquals(CHANGE_PERCENT.doubleValue(), ps.getChangePercent(), SkvirrelTestUtils.DELTA);
        assertEquals(PREVIOUS_CLOSE.doubleValue(), ps.getPreviousClose(), SkvirrelTestUtils.DELTA);
        assertEquals(OPEN.doubleValue(), ps.getOpen(), SkvirrelTestUtils.DELTA);
        assertEquals(LOW.doubleValue(), ps.getLow(), SkvirrelTestUtils.DELTA);
        assertEquals(HIGH.doubleValue(), ps.getHigh(), SkvirrelTestUtils.DELTA);
        assertEquals(LOW_52_WEEK.doubleValue(), ps.getLow52Week(), SkvirrelTestUtils.DELTA);
        assertEquals(HIGH_52_WEEK.doubleValue(), ps.getHigh52Week(), SkvirrelTestUtils.DELTA);
        assertEquals(MARKET_CAP.doubleValue(), ps.getMarketCap(), SkvirrelTestUtils.DELTA);
        assertEquals(PE.doubleValue(), ps.getPe(), SkvirrelTestUtils.DELTA);
        assertEquals(EPS.doubleValue(), ps.getEps(), SkvirrelTestUtils.DELTA);
        assertEquals(ANNUAL_YIELD.doubleValue(), ps.getAnnualYield(), SkvirrelTestUtils.DELTA);
        assertEquals(ANNUAL_YIELD_PERCENT.doubleValue(), ps.getAnnualYieldPercent(), SkvirrelTestUtils.DELTA);

        assertEquals(VOLUME, ps.getVolume());
        assertEquals(AVG_VOLUME, ps.getAvgVolume());

        assertEquals(LAST_TRADE, ps.getLastTrade());
        assertEquals(EARNINGS, ps.getEarnings());

        assertEquals(SMA_50_CLOSE.doubleValue(), ps.getSma50Close(), SkvirrelTestUtils.DELTA);
        assertEquals(EMA_50_CLOSE.doubleValue(), ps.getEma50Close(), SkvirrelTestUtils.DELTA);
        assertEquals(RSI_14_CLOSE.doubleValue(), ps.getRsi14Close(), SkvirrelTestUtils.DELTA);
    }

    @Test
    public void testDoubleToString() {
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(SkvirrelUtils.UNSET));
        assertEquals("-5.50", ParcelableStock.toString(-5.5));
        assertEquals("10.60", ParcelableStock.toString(10.6));

        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(SkvirrelUtils.UNSET, true));
        assertEquals("1298.38B", ParcelableStock.toString(1298376465534.0, true));
        assertEquals("12.98M", ParcelableStock.toString(12983798.0, true));
        assertEquals("1298", ParcelableStock.toString(1298.0, true));
        assertEquals("-1298.38B", ParcelableStock.toString(-1298376465534.0, true));
        assertEquals("-12.98M", ParcelableStock.toString(-12983798.0, true));
        assertEquals("-1298", ParcelableStock.toString(-1298.0, true));

        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(SkvirrelUtils.UNSET, "?"));
        assertEquals("10.98?", ParcelableStock.toString(10.98, "?"));
        assertEquals("-10.98test", ParcelableStock.toString(-10.98, "test"));
        assertEquals("-10.98", ParcelableStock.toString(-10.98, ""));

        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(SkvirrelUtils.UNSET, "?", true));
        assertEquals("+0.01?", ParcelableStock.toString(0.01, "?", true));
        assertEquals("0.00bla", ParcelableStock.toString(0.0, "bla", true));
        assertEquals("-0.01}}", ParcelableStock.toString(-0.01, "}}", true));

        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(SkvirrelUtils.UNSET, Double.NaN, "?", ""));
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(SkvirrelUtils.UNSET, SkvirrelUtils.UNSET, "?", ""));
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(1.1, SkvirrelUtils.UNSET, "?", ""));
        assertEquals("-1.20(1.60?)", ParcelableStock.toString(-1.2, 1.6, "?", ""));
        assertEquals("98.87(-8.56)", ParcelableStock.toString(98.87, -8.56, "", ""));
        assertEquals("value1: 98.87, value2: -8.56TEST", ParcelableStock.toString(98.87, -8.56, "TEST", "value1: %s, value2: %s"));
    }

    @Test
    public void testTimeZoneToString() {
        TimeZone timeZone = TimeZone.getTimeZone("testId");
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(null));
        assertEquals(timeZone.getID(), ParcelableStock.toString(timeZone));
    }

    @Test
    public void testCalendarToString() {
        TimeZone timeZone = TimeZone.getTimeZone("testId");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 1, 22);
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(null, null, "", false));
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(calendar, null, "", false));
        assertEquals(NOT_AVAILABLE, ParcelableStock.toString(null, timeZone, "", false));
        assertEquals("22 Feb 2021", ParcelableStock.toString(calendar, timeZone, "", false));
        assertEquals("22 Feb 2021 " + StringUtils.toRootUpperCase(timeZone.getID()), ParcelableStock.toString(calendar, timeZone, "", true));
        assertEquals("2021 22 Feb", ParcelableStock.toString(calendar, timeZone, "yyyy dd MMM", false));
        assertEquals("2021 22 Feb " + StringUtils.toRootUpperCase(timeZone.getID()), ParcelableStock.toString(calendar, timeZone, "yyyy dd MMM", true));
    }

    public static class ChangeTrendTest {
        @Test
        public void testFromChange() {
            assertEquals(ParcelableStock.ChangeTrend.POSITIVE, ParcelableStock.ChangeTrend.fromChange(0.00001));
            assertEquals(ParcelableStock.ChangeTrend.NEGATIVE, ParcelableStock.ChangeTrend.fromChange(-0.00001));
            assertEquals(ParcelableStock.ChangeTrend.NEUTRAL, ParcelableStock.ChangeTrend.fromChange(0.00000));
            assertEquals(ParcelableStock.ChangeTrend.NEUTRAL, ParcelableStock.ChangeTrend.fromChange(-0.00000));
        }
    }
}
