package ax.stardust.skvirrel.exception;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class IndicatorExceptionTest {

    private static final String REASON = "Something went wrong with the indicator";

    @Test(expected = IndicatorException.class)
    public void testIndicatorException() throws IndicatorException {
        IndicatorException exception = new IndicatorException(null);
        assertNotNull(exception);
        assertNull(exception.getMessage());

        exception = new IndicatorException(REASON);
        assertNotNull(exception);
        assertEquals(REASON, exception.getMessage());

        throw exception;
    }
}
