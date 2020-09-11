package ax.stardust.skvirrel.entity;

/**
 * Class containing the different information needed for proper stock monitoring
 */
public class StockMonitoring {

    private long id;
    private int sortingOrder;

    private String symbol;
    private String companyName;
    private String monitoringOptions;

    private boolean notified;

    /**
     * Creates a new stock monitoring
     */
    public StockMonitoring() {
        // just empty
    }

    /**
     * Creates a new stock monitoring
     *
     * @param id id of stock monitoring
     */
    public StockMonitoring(long id) {
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

    public String getMonitoringOptions() {
        return monitoringOptions;
    }

    public void setMonitoringOptions(String monitoringOptions) {
        this.monitoringOptions = monitoringOptions;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }
}
