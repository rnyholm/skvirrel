package ax.stardust.skvirrel.monitoring;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.exception.MonitoringException;
import ax.stardust.skvirrel.exception.MonitoringNotFoundException;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

/**
 * Class containing the different information needed for proper stock monitoring.
 */
@Getter
@Setter
public class StockMonitoring {

    /**
     * UI state, if this stock monitoring should be displayed as collapsed or expanded.
     */
    public enum ViewState {
        /** COLLAPSED, */ EXPANDED
    }

    private long id;
    private int sortingOrder;

    private String ticker;
    private String companyName;

    private MonitoringOptions monitoringOptions;

    // default view state to expanded
    private ViewState viewState = ViewState.EXPANDED;

    /**
     * Creates a new stock monitoring
     */
    public StockMonitoring() {
        monitoringOptions = new MonitoringOptions(this);
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

    /**
     * Indicating whether or not this stock monitoring should be monitored. Criteria is:
     * the monitoring options within this monitoring must be valid and not already notified
     *
     * @return true if this monitoring should be monitored else false
     */
    public boolean shouldBeMonitored() {
        return monitoringOptions.get().stream()
                .anyMatch(AbstractMonitoring::shouldBeMonitored);
    }

    /**
     * To find out if all monitoring options of this monitoring has been notified. Note that
     * monitorings must also be valid
     *
     * @return true if all monitoring options of this monitoring has been notified else false
     */
    public boolean isAllNotified() {
        return monitoringOptions.get().stream()
                .filter(AbstractMonitoring::isValid)
                .allMatch(AbstractMonitoring::isNotified);
    }

    /**
     * To find out if this stock monitoring have some option that has been notified. Note that
     * monitorings must also be valid
     *
     * @return true if this stock monitoring have some notified option else false
     */
    public boolean isNotified() {
        return monitoringOptions.get().stream()
                .filter(AbstractMonitoring::isValid)
                .anyMatch(AbstractMonitoring::isNotified);
    }

    /**
     * To find out if ticker of this stock monitoring is valid or more precisely: Ticker and
     * company name can't be empty
     *
     * @return true if ticker of this stock monitoring is valid else false
     */
    public boolean hasValidTicker() {
        return StringUtils.isNotEmpty(ticker) && StringUtils.isNotEmpty(companyName);
    }

    /**
     * To find out if this stock monitoring holds any specific monitoring that's valid
     *
     * @return true if this stock monitoring holds any specific monitoring that's valid else false
     */
    public boolean hasAnyValidMonitoring() {
        return monitoringOptions.get().stream()
                .anyMatch(AbstractMonitoring::isValid);
    }

    /**
     * To find out if this stock monitoring holds enough data for monitoring to take place.
     * For it to be valid it must hold: ticker and company name, it also must have at least
     * one of the different monitoring options valid.
     *
     * @return true if this stock monitoring holds enough data for monitoring to take place, else false
     */
    public boolean hasValidDataForMonitoring() {
        return hasValidTicker() && hasAnyValidMonitoring();
    }

    /**
     * To get a list of notified monitorings
     *
     * @return list of notified monitorings
     */
    public List<AbstractMonitoring> getNotifiedMonitorings() {
        return monitoringOptions.get().stream()
                .filter(AbstractMonitoring::isNotified)
                .collect(Collectors.toList());
    }

    /**
     * To get a list of monitorings that should be monitored, or in other words that are valid and not notified
     *
     * @return list of monitorings that should be monitored
     */
    public List<AbstractMonitoring> getMonitoringsThatShouldBeMonitored() {
        return monitoringOptions.get().stream()
                .filter(AbstractMonitoring::shouldBeMonitored)
                .collect(Collectors.toList());
    }

    /**
     * To get a list of valid monitorings, for a monitoring to be valid it must contain enough data
     * for a monitoring to take place
     *
     * @return list of valid monitorings
     */
    public List<AbstractMonitoring> getValidMonitorings() {
        return monitoringOptions.get().stream()
                .filter(AbstractMonitoring::isValid)
                .collect(Collectors.toList());
    }

    /**
     * To reset all monitorings notified flag(setting it to false), hence making the monitorings
     * valid for monitoring duty again
     */
    public void resetNotified() {
        monitoringOptions.get().forEach(AbstractMonitoring::resetNotified);
    }

    /**
     * Class containing the different sub-options to monitor, this object can be seen as a container.
     */
    public static class MonitoringOptions {

        private final List<AbstractMonitoring> monitoringOptions = new ArrayList<>();

        /**
         * Creates a new instance of monitoring options with given stock monitoring, which
         * is needed to set a handle from each monitoring to it's parent stock monitoring.
         *
         * @param stockMonitoring for each specific monitoring withing this monitoring options
         */
        public MonitoringOptions(StockMonitoring stockMonitoring) {
            monitoringOptions.add(new PriceMonitoring(stockMonitoring));
            monitoringOptions.add(new PeMonitoring(stockMonitoring));
            monitoringOptions.add(new RsiMonitoring(stockMonitoring));
        }

        public void setStockMonitoring(StockMonitoring stockMonitoring) {
            monitoringOptions.forEach(monitoringOption
                    -> monitoringOption.setStockMonitoring(stockMonitoring));
        }

        /**
         * To get all different monitoring options within this monitoring options
         *
         * @return all different monitoring options
         */
        public List<AbstractMonitoring> get() {
            return monitoringOptions;
        }

        /**
         * Creates and adds any missing monitoring to this monitoring options. Any newly created
         * monitoring will be referenced to given stock monitoring
         *
         * @param stockMonitoring to reference any new monitorings to
         * @return true if a monitoring has been created and added to this monitoring options, else false
         */
        public boolean addMonitoringsIfMissing(StockMonitoring stockMonitoring) {
            boolean monitoringAdded = false;

            for (AbstractMonitoring.MonitoringType monitoringType : AbstractMonitoring.MonitoringType.values()) {
                boolean hasMonitoringOfType = monitoringOptions.stream()
                        .anyMatch(m -> m.getClass().isAssignableFrom(monitoringType.getMonitoringClass()));

                if (!hasMonitoringOfType) {
                    try {
                        // create the missing monitoring with reference to stock monitoring
                        AbstractMonitoring abstractMonitoring = monitoringType.getMonitoringClass()
                                .getConstructor(StockMonitoring.class)
                                .newInstance(stockMonitoring);

                        // add it to the monitoring options and return it
                        monitoringOptions.add(abstractMonitoring);

                        monitoringAdded = true;
                    } catch (Exception e) {
                        MonitoringException exception =
                                new MonitoringException(String.format("Unable to create monitoring of type %s",
                                        monitoringType.getMonitoringClass().getSimpleName()));
                        Timber.e(exception, "Unable to add monitoring to existing list of monitorings");
                        throw exception;
                    }
                }
            }

            return monitoringAdded;
        }

        public PriceMonitoring getPriceMonitoring() {
            return (PriceMonitoring) getMonitoringOfType(AbstractMonitoring.MonitoringType.PRICE);
        }

        public PeMonitoring getPeMonitoring() {
            return (PeMonitoring) getMonitoringOfType(AbstractMonitoring.MonitoringType.PE);
        }

        public RsiMonitoring getRsiMonitoring() {
            return (RsiMonitoring) getMonitoringOfType(AbstractMonitoring.MonitoringType.RSI);
        }

        private AbstractMonitoring getMonitoringOfType(AbstractMonitoring.MonitoringType monitoringType) {
            Optional<AbstractMonitoring> optionalMonitoring = monitoringOptions.stream()
                    .filter(mo -> monitoringType.equals(mo.getMonitoringType()))
                    .findFirst();

            if (optionalMonitoring.isPresent()) {
                return optionalMonitoring.get();
            }

            MonitoringNotFoundException exception = new MonitoringNotFoundException(monitoringType.name());
            Timber.e(exception, "Unable to get monitoring");
            throw exception;
        }
    }
}
