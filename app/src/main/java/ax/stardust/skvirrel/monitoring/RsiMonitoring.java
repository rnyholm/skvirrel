package ax.stardust.skvirrel.monitoring;

import org.apache.commons.lang3.math.NumberUtils;

import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;

/**
 * Class representing a RSI monitoring.
 */
public class RsiMonitoring extends AbstractMonitoring {

    private int rsi;

    /**
     * Creates a new instance of rsi monitoring with given stock monitoring
     *
     * @param stockMonitoring stock monitoring which holds this monitoring
     */
    public RsiMonitoring(StockMonitoring stockMonitoring) {
        super(stockMonitoring);
        monitoringType = MonitoringType.RSI;
    }

    public int getRsi() {
        return rsi;
    }

    @Override
    public void setValue(String numericString) {
        int i = NumberUtils.createInteger(numericString);
        if (i <= 0 || i >= 100) {
            throw new NumberFormatException("Given integer is (less than or equals to 0) "
                    + "or (larger than or equals to 100)");
        }

        // set value only when we know it's correct
        rsi = i;
    }

    @Override
    public String getValue() {
        return String.valueOf(rsi);
    }

    @Override
    public void resetValue() {
        rsi = 0;
    }

    @Override
    public boolean isValid() {
        return (rsi > 0 && rsi < 100) && comparator != null;
    }

    @Override
    public boolean checkMonitoringCriteria(ParcelableStock parcelableStock) {
        if (Criteria.Comparator.BELOW.equals(comparator)) {
            return parcelableStock.getRsi14Close() <= (double) rsi;
        }

        return parcelableStock.getRsi14Close() >= (double) rsi;
    }
}
