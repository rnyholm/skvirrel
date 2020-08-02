package ax.stardust.skvirrel;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ax.stardust.skvirrel.parcelable.ParcelableStock;
import yahoofinance.Stock;
import yahoofinance.quotes.stock.StockDividend;
import yahoofinance.quotes.stock.StockQuote;
import yahoofinance.quotes.stock.StockStats;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ParcelableStockTest {
    private static final DecimalFormat VOLUME_FORMAT = new DecimalFormat("###,###,###");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM. yyyy", Locale.getDefault());
    private static final String DIVIDEND_FORMAT = "%s (%s%%)";

    private static final String NAME = "Mocked test company Inc.";
    private static final String SYMBOL = "TEST";
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
    private static final String DIVIDEND_STR = "5.88";
    private static final String DIVIDEND_YIELD_STR = "3.91";
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
    private static final BigDecimal DIVIDEND = new BigDecimal(DIVIDEND_STR);
    private static final BigDecimal DIVIDEND_YIELD = new BigDecimal(DIVIDEND_YIELD_STR);

    private static final Long VOLUME = 71699667L;
    private static final Long AVG_VOLUME = 61294639L;

    private static final Calendar EARNINGS = Calendar.getInstance();

    private static Stock mockedStock;
    private static StockQuote mockedQuote;
    private static StockStats mockedStats;
    private static StockDividend mockedDividend;

    @BeforeClass
    public static void setUp() {
        mockedStock = Mockito.mock(Stock.class);
        mockedQuote = Mockito.mock(StockQuote.class);
        mockedStats = Mockito.mock(StockStats.class);
        mockedDividend = Mockito.mock(StockDividend.class);

        // set the basics
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
        Mockito.when(mockedStock.getSymbol()).thenReturn(SYMBOL);
        Mockito.when(mockedStock.getStockExchange()).thenReturn(STOCK_EXCHANGE);
        Mockito.when(mockedStock.getCurrency()).thenReturn(CURRENCY);
        Mockito.when(mockedStock.getCurrency()).thenReturn(CURRENCY);
        Mockito.when(mockedStock.getQuote()).thenReturn(mockedQuote);
        Mockito.when(mockedStock.getStats()).thenReturn(mockedStats);
        Mockito.when(mockedStock.getDividend()).thenReturn(mockedDividend);
    }

    @AfterClass
    public static void tearDown() {
        mockedStock = null;
        mockedQuote = null;
        mockedStats = null;
        mockedDividend = null;
    }

    @Test
    public void testFromBasics() {
        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(DIVIDEND);
        Mockito.when(mockedDividend.getAnnualYieldPercent()).thenReturn(DIVIDEND_YIELD);
        Mockito.when(mockedStats.getMarketCap()).thenReturn(MARKET_CAP);

        ParcelableStock ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals(NAME, ps.getName());
        assertEquals(SYMBOL, ps.getSymbol());
        assertEquals(STOCK_EXCHANGE, ps.getStockExchange());
        assertEquals(CURRENCY, ps.getCurrency());
        assertEquals(replaceSeparatorCharacter(PRICE_STR), ps.getPrice());
        assertEquals(replaceSeparatorCharacter(CHANGE_STR), ps.getChange());
        assertEquals(replaceSeparatorCharacter(CHANGE_PERCENT_STR), ps.getChangePercent());
        assertEquals(replaceSeparatorCharacter(PREVIOUS_CLOSE_STR), ps.getPreviousClose());
        assertEquals(replaceSeparatorCharacter(OPEN_STR), ps.getOpen());
        assertEquals(replaceSeparatorCharacter(LOW_STR), ps.getLow());
        assertEquals(replaceSeparatorCharacter(HIGH_STR), ps.getHigh());
        assertEquals(replaceSeparatorCharacter(LOW_52_WEEK_STR), ps.getLow52Week());
        assertEquals(replaceSeparatorCharacter(HIGH_52_WEEK_STR), ps.getHigh52Week());
        assertEquals(replaceSeparatorCharacter(MARKET_CAP_STR), ps.getMarketCap());
        assertEquals(VOLUME_FORMAT.format(VOLUME), ps.getVolume());
        assertEquals(VOLUME_FORMAT.format(AVG_VOLUME), ps.getAvgVolume());
        assertEquals(replaceSeparatorCharacter(PE_STR), ps.getPe());
        assertEquals(replaceSeparatorCharacter(EPS_STR), ps.getEps());
        assertEquals(DATE_FORMAT.format(EARNINGS.getTime()), ps.getEarnings());
        assertEquals(String.format(DIVIDEND_FORMAT, replaceSeparatorCharacter(DIVIDEND_STR), replaceSeparatorCharacter(DIVIDEND_YIELD_STR)), ps.getDividend());
    }

    @Test
    public void testFromMissingDividend() {
        Mockito.when(mockedStock.getDividend()).thenReturn(null);
        ParcelableStock ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals(NOT_AVAILABLE, ps.getDividend());

        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(null);
        Mockito.when(mockedStock.getDividend()).thenReturn(mockedDividend);
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals(NOT_AVAILABLE, ps.getDividend());

        Mockito.when(mockedDividend.getAnnualYield()).thenReturn(DIVIDEND);
        Mockito.when(mockedDividend.getAnnualYieldPercent()).thenReturn(null);
        Mockito.when(mockedStock.getDividend()).thenReturn(mockedDividend);
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals(NOT_AVAILABLE, ps.getDividend());
    }

    @Test
    public void testFromMarketCapFormatting() {
        Mockito.when(mockedStats.getMarketCap()).thenReturn(BigDecimal.ONE);
        ParcelableStock ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("1", ps.getMarketCap());

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("999999"));
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("999999", ps.getMarketCap());

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1000000"));
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("1,00M", ps.getMarketCap());

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1560000"));
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("1,56M", ps.getMarketCap());

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1999999"));
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("1,99M", ps.getMarketCap());

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1000000000"));
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("1,00B", ps.getMarketCap());

        Mockito.when(mockedStats.getMarketCap()).thenReturn(new BigDecimal("1670000000"));
        ps = ParcelableStock.from(mockedStock);
        System.out.println(ps);
        assertEquals("1,67B", ps.getMarketCap());
    }

    private String replaceSeparatorCharacter(String string) {
        return string.replace('.', ',');
    }
}
