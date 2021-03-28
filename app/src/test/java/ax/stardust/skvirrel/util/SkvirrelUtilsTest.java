package ax.stardust.skvirrel.util;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.test.util.SkvirrelTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class SkvirrelUtilsTest {

    private static Context mockContext;

    @BeforeClass
    public static void setUp() {
        mockContext = Mockito.mock(Context.class);
        Mockito.when(mockContext.getString(R.string.and)).thenReturn("and");
    }

    @Test
    public void testJoin() {
        List<String> strings = new ArrayList<>();
        assertEquals("", SkvirrelUtils.join(mockContext, null));
        assertEquals("", SkvirrelUtils.join(mockContext, strings));

        strings.add("the");
        assertEquals("the", SkvirrelUtils.join(mockContext, strings));

        strings.add("little");
        assertEquals("the and little", SkvirrelUtils.join(mockContext, strings));

        strings.add("fox");
        assertEquals("the, little and fox", SkvirrelUtils.join(mockContext, strings));

        strings.add("runs");
        strings.add("jumps");
        assertEquals("the, little, fox, runs and jumps", SkvirrelUtils.join(mockContext, strings));
    }

    @Test
    public void testRound() {
        assertEquals(1.00, SkvirrelUtils.round(1), SkvirrelTestUtils.DELTA);
        assertEquals(-1.00, SkvirrelUtils.round(-1), SkvirrelTestUtils.DELTA);
        assertEquals(1000.00, SkvirrelUtils.round(999.999), SkvirrelTestUtils.DELTA);
        assertEquals(89.95, SkvirrelUtils.round(89.951), SkvirrelTestUtils.DELTA);
        assertEquals(89.96, SkvirrelUtils.round(89.956), SkvirrelTestUtils.DELTA);
    }

    @Test
    public void testEquals() {
        assertTrue(SkvirrelUtils.equals(new BigDecimal("0.00000"), new BigDecimal("0.00")));
        assertTrue(SkvirrelUtils.equals(new BigDecimal("1.0"), new BigDecimal("1.000")));
        assertTrue(SkvirrelUtils.equals(new BigDecimal("-1.0"), new BigDecimal("-1.000")));
        assertTrue(SkvirrelUtils.equals(new BigDecimal("-23.987645"), new BigDecimal("-23.987645")));
        assertTrue(SkvirrelUtils.equals(new BigDecimal("-23.987645"), new BigDecimal("-23.987643")));
        assertTrue(SkvirrelUtils.equals(new BigDecimal("23.98765"), new BigDecimal("23.987643")));
        assertFalse(SkvirrelUtils.equals(new BigDecimal("23.98765"), new BigDecimal("23.983643")));
        assertFalse(SkvirrelUtils.equals(new BigDecimal("23.18765"), new BigDecimal("23.983643")));
        assertFalse(SkvirrelUtils.equals(new BigDecimal("-23.18765"), new BigDecimal("23.983643")));
    }
}
