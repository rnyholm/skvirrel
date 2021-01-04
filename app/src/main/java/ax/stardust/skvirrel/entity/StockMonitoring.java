package ax.stardust.skvirrel.entity;

import java.util.Objects;

/**
 * Class containing the different information needed for proper stock monitoring.
 */
public class StockMonitoring {

    private long id;
    private int sortingOrder;

    private String symbol;
    private String companyName;

    private MonitoringOptions monitoringOptions;

    private boolean notified;

    /**
     * Creates a new stock monitoring
     */
    public StockMonitoring() {
        monitoringOptions = new MonitoringOptions();
    }

    /**
     * Creates a new stock monitoring
     *
     * @param id id of stock monitoring
     */
    public StockMonitoring(long id) {
        this();
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSortingOrder() {
        return sortingOrder;
    }

    public void setSortingOrder(int sortingOrder) {
        this.sortingOrder = sortingOrder;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public MonitoringOptions getMonitoringOptions() {
        return monitoringOptions;
    }

    public void setMonitoringOptions(MonitoringOptions monitoringOptions) {
        this.monitoringOptions = monitoringOptions;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StockMonitoring that = (StockMonitoring) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Class containing the different options or key values to trigger on
     */
    public static class MonitoringOptions {
        private double price;

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
