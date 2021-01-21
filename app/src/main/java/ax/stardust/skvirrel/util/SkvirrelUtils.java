package ax.stardust.skvirrel.util;

import android.content.Context;

import java.util.List;

import ax.stardust.skvirrel.R;

/**
 * Class containing static utility methods.
 */
public class SkvirrelUtils {

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
}
