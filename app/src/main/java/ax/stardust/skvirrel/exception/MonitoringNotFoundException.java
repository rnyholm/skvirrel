package ax.stardust.skvirrel.exception;

/**
 * Exception for monitoring not found error situations.
 */
public class MonitoringNotFoundException extends RuntimeException {

    /**
     * Creates a new instance with given monitoring type
     *
     * @param monitoringType monitoring type not found
     */
    public MonitoringNotFoundException(String monitoringType) {
        super(String.format("No monitoring found with type: %s", monitoringType));
    }
}
