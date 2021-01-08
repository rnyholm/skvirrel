package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class StockMonitoringNotFoundExceptionTest {

    @Test(expected = StockMonitoringNotFoundException.class)
    public void testStockMonitoringNotFoundException() throws StockMonitoringNotFoundException {
        long id = 18363490;

        StockMonitoringNotFoundException exception = new StockMonitoringNotFoundException(id);
        assertNotNull(exception);
        assertEquals("No stock monitoring found in database with id: 18363490", exception.getMessage());

        throw exception;
    }
}
