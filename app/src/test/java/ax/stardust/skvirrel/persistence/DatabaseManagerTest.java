package ax.stardust.skvirrel.persistence;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import ax.stardust.skvirrel.monitoring.StockMonitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseManagerTest {

    private static final String TICKER_0 = "T0";
    private static final String TICKER_1 = "T1";
    private static final String TICKER_2 = "T2";
    private static final String TICKER_3 = "T3";

    private static final String COMPANY_NAME_0 = "company 0 inc";
    private static final String COMPANY_NAME_1 = "company 1 inc";
    private static final String COMPANY_NAME_2 = "company 2 inc";
    private static final String COMPANY_NAME_3 = "company 3 inc";

    private static DatabaseManager mockedDatabaseManager;

    @BeforeClass
    public static void setUp() {
        mockedDatabaseManager = Mockito.mock(DatabaseManager.class);
        Mockito.when(mockedDatabaseManager.fetchAllForMonitoring()).thenCallRealMethod();
        Mockito.when(mockedDatabaseManager.fetchAllTickersForMonitoring()).thenCallRealMethod();
        Mockito.when(mockedDatabaseManager.fetchAll()).thenReturn(provideMockStockMonitorings());
    }

    @Test
    public void testFetchAllForMonitoring() {
        List<StockMonitoring> stockMonitorings = mockedDatabaseManager.fetchAllForMonitoring();
        assertNotNull(stockMonitorings);
        assertEquals(3, stockMonitorings.size());

        StockMonitoring sm0 = stockMonitorings.get(0);
        assertEquals(TICKER_0, sm0.getTicker());
        assertEquals(COMPANY_NAME_0, sm0.getCompanyName());
        assertEquals("10", sm0.getMonitoringOptions().getPriceMonitoring().getValue());
        assertEquals("35", sm0.getMonitoringOptions().getRsiMonitoring().getValue());

        StockMonitoring sm1 = stockMonitorings.get(1);
        assertEquals(TICKER_1, sm1.getTicker());
        assertEquals(COMPANY_NAME_1, sm1.getCompanyName());
        assertEquals("20", sm1.getMonitoringOptions().getPriceMonitoring().getValue());
        assertEquals("0", sm1.getMonitoringOptions().getRsiMonitoring().getValue());

        StockMonitoring sm1_1 = stockMonitorings.get(2);
        assertEquals(TICKER_1, sm1_1.getTicker());
        assertEquals(COMPANY_NAME_1, sm1_1.getCompanyName());
        assertEquals("0", sm1_1.getMonitoringOptions().getPriceMonitoring().getValue());
        assertEquals("45", sm1_1.getMonitoringOptions().getRsiMonitoring().getValue());
    }

    @Test
    public void testFetchAllTickersForMonitoring() {
        ArrayList<String> tickers = mockedDatabaseManager.fetchAllTickersForMonitoring();
        assertNotNull(tickers);
        assertEquals(2, tickers.size());
        assertTrue(tickers.contains(TICKER_0));
        assertTrue(tickers.contains(TICKER_1));
    }

    private static List<StockMonitoring> provideMockStockMonitorings() {
        StockMonitoring sm0 = new StockMonitoring();
        sm0.setTicker(TICKER_0);
        sm0.setCompanyName(COMPANY_NAME_0);
        sm0.getMonitoringOptions().getPriceMonitoring().setValue("10");
        sm0.getMonitoringOptions().getRsiMonitoring().setValue("35");

        StockMonitoring sm1 = new StockMonitoring();
        sm1.setTicker(TICKER_1);
        sm1.setCompanyName(COMPANY_NAME_1);
        sm1.getMonitoringOptions().getPriceMonitoring().setValue("20");

        StockMonitoring sm1_1 = new StockMonitoring();
        sm1_1.setTicker(TICKER_1);
        sm1_1.setCompanyName(COMPANY_NAME_1);
        sm1_1.getMonitoringOptions().getRsiMonitoring().setValue("45");

        StockMonitoring sm2 = new StockMonitoring();
        sm2.setTicker(TICKER_2);
        sm2.setCompanyName(COMPANY_NAME_2);

        StockMonitoring sm3 = new StockMonitoring();
        sm3.setTicker(TICKER_3);
        sm3.getMonitoringOptions().getPriceMonitoring().setValue("98");
        sm3.getMonitoringOptions().getRsiMonitoring().setValue("56");

        StockMonitoring sm3_3 = new StockMonitoring();
        sm3_3.setCompanyName(COMPANY_NAME_3);
        sm3_3.getMonitoringOptions().getPriceMonitoring().setValue("125");
        sm3_3.getMonitoringOptions().getRsiMonitoring().setValue("20");

        List<StockMonitoring> stockMonitorings = new ArrayList<>();
        stockMonitorings.add(sm0);
        stockMonitorings.add(sm1);
        stockMonitorings.add(sm1_1);
        stockMonitorings.add(sm2);
        stockMonitorings.add(sm3);
        stockMonitorings.add(sm3_3);

        return stockMonitorings;
    }
}
