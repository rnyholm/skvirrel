package ax.stardust.skvirrel.util;

import android.content.Context;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import ax.stardust.skvirrel.R;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SkvirrelUtilsTest {

    private static Context mockedContext;

    @BeforeClass
    public static void setUp() {
        mockedContext = Mockito.mock(Context.class);
        Mockito.when(mockedContext.getString(R.string.and)).thenReturn("and");
    }

    @Test
    public void testJoin() {
        List<String> strings = new ArrayList<>();
        assertEquals("", SkvirrelUtils.join(mockedContext, null));
        assertEquals("", SkvirrelUtils.join(mockedContext, strings));

        strings.add("the");
        assertEquals("the", SkvirrelUtils.join(mockedContext, strings));

        strings.add("little");
        assertEquals("the and little", SkvirrelUtils.join(mockedContext, strings));

        strings.add("fox");
        assertEquals("the, little and fox", SkvirrelUtils.join(mockedContext, strings));

        strings.add("runs");
        strings.add("jumps");
        assertEquals("the, little, fox, runs and jumps", SkvirrelUtils.join(mockedContext, strings));
    }
}
