package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ax.stardust.skvirrel.monitoring.AbstractMonitoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class MonitoringNotFoundExceptionTest {

    private static final String MONITORING_TYPE = AbstractMonitoring.MonitoringType.PRICE.name();

    @Test(expected = MonitoringNotFoundException.class)
    public void testMonitoringNotFoundException() throws MonitoringNotFoundException{
        MonitoringNotFoundException exception = new MonitoringNotFoundException(MONITORING_TYPE);
        assertNotNull(exception);
        assertEquals(String.format("No monitoring found with type: %s", MONITORING_TYPE), exception.getMessage());
        throw exception;
    }
}
