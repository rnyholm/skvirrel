package ax.stardust.skvirrel.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import ax.stardust.skvirrel.cache.IndicatorCache;
import ax.stardust.skvirrel.monitoring.AbstractMonitoring;
import ax.stardust.skvirrel.monitoring.StockMonitoring;
import ax.stardust.skvirrel.persistence.gson.AbstractMonitoringJsonAdapter;
import timber.log.Timber;

/**
 * Manager responsible for database handling.
 */
public class DatabaseManager {

    private final Context context;
    private final Gson gson;

    /**
     * Creates a new database manager with given context
     *
     * @param context context for database handling
     */
    public DatabaseManager(Context context) {
        this.context = context;
        gson = new GsonBuilder()
                .registerTypeAdapter(AbstractMonitoring.class, new AbstractMonitoringJsonAdapter())
                .create();
    }

    /**
     * Inserts a new stock monitoring into database
     *
     * @param stockMonitoring stock monitoring to be inserted
     * @return inserted stock monitoring with the newly created id after insertion
     */
    public StockMonitoring insert(StockMonitoring stockMonitoring) {
        TransactionHandler.runInTransaction(context, database -> {
            long id = database.insert(DatabaseHelper.STOCK_MONITORING_TABLE_NAME,
                    null, getContentValues(stockMonitoring));
            stockMonitoring.setId(id);

            Timber.d("Stock monitoring with id: %s and ticker: %s inserted",
                    stockMonitoring.getId(),
                    stockMonitoring.getTicker());
        });

        return stockMonitoring;
    }

    /**
     * Inserts a new indicator cache into database
     *
     * @param indicatorCache indicator cache to insert
     * @return inserted indicator cache with the newly created id after insertion
     */
    public IndicatorCache insert(IndicatorCache indicatorCache) {
        TransactionHandler.runInTransaction(context, database -> {
            long id = database.insert(DatabaseHelper.INDICATOR_CACHE_TABLE_NAME,
                    null, getContentValues(indicatorCache));
            indicatorCache.setId(id);

            Timber.d("Indicator cache with id: %s and ticker: %s inserted",
                    indicatorCache.getId(),
                    indicatorCache.getTicker());
        });

        return indicatorCache;
    }

    /**
     * Updates stock monitoring
     *
     * @param stockMonitoring stock monitoring to be updated
     * @return updated stock monitoring
     */
    public StockMonitoring update(StockMonitoring stockMonitoring) {
        TransactionHandler.runInTransaction(context, database -> {
            // ignore result on update, it's always 0
            database.update(DatabaseHelper.STOCK_MONITORING_TABLE_NAME, getContentValues(stockMonitoring),
                    DatabaseHelper.ID_COLUMN + " = ?", new String[]{String.valueOf(stockMonitoring.getId())});

            Timber.d("Stock monitoring with id: %s and ticker: %s updated",
                    stockMonitoring.getId(),
                    stockMonitoring.getTicker());
        });

        return stockMonitoring;
    }

    /**
     * Updates indicator cache
     *
     * @param indicatorCache indicator cache to be updated
     * @return updated indicator cache
     */
    public IndicatorCache update(IndicatorCache indicatorCache) {
        TransactionHandler.runInTransaction(context, database -> {
            // ignore results on update, it's always 0
            database.update(DatabaseHelper.INDICATOR_CACHE_TABLE_NAME, getContentValues(indicatorCache),
                    DatabaseHelper.ID_COLUMN + " = ?", new String[]{String.valueOf(indicatorCache.getId())});

            Timber.d("Indicator cache with id: %s and ticker: %s updated",
                    indicatorCache.getId(),
                    indicatorCache.getTicker());
        });

        return indicatorCache;
    }

