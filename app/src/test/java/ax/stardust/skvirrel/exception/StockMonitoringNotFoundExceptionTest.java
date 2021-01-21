package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class StockMonitoringNotFoundExceptionTest {

    private static final long ID = 18363490;

    @Test(expected = StockMonitoringNotFoundException.class)
    public void testStockMonitoringNotFoundException() throws StockMonitoringNotFoundException {
        StockMonitoringNotFoundException exception = new StockMonitoringNotFoundException(ID);
        assertNotNull(exception);
        assertEquals(String.format("No stock monitoring found in database with id: %s", ID), exception.getMessage());

        throw exception;
    }
}
