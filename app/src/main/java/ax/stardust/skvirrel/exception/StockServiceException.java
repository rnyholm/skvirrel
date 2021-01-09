package ax.stardust.skvirrel.exception;

/**
 * Exception for stock service error situations.
 */
public class StockServiceException extends RuntimeException {

    /**
     * Creates a new instance with given reason
     *
     * @param reason reason of exception
     */
    public StockServiceException(String reason) {
        super(reason);
    }
}
