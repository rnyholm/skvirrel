package ax.stardust.skvirrel.monitoring;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import ax.stardust.skvirrel.R;

/**
 * Criteria common for different stock monitorings.
 */
public class Criteria {

    /**
     * Comparator criteria
     */
    public enum Comparator {
        ABOVE(R.string.above),
        BELOW(R.string.below);

        private int stringResourceId;

        Comparator(int stringResourceId) {
            this.stringResourceId = stringResourceId;
        }

        /**
         * To get the translated name of this comparator, taking to lower case flag of enum
         * in consideration.
         *
         * @param context context from which text is resolved from
         * @return translated name
         */
        public String getTranslatedName(Context context) {
            return context.getString(stringResourceId);
        }
    }
}
