package ax.stardust.skvirrel.exception;

/**
 * Exception for monitoring error situations.
 */
public class MonitoringException extends RuntimeException {

    /**
     * Creates a new instance with given message
     *
     * @param message message of exception
     */
    public MonitoringException(String message) {
        super(message);
    }
}
