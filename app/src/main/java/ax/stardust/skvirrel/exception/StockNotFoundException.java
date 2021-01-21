package ax.stardust.skvirrel.exception;

import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import yahoofinance.Stock;

/**
 * Exception for stock not found/valid situations.
 */
public class StockNotFoundException extends Exception {

    /**
     * Creates a new instance with given stock, can be null
     *
     * @param stock stock for exception
     */
    public StockNotFoundException(@Nullable Stock stock) {
        super(stock == null || StringUtils.isEmpty(stock.getName()) ? "Stock simply not found"
                : String.format("Stock with ticker: %s is not valid", stock.getSymbol()));
    }
}
