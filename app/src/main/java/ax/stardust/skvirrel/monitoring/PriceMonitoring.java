package ax.stardust.skvirrel.monitoring;

import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;

import ax.stardust.skvirrel.exception.MonitoringException;
import ax.stardust.skvirrel.stock.parcelable.ParcelableStock;
import timber.log.Timber;

/**
 * Class representing a price monitoring.
 */
public class PriceMonitoring extends AbstractMonitoring {

    private double price = 0;

    /**
     * Creates a new instance of price monitoring with given stock monitoring
     *
     * @param stockMonitoring stock monitoring which holds this monitoring
     */
    public PriceMonitoring(StockMonitoring stockMonitoring) {
        super(stockMonitoring);
        monitoringType = MonitoringType.PRICE;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public void setValue(String numericString) {
        double d = NumberUtils.createDouble(numericString);
        if (d <= 0) {
            throw new NumberFormatException("Given number is less than or equals to 0");
        }

        // set value only when we know it's correct
        price = d;
    }

    @Override
    public String getValue() {
        return new DecimalFormat("#.##").format(price);
    }

    @Override
    public void resetValue() {
        price = 0;
    }

    @Override
    public boolean isValid() {
        return price > 0 && comparator != null;
    }

    @Override
    public boolean checkMonitoringCriteria(ParcelableStock parcelableStock) {
        double stockPrice = parcelableStock.getPrice();

        if (Double.isNaN(stockPrice)) {
            MonitoringException exception = new MonitoringException("Price of parcelable stock is NaN");
            Timber.e(exception, "Unable to check monitoring criteria");
            throw exception;
        }

        if (Criteria.Comparator.BELOW.equals(comparator)) {
            return stockPrice <= price;
        }

        return stockPrice >= price;
    }
}
