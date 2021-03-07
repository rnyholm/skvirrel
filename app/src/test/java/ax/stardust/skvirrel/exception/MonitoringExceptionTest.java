package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class MonitoringExceptionTest {

    private static final String MESSAGE = "Test message";

    @Test(expected = MonitoringException.class)
    public void testMonitoringException() throws MonitoringException {
        MonitoringException exception = new MonitoringException(null);
        assertNotNull(exception);
        assertNull(exception.getMessage());

        exception = new MonitoringException(MESSAGE);
        assertNotNull(exception);
        assertEquals(MESSAGE, exception.getMessage());

        throw exception;
    }
}
