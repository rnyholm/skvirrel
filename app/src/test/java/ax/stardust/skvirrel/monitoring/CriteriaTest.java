package ax.stardust.skvirrel.monitoring;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import ax.stardust.skvirrel.R;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CriteriaTest {

    private static final String ABOVE = "above";
    private static final String BELOW = "below";

    private static Context mockedContext;

    @BeforeClass
    public static void setUp() {
        mockedContext = Mockito.mock(Context.class);

        Mockito.when(mockedContext.getString(R.string.above)).thenReturn(ABOVE);
        Mockito.when(mockedContext.getString(R.string.below)).thenReturn(BELOW);
    }

    @Test
    public void testComparator() {
        assertEquals(ABOVE, Criteria.Comparator.ABOVE.getTranslatedName(mockedContext));
        assertEquals(BELOW, Criteria.Comparator.BELOW.getTranslatedName(mockedContext));
    }
}
