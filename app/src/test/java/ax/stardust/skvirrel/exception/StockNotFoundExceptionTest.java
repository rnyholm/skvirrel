package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import yahoofinance.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class StockNotFoundExceptionTest {

    @Test(expected = StockNotFoundException.class)
    public void testStockNotFoundException() throws StockNotFoundException {
        StockNotFoundException exception = new StockNotFoundException(null);
        assertNotFoundException(exception);

        Stock stock = new Stock(null);
        stock.setName(null);

        exception = new StockNotFoundException(stock);
        assertNotFoundException(exception);

        stock.setName("");
        exception = new StockNotFoundException(stock);
        assertNotFoundException(exception);

        stock = new Stock("JU7");
        stock.setName("82763");

        exception = new StockNotFoundException(stock);
        assertNotNull(exception);
        assertEquals("Stock with symbol: JU7 is not valid", exception.getMessage());

        throw exception;
    }

    private void assertNotFoundException(StockNotFoundException exception) {
        assertNotNull(exception);
        assertEquals("Stock simply not found", exception.getMessage());
    }
}
