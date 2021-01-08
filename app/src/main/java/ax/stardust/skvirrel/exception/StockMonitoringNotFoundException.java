package ax.stardust.skvirrel.exception;

import java.sql.SQLException;

/**
 * Exception for stock monitoring not found situations.
 */
public class StockMonitoringNotFoundException extends SQLException {

    /**
     * Creates a new instance with given id of stock monitoring
     *
     * @param id id of stock monitoring
     */
    public StockMonitoringNotFoundException(long id) {
        super(String.format("No stock monitoring found in database with id: %s", id));
    }
}
