package ax.stardust.skvirrel.exception;

import java.sql.SQLException;

public class StockMonitoringNotFound extends SQLException {
    public StockMonitoringNotFound(String reason) {
        super(reason);
    }
}
