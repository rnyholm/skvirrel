package ax.stardust.skvirrel.monitoring;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import ax.stardust.skvirrel.util.SkvirrelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class TestPeMonitoring {

    private static PeMonitoring monitoring;
    private static ParcelableStock mockStock;

    @BeforeClass
    public static void setUp() {
        monitoring = new PeMonitoring(new StockMonitoring());
        mockStock = Mockito.mock(ParcelableStock.class);

        Mockito.when(mockStock.getPe()).thenReturn(SkvirrelUtils.UNSET);
    }

    @Test
    public void testSetValue() {
        try {
            monitoring.setValue("-1");
            fail("Not allowed to set negative value");
        } catch (NumberFormatException ignore) {
            // expected
        }

        try {
            monitoring.setValue("-0.01");
            fail("Not allowed to set negative value");
        } catch (NumberFormatException ignore) {
            // expected
        }

        monitoring.setValue("1");
    }

    @Test
    public void getValue() {
        String value = "54";
        monitoring.setValue(value);
        assertEquals(value, monitoring.getValue());
    }

    @Test
    public void testResetValue() {
        String value = "2";
        monitoring.setValue(value);
        assertEquals(value, monitoring.getValue());

        monitoring.resetValue();

        assertEquals("0", monitoring.getValue());
    }

    @Test
    public void testIsValid() {
        monitoring.resetValue();
        assertFalse(monitoring.isValid());

        monitoring.setComparator(Criteria.Comparator.ABOVE);
        assertFalse(monitoring.isValid());

        monitoring.setValue("3");
        assertTrue(monitoring.isValid());
    }

    @Test
    public void testCheckMonitoringCriteria() {
        monitoring.setComparator(Criteria.Comparator.BELOW);
        monitoring.setValue("11");

        assertFalse(monitoring.checkMonitoringCriteria(mockStock));

        Mockito.when(mockStock.getPe()).thenReturn(10.6);

        // pe below 11 - true
        assertTrue(monitoring.checkMonitoringCriteria(mockStock));

        // pe above 10 - true
        monitoring.setComparator(Criteria.Comparator.ABOVE);
        monitoring.setValue("10");
        assertTrue(monitoring.checkMonitoringCriteria(mockStock));

        // pe above 12 -false
        monitoring.setValue("12");
        assertFalse(monitoring.checkMonitoringCriteria(mockStock));

        // pe below 10 - false
        monitoring.setComparator(Criteria.Comparator.BELOW);
        monitoring.setValue("10");
        assertFalse(monitoring.checkMonitoringCriteria(mockStock));
    }
}
