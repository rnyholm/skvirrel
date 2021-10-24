package ax.stardust.skvirrel.cache;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ax.stardust.skvirrel.persistence.DatabaseManager;
import lombok.RequiredArgsConstructor;
import timber.log.Timber;

/**
 * Manager for caches within application.
 */
@RequiredArgsConstructor
public class CacheManager {

    private final Context context;

    private DatabaseManager databaseManager;

    /**
     * To get indicator cache for given ticker. If an indicator cache doesn't exists in the database
     * already a new one is created and inserted
     *
     * @param ticker ticker for which indicator cache is to be resolved
     *
     * @return indicator cache for given ticker
     */
    public IndicatorCache getIndicatorCache(@NonNull String ticker) {
        Timber.d("Getting indicator cache for ticker: %s", ticker);

        IndicatorCache indicatorCache = getDatabaseManager().fetchIndicatorCacheForTicker(ticker);
        if (indicatorCache == null) {
            Timber.d("No indicator cache exists for ticker: %s, creating a new one", ticker);
            indicatorCache = getDatabaseManager().insert(new IndicatorCache(ticker));
        }

        return indicatorCache;
    }

    /**
     * To update given indicator cache
     *
     * @param indicatorCache indicator cache to update
     *
     * @return updated indicator cache
     */
    public IndicatorCache updateIndicatorCache(@NonNull IndicatorCache indicatorCache) {
        Timber.d("Updating indicator cache with ticker: %s", indicatorCache.getTicker());
        return getDatabaseManager().update(indicatorCache);
    }

    /**
     * To clean indicator cache from cache entries that are not needed anymore
     */
    public void cleanIndicatorCache() {
        Timber.d("Cleaning indicator cache");

        ArrayList<String> tickersForMonitoring = getDatabaseManager().fetchAllTickersForMonitoring();

        // go though all existing indicator caches and compare with tickers that are up for monitoring
        // if the list of tickers for monitoring doesn't contain ticker of indicator cache then
        // it is removed
        getDatabaseManager().fetchAllIndicatorCaches().forEach(indicatorCache -> {
            if (!tickersForMonitoring.contains(indicatorCache.getTicker())) {
                Timber.d("No active stock monitoring exists for ticker: %s, deleting it's indicator cache",
                        indicatorCache.getTicker());
                getDatabaseManager().delete(indicatorCache);
            }
        });
    }

    private DatabaseManager getDatabaseManager() {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }
}
