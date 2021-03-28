package ax.stardust.skvirrel.stock.indicator;

import java.math.BigDecimal;

/**
 * Interface for a technical stock indicator.
 */
interface Indicator {

    /**
     * Validates data needed for calculation within the indicator.
     * This method is supposed to throw an exception if the data is invalid
     */
    void validate();

    /**
     * To handle current price for the calculations
     *
     * @param currentPrice current price to be handled
     */
    void handleCurrentPrice(BigDecimal currentPrice);

    /**
     * Does the actual calculation of the indicator
     */
    void calculate();

    /**
     * To get the last result of the indicator in the array of results
     *
     * @return last result within the indicators results
     */
    double getLastResult();

    /**
     * To get the results of the indicator
     *
     * @return results of the indicator
     */
    double[] getResults();
}
