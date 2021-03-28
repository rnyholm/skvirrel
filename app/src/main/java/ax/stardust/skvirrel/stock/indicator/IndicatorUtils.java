package ax.stardust.skvirrel.stock.indicator;

import java.math.BigDecimal;
import java.util.List;

import ax.stardust.skvirrel.util.SkvirrelUtils;
import yahoofinance.histquotes.HistoricalQuote;

/**
 * Class containing static indicator related utility methods.
 */
public class IndicatorUtils {

    /**
     * Current price handling. If current price isn't null and current price is not the same as last
     * quotes closing price, then current price is taken into account in that way that it is added
     * as the last historical quote in the returned list and the first element is removed. This is
     * to ensure that the returned list has the same length as the given one. If given price is null
     * or equal to last historical quotes closing price, then given list is simply returned.
     *
     * @param historicalQuotes historical quotes
     * @param currentPrice     current price
     * @return historical quotes with current price if needed
     */
    public static List<HistoricalQuote> handleCurrentPrice(List<HistoricalQuote> historicalQuotes,
                                                           BigDecimal currentPrice) {
        if (currentPrice != null) {
            HistoricalQuote lastQuote = historicalQuotes.get(historicalQuotes.size() - 1);

            // find out if last quotes closing price equals given price, if they're unequal then we
            // use the current price as last quotes price
            if (!SkvirrelUtils.equals(lastQuote.getClose(), currentPrice)) {
                // create a new historical quote with current price
                HistoricalQuote quoteWithCurrentPrice = new HistoricalQuote();
                quoteWithCurrentPrice.setClose(currentPrice);

                // grab a sublist of historical quotes but exclude the oldest(first) element
                List<HistoricalQuote> subList = historicalQuotes.subList(1, historicalQuotes.size());
                subList.add(quoteWithCurrentPrice);

                // update the historical quotes
                historicalQuotes = subList;
            }
        }

        return historicalQuotes;
    }
}
