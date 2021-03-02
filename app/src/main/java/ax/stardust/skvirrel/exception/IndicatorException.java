package ax.stardust.skvirrel.exception;

/**
 * Exception for stock indicator error situations.
 */
public class IndicatorException extends RuntimeException {

    /**
     * Creates a new instance with given reason
     *
     * @param reason reason of exception
     */
    public IndicatorException(String reason) {
        super(reason);
    }
}
