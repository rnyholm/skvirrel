package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class StockServiceExceptionTest {

    private static final String REASON = "Missing operation";

    @Test(expected = StockServiceException.class)
    public void testStockServiceException() throws StockServiceException {
        StockServiceException exception = new StockServiceException(null);
        assertNotNull(exception);
        assertNull(exception.getMessage());

        exception = new StockServiceException(REASON);
        assertNotNull(exception);
        assertEquals(REASON, exception.getMessage());

        throw exception;
    }
}
