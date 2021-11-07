package ax.stardust.skvirrel.monitoring;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.R;
import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import ax.stardust.skvirrel.util.SkvirrelUtils;

/**
 * Abstract class holding fundamental data/functionality shared by all different monitorings.
 */
public abstract class AbstractMonitoring {

    /**
     * The different monitoring types and their corresponding string resource id's.
     */
    public enum MonitoringType {
        PRICE(R.string.price),
        PE(R.string.pe_ratio),
        RSI(R.string.rsi);

        private final int stringResourceId;

        MonitoringType(int stringResourceId) {
            this.stringResourceId = stringResourceId;
        }

        /**
         * To get the translated name of this monitoring type
         *
         * @param context context from which text is resolved from
         * @return translated name
         */
        public String getTranslatedName(Context context) {
            String translatedName = context.getString(stringResourceId);

            // special handling for PE monitoring
            if (MonitoringType.PE.equals(this)) {
                String[] split = StringUtils.split(translatedName);
                translatedName = String.format("%s %s", split[0], StringUtils.toRootLowerCase(split[1]));
            }

            return translatedName;
        }
    }

    // handle to owner of this monitoring
    protected transient StockMonitoring stockMonitoring;

    // type of monitoring
    protected MonitoringType monitoringType;

    // comparator of monitoring
    protected Criteria.Comparator comparator = Criteria.Comparator.BELOW;

    // flag indicating whether or not this monitoring has been notified
    protected boolean notified;

    /**
     * Creates a new instance of monitoring
     *
     * @param stockMonitoring stock monitoring which holds this monitoring
     */
    protected AbstractMonitoring(StockMonitoring stockMonitoring) {
        this.stockMonitoring = stockMonitoring;
    }

    public void setStockMonitoring(StockMonitoring stockMonitoring) {
        this.stockMonitoring = stockMonitoring;
    }

    public StockMonitoring getStockMonitoring() {
        return stockMonitoring;
    }

    public MonitoringType getMonitoringType() {
        return monitoringType;
    }

    public Criteria.Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Criteria.Comparator comparator) {
        this.comparator = comparator;
    }

    public boolean isNotified() {
        return notified;
    }

    /**
     * To mark this monitoring as notified
     * Note. no spelling mistake :)
     */
    public void notifyy() {
        notified = true;
    }

    /**
     * To reset notified state of monitoring
     */
    public void resetNotified() {
        notified = false;
    }

    /**
     * To get a string of translated monitoring names for given monitorings. If for example a
     * list of monitorings Price, RSI and SMA are given and the language of context is english than a
     * string like this is returned: price, RSI and SMA
     *
     * @param context     context for which texts are retrieved
     * @param monitorings monitorings to get their names translated and joined
     * @return string of joined and translated monitoring names
     */
    public static String getJoinedTranslatedMonitoringNames(Context context, List<AbstractMonitoring> monitorings) {
        List<String> translatedMonitoringNames = monitorings.stream()
                .map(monitoring -> {
                    MonitoringType monitoringType = monitoring.getMonitoringType();
                    String translatedName = monitoringType.getTranslatedName(context);
                    if (MonitoringType.PRICE.equals(monitoringType)) {
                        translatedName = StringUtils.toRootLowerCase(translatedName);
                    }
                    return translatedName;
                })
                .collect(Collectors.toList());

        return SkvirrelUtils.join(context, translatedMonitoringNames);
    }

    /**
     * Indicating whether or not this monitoring should be monitored. Criteria is: this monitoring must
     * be valid and not already notified
     *
     * @return true if this monitoring should be monitored else false
     */
    public boolean shouldBeMonitored() {
        return isValid() && !isNotified();
    }

    /**
     * To set monitoring value, provided string must be numeric
     *
     * @param numericString numeric string to be transformed to a real numeric value by the implementor
     */
    public abstract void setValue(String numericString);

    /**
     * To get monitoring value as string
     *
     * @return monitoring value as string
     */
    public abstract String getValue();

    /**
     * To reset value of monitoring, typical use case is when user give empty input
     */
    public abstract void resetValue();

    /**
     * Indicating whether or not this monitoring actually is valid to be used for monitoring
     *
     * @return true if this monitoring is ready to be used else false
     */
    public abstract boolean isValid();

    /**
     * To check monitoring criteria against data within given parcelable stock, if true is returned
     * it means that we got a hit for this monitoring and it should be handled for notification
     *
     * @param parcelableStock parcelable stock which data is about to be checked against criteria within the monitoring
     * @return true if criteria within monitoring are met else false
     */
    public abstract boolean checkMonitoringCriteria(ParcelableStock parcelableStock);
}
