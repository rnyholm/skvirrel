package ax.stardust.skvirrel.util;

import android.content.Context;

import java.math.BigDecimal;
import java.util.List;

import ax.stardust.skvirrel.R;

/**
 * Class containing static utility methods.
 */
public class SkvirrelUtils {

    /** A value indicating that a double is unset within this application */
    public static final double UNSET = -1.1;

    private static final int TWO_DECIMALS = 2;
    private static final int THREE_DECIMALS = 3;

    /**
     * Joins given list of strings into a list of format: one, two and three
     *
     * @param context context from which "and" string in correct language is resolved
     * @param list    list of strings to be joined
     * @return joined string
     */
    public static String join(Context context, List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        if (list.size() > 1) {
            int lastIndex = list.size() - 1;
            String and = String.format(" %s ", context.getString(R.string.and));
            return String.join(and,
                    String.join(", ", list.subList(0, lastIndex)),
                    list.get(lastIndex));
        }

        return list.get(0);
    }

    /**
     * Rounds given double value to a double with two decimals. Rounding is done using
     * {@link BigDecimal#ROUND_HALF_UP}
     *
     * @param value value to round
     * @return rounded double
     */
    public static double round(double value) {
        return round(value, TWO_DECIMALS);
    }

    /**
     * Rounds given double value to a double with given number of decimals. Rounding is done using
     * {@link BigDecimal#ROUND_HALF_UP}
     *
     * @param value            value to round
     * @param numberOfDecimals number of decimals for round
     * @return rounded double
     */
    private static double round(double value, int numberOfDecimals) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDecimals, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * To find out if given values are numerically equal to each other
     *
     * @param value1 big decimal to compare
     * @param value2 big decimal to compare
     * @return true if given values are numerically equal to each other, else false
     */
    public static boolean equals(BigDecimal value1, BigDecimal value2) {
        return round(value1.doubleValue(), THREE_DECIMALS)
                == round(value2.doubleValue(), THREE_DECIMALS);
    }
}