    /**
     * Fetch all stock monitorings that exists in database
     *
     * @return list of all stock monitorings existing in database
     */
    @SuppressLint("StringFormatInTimber")
    public List<StockMonitoring> fetchAllStockMonitorings() {
        List<StockMonitoring> stockMonitorings = new ArrayList<>();

        TransactionHandler.runInTransaction(context, database -> {
            Cursor cursor = database.rawQuery(DatabaseHelper.SELECT_ALL_FROM_STOCK_MONITORING_TABLE, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        stockMonitorings.add(getStockMonitoring(cursor));
                    } while (cursor.moveToNext());
                }

                cursor.close();
                stockMonitorings.sort(Comparator.comparing(StockMonitoring::getSortingOrder));
            }

            Timber.d("Stock monitorings fetched with id's and tickers: %s",
                    stockMonitorings.stream()
                            .map(sm -> String.format("%s(%s)", sm.getId(), sm.getTicker()))
                            .collect(Collectors.joining(", ")));
        });

        return stockMonitorings;
    }

    /**
     * Fetch all stock monitorings that should be monitored
     *
     * @return list of stock monitorings that should be monitored
     */
    public List<StockMonitoring> fetchAllStockMonitoringsForMonitoring() {
        return fetchAllStockMonitorings().stream()
                .filter(StockMonitoring::hasValidDataForMonitoring)
                .filter(StockMonitoring::shouldBeMonitored)
                .collect(Collectors.toList());
    }

    /**
     * Fetch all tickers for stock monitorings that should be monitored
     *
     * @return array list of tickers that should be monitored
     */
    public ArrayList<String> fetchAllTickersForMonitoring() {
        return fetchAllStockMonitoringsForMonitoring().stream()
                .map(StockMonitoring::getTicker)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Fetch all indicator caches that exists in database
     *
     * @return list of all indicator caches existing in database
     */
    @SuppressLint("StringFormatInTimber")
    public List<IndicatorCache> fetchAllIndicatorCaches() {
        List<IndicatorCache> indicatorCaches = new ArrayList<>();

        TransactionHandler.runInTransaction(context, database -> {
            Cursor cursor = database.rawQuery(DatabaseHelper.SELECT_ALL_FROM_INDICATOR_CACHE_TABLE, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        indicatorCaches.add(getIndicatorCache(cursor));
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }

            Timber.d("Indicator caches fetched with id's and tickers: %s",
                    indicatorCaches.stream()
                            .map(ic -> String.format("%s(%s)", ic.getId(), ic.getTicker()))
                            .collect(Collectors.joining(", ")));
        });

        return indicatorCaches;
    }

    /**
     * Fetch indicator cache for given ticker
     *
     * @param ticker ticker for which indicator cache is to be fetched
     * @return indicator cache for ticker or null if it doesn't exist
     */
    public IndicatorCache fetchIndicatorCacheForTicker(String ticker) {
        AtomicReference<IndicatorCache> indicatorCacheReference = new AtomicReference<>();

        TransactionHandler.runInTransaction(context, database -> {
            Cursor cursor = database.rawQuery(DatabaseHelper.SELECT_ALL_FOR_TICKER_FROM_INDICATOR_CACHE_TABLE, new String[]{ticker});
            if (cursor != null) {
                if (cursor.moveToFirst()) { // should only be one row cause of unique constraint on column ticker
                    IndicatorCache indicatorCache = getIndicatorCache(cursor);
                    indicatorCacheReference.set(indicatorCache);

                    Timber.d("Indicator cache fetched with id:%s and ticker: %s",
                            indicatorCache.getId(), indicatorCache.getTicker());
                }
                cursor.close();
            }
        });

        return indicatorCacheReference.get();
    }

    /**
     * Deletes stock monitoring
     *
     * @param stockMonitoring stock monitoring to be deleted
     */
    public void delete(StockMonitoring stockMonitoring) {
        TransactionHandler.runInTransaction(context, database -> {
            // returns rows affected by operation, for now we ignore it
            database.delete(DatabaseHelper.STOCK_MONITORING_TABLE_NAME,
                    DatabaseHelper.ID_COLUMN + " = ?", new String[]{String.valueOf(stockMonitoring.getId())});

            Timber.d("Stock monitoring with id: %s and ticker: %s deleted",
                    stockMonitoring.getId(),
                    stockMonitoring.getTicker());
        });
    }

    /**
     * Deletes indicator cache
     *
     * @param indicatorCache indicator cache to be deleted
     */
    public void delete(IndicatorCache indicatorCache) {
        TransactionHandler.runInTransaction(context, database -> {
            // returns row affected by operation, for now we ignore it
            database.delete(DatabaseHelper.INDICATOR_CACHE_TABLE_NAME,
                    DatabaseHelper.ID_COLUMN + " = ?", new String[]{String.valueOf(indicatorCache.getId())});

            Timber.d("Indicator cache with id: %s and ticker: %S deleted",
                    indicatorCache.getId(),
                    indicatorCache.getTicker());
        });
    }

    private ContentValues getContentValues(StockMonitoring stockMonitoring) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TICKER_COLUMN, stockMonitoring.getTicker());
        contentValues.put(DatabaseHelper.COMPANY_NAME_COLUMN, stockMonitoring.getCompanyName());
        contentValues.put(DatabaseHelper.MONITORING_OPTIONS_COLUMN, gson.toJson(stockMonitoring.getMonitoringOptions()));
        contentValues.put(DatabaseHelper.VIEW_STATE_COLUMN, stockMonitoring.getViewState().name());
        contentValues.put(DatabaseHelper.SORTING_ORDER_COLUMN, stockMonitoring.getSortingOrder());

        return contentValues;
    }

    private ContentValues getContentValues(IndicatorCache indicatorCache) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TICKER_COLUMN, indicatorCache.getTicker());
        contentValues.put(DatabaseHelper.SMA_COLUMN, indicatorCache.getSma());
        contentValues.put(DatabaseHelper.EMA_COLUMN, indicatorCache.getEma());
        contentValues.put(DatabaseHelper.RSI_COLUMN, indicatorCache.getRsi());

        if (indicatorCache.getExpires() != null) {
            contentValues.put(DatabaseHelper.EXPIRES_COLUMN, indicatorCache.getExpires().getTime());
        } else {
            contentValues.put(DatabaseHelper.EXPIRES_COLUMN, Integer.MIN_VALUE);
        }

        return contentValues;
    }

    private StockMonitoring getStockMonitoring(Cursor cursor) {
        StockMonitoring stockMonitoring = new StockMonitoring(cursor.getInt(getColumnIndex(cursor, DatabaseHelper.ID_COLUMN)));
        stockMonitoring.setTicker(cursor.getString(getColumnIndex(cursor, DatabaseHelper.TICKER_COLUMN)));
        stockMonitoring.setCompanyName(cursor.getString(getColumnIndex(cursor, DatabaseHelper.COMPANY_NAME_COLUMN)));
        stockMonitoring.setViewState(StockMonitoring.ViewState.valueOf(cursor.getString(getColumnIndex(cursor, DatabaseHelper.VIEW_STATE_COLUMN))));
        stockMonitoring.setSortingOrder(cursor.getInt(getColumnIndex(cursor, DatabaseHelper.SORTING_ORDER_COLUMN)));

        // special handling for monitoring options as the specific monitorings within it should
        // hold a reference to the stock monitoring that owns it, and this reference is not
        // serializable which means we must set it manual
        StockMonitoring.MonitoringOptions monitoringOptions = gson.fromJson(
                cursor.getString(getColumnIndex(cursor, DatabaseHelper.MONITORING_OPTIONS_COLUMN)),
                StockMonitoring.MonitoringOptions.class);
        monitoringOptions.setStockMonitoring(stockMonitoring);

        // at last set monitoring options to stock monitoring
        stockMonitoring.setMonitoringOptions(monitoringOptions);

        return stockMonitoring;
    }

    private IndicatorCache getIndicatorCache(Cursor cursor) {
        IndicatorCache indicatorCache = new IndicatorCache(
                cursor.getInt(getColumnIndex(cursor, DatabaseHelper.ID_COLUMN)),
                cursor.getString(getColumnIndex(cursor, DatabaseHelper.TICKER_COLUMN)));
        indicatorCache.setSma(cursor.getDouble(getColumnIndex(cursor, DatabaseHelper.SMA_COLUMN)));
        indicatorCache.setEma(cursor.getDouble(getColumnIndex(cursor, DatabaseHelper.EMA_COLUMN)));
        indicatorCache.setRsi(cursor.getDouble(getColumnIndex(cursor, DatabaseHelper.RSI_COLUMN)));
        indicatorCache.setExpires(cursor.getLong(getColumnIndex(cursor, DatabaseHelper.EXPIRES_COLUMN)));

        return indicatorCache;
    }

    private int getColumnIndex(Cursor cursor, String column) {
        int columnIndex = cursor.getColumnIndex(column);
        if (columnIndex < 0) {
            throw new IllegalArgumentException(String.format("No column exists for given argument: %s", column));
        }
        return columnIndex;
    }
}
