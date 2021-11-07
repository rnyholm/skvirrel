package ax.stardust.skvirrel.monitoring;

import org.apache.commons.lang3.math.NumberUtils;

import ax.stardust.skvirrel.exception.MonitoringException;
import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import ax.stardust.skvirrel.util.SkvirrelUtils;
import lombok.Getter;
import timber.log.Timber;

/**
 * Class representing a PE ratio monitoring.
 */
@Getter
public class PeMonitoring extends AbstractMonitoring {

    private int pe;

    /**
     * Creates a new instance of pe monitoring with given stock monitoring
     *
     * @param stockMonitoring stock monitoring which holds this monitoring
     */
    public PeMonitoring(StockMonitoring stockMonitoring) {
        super(stockMonitoring);
        monitoringType = MonitoringType.PE;
    }

    @Override
    public void setValue(String numericString) {
        int i = NumberUtils.createInteger(numericString);
        if (i <= 0) {
            throw new NumberFormatException("Given integer is less than or equals to 0");
        }

        // set value only when we know it's correct
        pe = i;
    }

    @Override
    public String getValue() {
        return String.valueOf(pe);
    }

    @Override
    public void resetValue() {
        pe = 0;
    }

    @Override
    public boolean isValid() {
        return pe > 0 && comparator != null;
    }

    @Override
    public boolean checkMonitoringCriteria(ParcelableStock parcelableStock) {
        double stockPe = parcelableStock.getPe();

        if (SkvirrelUtils.UNSET == stockPe) {
            MonitoringException exception = new MonitoringException("PE ratio of parcelable stock is unset");
            Timber.e(exception, "Unable to check monitoring criteria");
            throw exception;
        }

        if (Criteria.Comparator.BELOW.equals(comparator)) {
            return stockPe <= pe;
        }

        return stockPe >= pe;
    }
}
