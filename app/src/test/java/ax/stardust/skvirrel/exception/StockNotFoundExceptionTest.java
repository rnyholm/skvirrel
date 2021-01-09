package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import yahoofinance.Stock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class StockNotFoundExceptionTest {
    private static final String SYMBOL = "JU7";
    private static final String NAME = "82763";

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

        stock = new Stock(SYMBOL);
        stock.setName(NAME);

        exception = new StockNotFoundException(stock);
        assertNotNull(exception);
        assertEquals(String.format("Stock with symbol: %s is not valid", SYMBOL), exception.getMessage());

        throw exception;
    }

    private void assertNotFoundException(StockNotFoundException exception) {
        assertNotNull(exception);
        assertEquals("Stock simply not found", exception.getMessage());
    }
}
